package com.practical.edumasters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.practical.edumasters.R;
import com.practical.edumasters.models.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private Context context;
    private List<Chapter> chapters;
    private OnChapterClickListener listener;

    public interface OnChapterClickListener {
        void onChapterClick(Chapter chapter);
    }

    public ChapterAdapter(Context context, List<Chapter> chapters, OnChapterClickListener listener) {
        this.context = context;
        this.chapters = chapters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);

        holder.title.setText(chapter.getTitle());

        // Set the chapter type icon (video, text, quiz)
        switch (chapter.getType()) {
            case "video":
                holder.icon.setImageResource(R.mipmap.ic_video);
                break;
            case "text":
                holder.icon.setImageResource(R.mipmap.ic_text);
                break;
            case "quiz":
                holder.icon.setImageResource(R.mipmap.ic_quiz);
                break;
            default:
                holder.icon.setImageResource(R.mipmap.ic_launcher);
                break;
        }

        // Set unlock status
        if (!chapter.isUnlocked()) {
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setOnClickListener(null);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> listener.onChapterClick(chapter));
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chapterTitle);
            icon = itemView.findViewById(R.id.chapterIcon);
        }
    }
}
