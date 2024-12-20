package com.practical.edumasters.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.models.CommunityPost;

import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.PostViewHolder> {

    private List<CommunityPost> postList;

    private FirebaseFirestore db;
    private final Context context;

    public CommunityAdapter(List<CommunityPost> postList, FirebaseFirestore db, Context context) {
        this.postList = postList;
        this.db = db;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = postList.get(position);

        // Fetch and display user details
        post.fetchUserDetails(db, new CommunityPost.UserDetailsCallback() {
            @Override
            public void onSuccess(String username, String avatarUrl) {
                holder.username.setText(username);
                Glide.with(holder.avatar.getContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.gradient_background)
                        .into(holder.avatar);
            }

            @Override
            public void onFailure(Exception e) {
                holder.username.setText("Unknown User");
                holder.avatar.setImageResource(R.drawable.gradient_background);
            }
        });

        holder.postTime.setText("· " + post.getTimeAgo());
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getContent());
        holder.postLikes.setText(String.valueOf(post.getLikedBy().size()));
        holder.postComments.setText(String.valueOf(post.getNumOfComments()));

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Update like button UI
        updateLikeButtonUI(holder, post, currentUserID);

        // Handle like button click
//        holder.postLikes.setOnClickListener(v -> {
//            post.toggleLike(currentUserID, db, new CommunityPost.SaveCallback() {
//                @Override
//                public void onSuccess(String postId) {
//                    // Update the dataset and notify the adapter
//                    notifyItemChanged(holder.getAdapterPosition());
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    Toast.makeText(context, "Failed to update like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        });

        holder.postLikes.setOnClickListener(v -> {
            post.toggleLike(currentUserID, db, new CommunityPost.SaveCallback() {
                @Override
                public void onSuccess(String postId) {
                    // Notify adapter of the change
                    notifyItemChanged(holder.getAdapterPosition(), "likeChanged");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(context, "Failed to update like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.contains("likeChanged")) {
                CommunityPost post = postList.get(position);
                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                updateLikeButtonUI(holder, post, currentUserID);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }


    private void updateLikeButtonUI(PostViewHolder holder, CommunityPost post, String currentUserID) {
        boolean isLiked = post.getLikedBy().contains(currentUserID);

        if (isLiked) {
            //holder.postLikes.setBackgroundResource(R.drawable.like_button_liked); // Gradient background
            //holder.postLikes.setBackgroundTintList(null);
            holder.postLikes.setBackgroundColor(holder.postLikes.getContext().getResources().getColor(R.color.liked));
            holder.postLikes.setTextColor(ContextCompat.getColor(context, android.R.color.white)); // White text

//            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_like).mutate();
//            if (drawable != null) {
//                drawable.setColorFilter(ContextCompat.getColor(context, R.color.whiteIconTint), PorterDuff.Mode.SRC_IN);
//
//                // Set explicit bounds to preserve the drawable size
//                int iconSize = (int) holder.postLikes.getTextSize(); // Use text size or a fixed dimension
//                drawable.setBounds(0, 0, iconSize, iconSize);
//
//                // Apply the drawable to the button
//                holder.postLikes.setCompoundDrawables(drawable, null, null, null);
//            }
        } else {
            //holder.postLikes.setBackgroundResource(R.drawable.like_button_default); // Default tint
            //holder.postLikes.setBackgroundTintList(null);
            holder.postLikes.setBackgroundColor(holder.postLikes.getContext().getResources().getColor(R.color.unliked));
            holder.postLikes.setTextColor(ContextCompat.getColor(context, R.color.blackIconTint)); // Black text

//            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_like).mutate();
//            if (drawable != null) {
//                drawable.setColorFilter(ContextCompat.getColor(context, R.color.blackIconTint), PorterDuff.Mode.SRC_IN);
//
//                // Set explicit bounds to preserve the drawable size
//                int iconSize = (int) holder.postLikes.getTextSize(); // Use text size or a fixed dimension
//                drawable.setBounds(0, 0, iconSize, iconSize);
//
//                // Apply the drawable to the button
//                holder.postLikes.setCompoundDrawables(drawable, null, null, null);
//            }
        }

        holder.postLikes.setText(String.valueOf(post.getLikedBy().size())); // Update like count
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, postTime, postTitle, postContent;
        public Button postLikes;
        Button postComments;
        ImageView avatar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            postTime = itemView.findViewById(R.id.post_time);
            postTitle = itemView.findViewById(R.id.post_title);
            postContent = itemView.findViewById(R.id.post_content);
            postLikes = itemView.findViewById(R.id.post_likes);
            postComments = itemView.findViewById(R.id.post_comments);
            avatar = itemView.findViewById(R.id.post_user_avatar);
        }
    }

    public void updateList(List<CommunityPost> filteredPosts) {
        this.postList = filteredPosts;
        notifyDataSetChanged();
    }

}
