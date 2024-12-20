//package com.practical.edumasters.adapters;
//
//import android.content.Context;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.practical.edumasters.models.CommunityComment;
//
//import java.util.List;
//
//public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
//    private final Context context;
//    private final List<CommunityComment> comments;
//
//    // Constructor
//    public CommentAdapter(Context context, List<CommunityComment> comments) {
//        this.context = context;
//        this.comments = comments;
//    }
//
//    @NonNull
//    @Override
//    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
//        return new CommentViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
//        CommunityComment comment = comments.get(position);
//
//        holder.usernameTextView.setText(comment.getUsername());
//        holder.commentTextView.setText(comment.getComment());
//        holder.timeTextView.setText(comment.getTimeAgo());
//
//        // Load avatar using Glide
//        Glide.with(context)
//                .load(comment.getAvatarURL())
//                .placeholder(R.drawable.default_avatar) // Default avatar image
//                .into(holder.avatarImageView);
//    }
//
//    @Override
//    public int getItemCount() {
//        return comments.size();
//    }
//
//    static class CommentViewHolder extends RecyclerView.ViewHolder {
//        ImageView avatarImageView;
//        TextView usernameTextView, commentTextView, timeTextView;
//
//        public CommentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            avatarImageView = itemView.findViewById(R.id.avatarImageView);
//            usernameTextView = itemView.findViewById(R.id.usernameTextView);
//            commentTextView = itemView.findViewById(R.id.commentTextView);
//            timeTextView = itemView.findViewById(R.id.timeTextView);
//        }
//    }
//}