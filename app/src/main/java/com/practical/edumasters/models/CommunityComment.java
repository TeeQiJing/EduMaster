package com.practical.edumasters.models;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityComment {
    private String commentID;
    private String userID;
    private String comment;
    private long timestamp;
    private String username;
    private String avatarURL;

    public CommunityComment() {}

    public CommunityComment(String commentID, String userID, String comment, long timestamp, String username, String avatarURL) {
        this.commentID = commentID;
        this.userID = userID;
        this.comment = comment;
        this.timestamp = timestamp;
        this.username = username;
        this.avatarURL = avatarURL;
    }

    public String getCommentID() { return commentID; }
    public void setCommentID(String commentID) { this.commentID = commentID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAvatarURL() { return avatarURL; }
    public void setAvatarURL(String avatarURL) { this.avatarURL = avatarURL; }

    private void addComment(String postID, String commentText) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a comment object
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String, Object> comment = new HashMap<>();
        comment.put("commentUserID", userID);
        comment.put("commentContent", commentText);
        comment.put("commentTimestamp", timestamp);

        // Add the comment to the "comments" subcollection of the specified post
        db.collection("community").document(postID)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    this.commentID = documentReference.getId();
                })
                .addOnFailureListener(e -> {
                    Log.e("AddComment", "Error adding comment", e);
                });
    }

    // Fetch username and avatar from the 'users' collection
    public void fetchUserDetails(FirebaseFirestore db, CommunityComment.UserDetailsCallback callback) {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        this.username = documentSnapshot.getString("username");
                        this.avatarURL = documentSnapshot.getString("avatar");
                        if (callback != null) callback.onSuccess(username, avatarURL);
                    } else {
                        if (callback != null) callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Callback interface for fetching user details
    public interface UserDetailsCallback {
        void onSuccess(String username, String avatarUrl);

        void onFailure(Exception e);
    }

    // Calculate how long ago the post was created
    public String getTimeAgo() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - timestamp;

        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (years > 0) return years + (years == 1 ? " year ago" : " years ago");
        if (months > 0) return months + (months == 1 ? " month ago" : " months ago");
        if (weeks > 0) return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        if (days > 0) return days + (days == 1 ? " day ago" : " days ago");
        if (hours > 0) return hours + (hours == 1 ? " hour ago" : " hours ago");
        if (minutes > 0) return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        return seconds + (seconds == 1 ? " second ago" : " seconds ago");
    }
}
