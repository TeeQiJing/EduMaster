package com.practical.edumasters.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.CommunityAdapter;
import com.practical.edumasters.models.CommunityPost;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class CommunityFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView postsRecyclerView;
    private CommunityAdapter adapter;
    private final List<CommunityPost> postList = new ArrayList<>();
    private ViewGroup rootView; // To manage the dim background

    private LinearLayoutManager layoutManager;
    private int lastKnownScrollPosition = 0; // To store the scroll position

    private static final int PICK_IMAGE_REQUEST = 1;
    private String imageToUpload = null; // Store the Base64 string of the image
    private ImageView noti;

    private static final String TAG = "CommunityFragment";

    public CommunityFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize rootView for dimmed background usage
        rootView = (ViewGroup) requireActivity().findViewById(android.R.id.content);

        // Setup RecyclerView
        postsRecyclerView = view.findViewById(R.id.posts_recycler_view);
        layoutManager = new LinearLayoutManager(getContext()); // Initialize layoutManager
        postsRecyclerView.setLayoutManager(layoutManager); // Set layout manager to RecyclerView
        adapter = new CommunityAdapter(postList, db, getContext());
        postsRecyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            lastKnownScrollPosition = savedInstanceState.getInt("scroll_position", 0);
        }
        // Set the RecyclerView's scroll position
        postsRecyclerView.scrollToPosition(lastKnownScrollPosition);

        // Load posts from Firestore
        loadPosts();

        // Setup Floating Action Button
        FloatingActionButton fabPost = view.findViewById(R.id.fab_post);
        fabPost.setOnClickListener(v -> showNewPostPopup());

        // Setup search functionality
        SearchView searchView = view.findViewById(R.id.search_post);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPosts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Reset the list when the search is cleared
                    adapter.updateList(new ArrayList<>(postList));
                } else {
                    filterPosts(newText);
                }
                return true;
            }
        });
    }

