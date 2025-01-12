package com.practical.edumasters.fragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.CommentAdapter;
import com.practical.edumasters.models.CommunityComment;
import com.practical.edumasters.models.CommunityPost;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {
    private static final String TAG = "CommentFragment";
    private static final String ARG_POST_ID = "postID";
    private RecyclerView rvcomments;
    private CommentAdapter commentAdapter;
    private List<CommunityComment> commentList = new ArrayList<>();
    private FirebaseFirestore db;

    private CommunityPost post;
    private ImageView avatarPost,likeOverlayIcon,imagePost;
    private TextView usernamePost, timestampPost, titlePost, contentPost;
    private ImageButton btnBack, postDelete;
    private EditText inputComment;
    private Button btnSendComment,postLikes,postComments;
    private ConstraintLayout imageHolder;

    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(CommunityPost post) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putSerializable("post", post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (CommunityPost) getArguments().getSerializable("post");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        db = FirebaseFirestore.getInstance();

        // Set up upper post
        btnBack = view.findViewById(R.id.BtnBack);
        avatarPost = view.findViewById(R.id.post_user_avatar);
        usernamePost = view.findViewById(R.id.post_user_name);
        timestampPost = view.findViewById(R.id.post_time);
        titlePost = view.findViewById(R.id.post_title);
        contentPost = view.findViewById(R.id.post_content);
        postLikes = view.findViewById(R.id.post_likes);
        postComments = view.findViewById(R.id.post_comments);
        likeOverlayIcon = view.findViewById(R.id.ic_liked);
        imageHolder = view.findViewById(R.id.imageHolder);
        imagePost = view.findViewById(R.id.post_image);
        postDelete = view.findViewById(R.id.post_delete);

        loadUpperPost(post);
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Update like button UI
        updateLikeButtonUI(post, currentUserID);

        postLikes.setOnClickListener(v -> {
            post.toggleLike(currentUserID, db, new CommunityPost.SaveCallback() {
                @Override
                public void onSuccess(String postId) {
                    updateLikeButtonUI(post, currentUserID);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to update like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnBack.setOnClickListener(v -> {
            // Use FragmentManager to pop the back stack and go back to the previous fragment (CommunityFragment)
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        rvcomments = view.findViewById(R.id.recycler_comments);
        inputComment = view.findViewById(R.id.input_comment);
        btnSendComment = view.findViewById(R.id.btn_send_comment);

        commentAdapter = new CommentAdapter(commentList);
        rvcomments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvcomments.setAdapter(commentAdapter);

        // Load comments from Firestore
        if (post != null && post.getPostID() != null) {
            loadComments(post.getPostID());
        } else {
            Log.e(TAG, "Post is null or Post ID is missing.");
        }

        // Handle sending new comment
        btnSendComment.setOnClickListener(v -> {
            String commentText = inputComment.getText().toString().trim();

            if (commentText.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a comment", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(getContext(), "You need to be logged in to post a comment", Toast.LENGTH_SHORT).show();
                return;
            }

            String userID = currentUser.getUid();
            String username = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown User";
            String avatarURL = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";
            long timestamp = System.currentTimeMillis();

            CommunityComment comment = new CommunityComment(
                    null,  // Comment ID will be generated by Firestore
                    userID,
                    commentText,
                    timestamp
            );

            // Save the comment to Firestore
            comment.addComment(post.getPostID(), commentText, new CommunityComment.AddCommentCallback() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getContext(), "Comment added successfully!", Toast.LENGTH_SHORT).show();
                    inputComment.setText(""); // Clear input field
                    loadComments(post.getPostID()); // Refresh comments
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        return view;
    }

    private void loadComments(String postID) {
        if (postID == null) {
            Log.e(TAG, "Post ID is null. Cannot load comments.");
            return;
        }
        CollectionReference commentsRef = db.collection("community").document(postID).collection("comments");

//        commentsRef.orderBy("commentTimestamp", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    commentList.clear();
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        CommunityComment comment = doc.toObject(CommunityComment.class);
//                        if (comment != null) {
//                            commentList.add(comment);
//                        }
//                    }
//                    commentAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> Log.e(TAG, "Error loading comments", e));
        commentsRef.orderBy("commentTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error loading comments", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        commentList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            CommunityComment comment = doc.toObject(CommunityComment.class);
                            if (comment != null) {
                                commentList.add(comment);
                            }
                        }
                        commentAdapter.notifyDataSetChanged();

                        // Update the comment count in the UI
                        postComments.setText(String.valueOf(commentList.size()));
                    }
                });
    }

    private void loadUpperPost(CommunityPost post){
        db = FirebaseFirestore.getInstance();
        post.fetchUserDetails(db, new CommunityPost.UserDetailsCallback() {
            @Override
            public void onSuccess(String username, String avatarUrl) {
                usernamePost.setText(username);
                displayImage(avatarUrl, avatarPost);
            }

            @Override
            public void onFailure(Exception e) {
                usernamePost.setText("Unknown User");
                avatarPost.setImageResource(R.drawable.gradient_background);
            }
        });

        timestampPost.setText("· " + post.getTimeAgo());
        titlePost.setText(post.getTitle());
        contentPost.setText(post.getContent());
        postLikes.setText(String.valueOf(post.getLikedBy().size()));

        post.getCommentCount(db, new CommunityPost.FetchCommentCountCallback() {
            @Override
            public void onSuccess(int commentCount) {
                postComments.setText(String.valueOf(commentCount));
            }

            @Override
            public void onFailure(Exception e) {
                postComments.setText("0"); // Default to 0 if fetching fails
                Toast.makeText(getContext(), "Error loading comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (post.getImage()!=null){
            imageHolder.setVisibility(View.VISIBLE);
            displayImage(post.getImage(), imagePost);
        } else {
            imageHolder.setVisibility(View.GONE);
        }

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Handle delete button visibility and click
        if (currentUserID.equals(post.getUserID())) {
            postDelete.setVisibility(View.VISIBLE);
        } else {
            postDelete.setVisibility(View.GONE);
        }
        postDelete.setOnClickListener(v -> showPostDeleteConfirmationDialog(post));
    }

    private void displayImage(String avatarBase64, ImageView imageView) {
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                byte[] decodedBytes = android.util.Base64.decode(avatarBase64, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.gradient_background); // Fallback to default avatar
            }
        } else {
            imageView.setImageResource(R.drawable.gradient_background); // Fallback to default avatar
        }
    }

    private void updateLikeButtonUI(CommunityPost post, String currentUserID) {
        boolean isLiked = post.getLikedBy().contains(currentUserID);
        if (isLiked) {
            postLikes.setBackgroundColor(postLikes.getContext().getResources().getColor(R.color.liked));
            postLikes.setTextColor(ContextCompat.getColor(postLikes.getContext(), android.R.color.white)); // White text
            likeOverlayIcon.setVisibility(View.VISIBLE);
        } else {
            postLikes.setBackgroundColor(postLikes.getContext().getResources().getColor(R.color.unliked));
            postLikes.setTextColor(ContextCompat.getColor(postLikes.getContext(), R.color.blackIconTint)); // Black text
            likeOverlayIcon.setVisibility(View.GONE);
        }
        postLikes.setText(String.valueOf(post.getLikedBy().size())); // Update like count
    }

    private void showPostDeleteConfirmationDialog(CommunityPost post) {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_post_delete_confirmation, null);

        // Build the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Set up Cancel button
        Button cancelButton = dialogView.findViewById(R.id.dialog_post_delete_cancel);
        cancelButton.setOnClickListener(v -> dialog.dismiss()); // Dismiss dialog on cancel

        // Set up Delete button
        Button deleteButton = dialogView.findViewById(R.id.dialog_post_delete_confirmed);
        deleteButton.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss dialog
            db.collection("community").document(post.getPostID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Go back to the community page
                        requireActivity().getSupportFragmentManager().popBackStack();
                        Toast.makeText(getContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            dialog.dismiss(); // Dismiss dialog
        });
        // Show the dialog
        dialog.show();
    }

}