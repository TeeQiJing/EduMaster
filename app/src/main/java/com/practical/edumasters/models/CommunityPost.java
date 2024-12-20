package com.practical.edumasters.models;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.CommunityAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CommunityPost {
    private String postID;
    private String userID;
    private String title;
    private String content;
    private long timestamp;
    private List<String> likedBy;
    private int numOfComments;

    private String username;
    private String avatarURL;

    public CommunityPost(String userID, String title, String content, long timestamp, List<String> likedBy, int numOfComments) {
        this.userID = userID;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.likedBy = likedBy != null ? likedBy : new ArrayList<>();
        this.numOfComments = numOfComments;
    }

    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public List<String> getLikedBy() {
        return likedBy;
    }
    public int getNumOfComments() {
        return numOfComments;
    }
    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    // Save the CommunityPost to Firestore
    public void saveToFirebase(FirebaseFirestore db, SaveCallback callback) {
        Map<String, Object> post = new HashMap<>();
        post.put("userID", userID);
        post.put("title", title);
        post.put("content", content);
        post.put("timestamp", timestamp);
        post.put("likedBy", likedBy);
        post.put("numOfComments", numOfComments);

        db.collection("community")
                .add(post) // This adds the post and generates a unique document ID
                .addOnSuccessListener(documentReference -> {
                    this.postID = documentReference.getId(); // Set postID with the Firestore document ID
                    if (callback != null) callback.onSuccess(this.postID); // Provide the post ID through the callback
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e); // Handle any failures
                });
    }

    // Callback interface for Firebase operations
    public interface SaveCallback {
        void onSuccess(String postId);

        void onFailure(Exception e);
    }

    // Fetch username and avatar from the 'users' collection
    public void fetchUserDetails(FirebaseFirestore db, UserDetailsCallback callback) {
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

    // Callback interface for fetching user details
    public interface UserDetailsCallback {
        void onSuccess(String username, String avatarUrl);

        void onFailure(Exception e);
    }

    public void toggleLike(String userId, FirebaseFirestore db, SaveCallback callback) {
        if (likedBy.contains(userId)) {
            // Unlike the post
            likedBy.remove(userId);
        } else {
            // Like the post
            likedBy.add(userId);
        }

        db.collection("community").document(postID)
                .update("likedBy", likedBy)
                .addOnSuccessListener(unused -> {
                    if (callback != null) callback.onSuccess(postID);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostID(){
        return this.postID;
    }
}