//    private void loadPosts() {
//        db.collection("community")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .addSnapshotListener((queryDocumentSnapshots, e) -> {
//                    if (e != null) {
//                        Toast.makeText(getContext(), "Error loading posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    if (queryDocumentSnapshots != null) {
//                        postList.clear();
//                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                            String postID = doc.getId();
//                            String userID = doc.getString("userID");
//                            String title = doc.getString("title");
//                            String content = doc.getString("content");
//                            long timestamp = parseTimestamp(doc.get("timestamp"));
//                            List<String> likedBy = (List<String>) doc.get("likedBy");
//                            String image = doc.getString("image");
//
//                            // Create a CommunityPost object
//                            CommunityPost post = new CommunityPost(userID, title, content, timestamp, likedBy, image);
//                            post.setPostID(postID);
//                            postList.add(post);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//}

    private void loadPosts() {
        db.collection("community")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error loading posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        postList.clear(); // Clear the list to avoid duplicates
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String postID = doc.getId();
                            String userID = doc.getString("userID");
                            String title = doc.getString("title");
                            String content = doc.getString("content");
                            long timestamp = parseTimestamp(doc.get("timestamp"));
                            List<String> likedBy = (List<String>) doc.get("likedBy");
                            String image = doc.getString("image");

                            // Create a CommunityPost object
                            CommunityPost post = new CommunityPost(userID, title, content, timestamp, likedBy, image);
                            post.setPostID(postID);
                            postList.add(post);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    private long parseTimestamp(Object timestampObj) {
        if (timestampObj instanceof Number) {
            return ((Number) timestampObj).longValue();
        } else if (timestampObj instanceof com.google.firebase.Timestamp) {
            return ((com.google.firebase.Timestamp) timestampObj).toDate().getTime();
        } else if (timestampObj instanceof String) {
            try {
                return Long.parseLong((String) timestampObj);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0; // Default fallback
    }

    private void showNewPostPopup() {
        // Inflate the popup layout
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_new_post, null);

        // Create the PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Create and add a dim background layer
        View dimBackgroundView = new View(getContext());
        dimBackgroundView.setBackgroundColor(requireContext().getColor(R.color.popup_background_dim));
        dimBackgroundView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootView.addView(dimBackgroundView);

        // Show the popup window at the center
        popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0);

        // Remove dim background on popup dismiss
        popupWindow.setOnDismissListener(() -> rootView.removeView(dimBackgroundView));

        // Initialize popup views
        EditText postTitle = popupView.findViewById(R.id.popup_post_title);
        EditText postContent = popupView.findViewById(R.id.popup_post_content);
        ImageButton exitButton = popupView.findViewById(R.id.popup_exit);
        Button postButton = popupView.findViewById(R.id.popup_post);
        Button uploadImageButton = popupView.findViewById(R.id.popup_upload);
        setGradientText(uploadImageButton);
        noti = popupView.findViewById(R.id.noti);

        uploadImageButton.setOnClickListener(view -> openImagePicker());

        // Exit button click listener
        exitButton.setOnClickListener(v -> popupWindow.dismiss());

        // Post button click listener
        postButton.setOnClickListener(v -> {
            String title = postTitle.getText().toString().trim();
            String content = postContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userID = currentUser != null ? currentUser.getUid() : "Unknown User";
            long timestamp = System.currentTimeMillis();

            CommunityPost post = new CommunityPost(userID, title, content, timestamp, new ArrayList<>(), imageToUpload);
//            post.saveToFirebase(db, new CommunityPost.SaveCallback() {
//                @Override
//                public void onSuccess(String postId) {
//                    Toast.makeText(getContext(), "Post added successfully!", Toast.LENGTH_SHORT).show();
//                    popupWindow.dismiss();
//
//                    rootView.removeView(dimBackgroundView);
//
//                    post.setPostID(postId);
//                    postsRecyclerView.scrollToPosition(0); // Scroll to the top to show the new post
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    Toast.makeText(getContext(), "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG,  e.getMessage());
//                }
//            });

            // Save post to Firebase
            post.saveToFirebase(db, new CommunityPost.SaveCallback() {
                @Override
                public void onSuccess(String postId) {
                    Toast.makeText(getContext(), "Post added successfully!", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                    rootView.removeView(dimBackgroundView);

                    post.setPostID(postId);
                    adapter.notifyItemInserted(0); // Notify adapter of the new item
                    postsRecyclerView.scrollToPosition(0); // Scroll to the top to show the new post
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to add post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                }
            });
        });
    }

    private void filterPosts(String query) {
        List<CommunityPost> filteredPosts = new ArrayList<>();
        for (CommunityPost post : postList) {
            String title = post.getTitle() != null ? post.getTitle().toLowerCase() : ""; // Default to empty string if null
            String content = post.getContent() != null ? post.getContent().toLowerCase() : ""; // Default to empty string if null
            String username = post.getUsername() != null ? post.getUsername().toLowerCase() : ""; // Default to empty string if null

            // Check if query matches any field
            if (title.contains(query.toLowerCase()) || content.contains(query.toLowerCase()) || username.contains(query.toLowerCase())) {
                filteredPosts.add(post);
            }
        }
        adapter.updateList(filteredPosts);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Ensure layoutManager is not null before calling findFirstVisibleItemPosition
        if (layoutManager != null) {
            lastKnownScrollPosition = layoutManager.findFirstVisibleItemPosition();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scroll_position", lastKnownScrollPosition);
    }

    private void setGradientText(Button button) {
        button.post(() -> {
            TextPaint paint = button.getPaint();

            // Define the width and height of the button
            float width = button.getWidth();
            float height = button.getHeight();

            // Create the LinearGradient with an angle of -45 degrees
            Shader textShader = new LinearGradient(
                    0, height,  // Start point (bottom-left)
                    width, 0,   // End point (top-right)
                    new int[]{
                            Color.parseColor("#7512FF"),
                            Color.parseColor("#3978FF"),
                            Color.parseColor("#94BBE9")
                    },
                    null,
                    Shader.TileMode.CLAMP
            );

            paint.setShader(textShader);
            button.invalidate(); // Refresh the button to apply the gradient
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Convert Bitmap to Base64
                imageToUpload = encodeImageToBase64(bitmap);
                noti.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Image selected", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to process the image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private String encodeImageToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//        byte[] byteArray = outputStream.toByteArray();
//        return Base64.encodeToString(byteArray, Base64.DEFAULT);
//    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}