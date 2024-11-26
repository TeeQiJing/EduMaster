package com.practical.edumasters.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.practical.edumasters.R;
import com.practical.edumasters.models.Lesson;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private Context context;
    private List<Lesson> lessons;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    public LessonAdapter(Context context, List<Lesson> lessons, OnLessonClickListener listener) {
        this.context = context;
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);

        holder.title.setText(lesson.getTitle());

        // Set type icon
        switch (lesson.getType()) {
            case "text":
                holder.icon.setImageResource(R.mipmap.ic_text);
                break;
            case "video":
                holder.icon.setImageResource(R.mipmap.ic_video);
                break;
            case "quiz":
                holder.icon.setImageResource(R.mipmap.ic_quiz);
                break;
        }

        // Set unlock status
        if (!lesson.isUnlocked()) {
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setOnClickListener(null);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> listener.onLessonClick(lesson));
        }
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.lessonTitle);
            icon = itemView.findViewById(R.id.lessonIcon);
        }
    }
}
