//package com.practical.edumasters.fragments;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.practical.edumasters.R;
//import com.practical.edumasters.adapters.CommentAdapter;
//import com.practical.edumasters.models.CommunityComment;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link CommentFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class CommentFragment extends Fragment {
//    private RecyclerView recyclerComments;
//    private EditText inputComment;
//    private Button btnSendComment;
//    private TextView tvCommentContent;
//
//    private CommentAdapter adapter;
//    private List<CommunityComment> commentList = new ArrayList<>();
//    private FirebaseFirestore db;
//
//    private String postID;
//
//    public CommentFragment(String postID) {
//        this.postID = postID;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_comment, container, false);
//
//        recyclerComments = view.findViewById(R.id.recycler_comments);
//        inputComment = view.findViewById(R.id.input_comment);
//        btnSendComment = view.findViewById(R.id.btn_send_comment);
//
//        commentList = new ArrayList<>();
//        adapter = new CommentAdapter(getContext(), commentList);
//        recyclerComments.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerComments.setAdapter(adapter);
//
//        db = FirebaseFirestore.getInstance();
//
//        // Load comments from Firestore
//        loadComments();
//
//        // Handle sending new comment
//        btnSendComment.setOnClickListener(v -> {
//            String commentText = inputComment.getText().toString().trim();
//
//            if (!commentText.isEmpty()) {
//                addComment(commentText);
//                inputComment.setText(""); // Clear input field
//            }
//        });
//
//        return view;
//    }
//
//    private void loadComments() {
//        db.collection("comments")
//                .orderBy("timestamp", Query.Direction.ASCENDING)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        Comment comment = doc.toObject(Comment.class);
//                        commentList.add(comment);
//                    }
//                    adapter.notifyDataSetChanged();
//                });
//    }
//
//    private void sendComment() {
//        String content = inputComment.getText().toString().trim();
//        if (content.isEmpty()) return;
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//        String avatarUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
//        String timestamp = String.valueOf(System.currentTimeMillis());
//
//        Comment newComment = new Comment(userId, username, avatarUrl, content, timestamp);
//
//        db.collection("comments").add(newComment)
//                .addOnSuccessListener(documentReference -> {
//                    commentList.add(newComment);
//                    adapter.notifyItemInserted(commentList.size() - 1);
//                    inputComment.setText("");
//                });
//    }
//
//    private void addComment(String commentText) {
//        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        long timestamp = System.currentTimeMillis();
//
//        CommunityComment newComment = new CommunityComment(null, userID, commentText, timestamp, "Anonymous", ""); // You can replace with actual username/avatar
//
//        CollectionReference commentsRef = db.collection("community").document(postID).collection("comments");
//
//        commentsRef.add(newComment)
//                .addOnSuccessListener(documentReference -> Log.d(TAG, "Comment added"))
//                .addOnFailureListener(e -> Log.e(TAG, "Failed to add comment", e));
//
//    }
//}