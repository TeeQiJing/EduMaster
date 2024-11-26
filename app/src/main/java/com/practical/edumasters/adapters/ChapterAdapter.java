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
import com.practical.edumasters.models.Quiz;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Object> contentList; // List of both Chapters and Quizzes
    private final OnChapterClickListener chapterClickListener;

    // Define the view types for Chapter and Quiz
    private static final int TYPE_CHAPTER = 0;
    private static final int TYPE_QUIZ = 1;

    public interface OnChapterClickListener {
        void onChapterClick(Object chapterOrQuiz);
    }

    public ChapterAdapter(Context context, List<Object> contentList, OnChapterClickListener listener) {
        this.context = context;
        this.contentList = contentList;
        this.chapterClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        // Check the type of item and return corresponding view type
        if (contentList.get(position) instanceof Chapter) {
            return TYPE_CHAPTER;
        } else if (contentList.get(position) instanceof Quiz) {
            return TYPE_QUIZ;
        }
        return -1; // Default case (should never happen)
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        if (viewType == TYPE_CHAPTER) {
            return new ChapterViewHolder(view);
        } else {
            return new QuizViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChapterViewHolder) {
            Chapter chapter = (Chapter) contentList.get(position);
            ((ChapterViewHolder) holder).chapterTitle.setText(chapter.getTitle());
            ((ChapterViewHolder) holder).chapterIcon.setImageResource(R.drawable.ic_learn); // Chapter Icon
            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(chapter));
        } else if (holder instanceof QuizViewHolder) {
            Quiz quiz = (Quiz) contentList.get(position);
            ((QuizViewHolder) holder).chapterTitle.setText(quiz.getTitle());
            ((QuizViewHolder) holder).chapterIcon.setImageResource(R.drawable.ic_quiz); // Quiz Icon
            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(quiz));
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;
        ImageView chapterIcon;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.chapterTitle);
            chapterIcon = itemView.findViewById(R.id.chapterIcon);
        }
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;
        ImageView chapterIcon;

        public QuizViewHolder(View itemView) {
            super(itemView);
            chapterTitle = itemView.findViewById(R.id.chapterTitle);
            chapterIcon = itemView.findViewById(R.id.chapterIcon);
        }
    }
}
