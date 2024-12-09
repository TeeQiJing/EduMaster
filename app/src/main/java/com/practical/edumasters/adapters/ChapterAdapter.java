//package com.practical.edumasters.adapters;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.practical.edumasters.R;
//import com.practical.edumasters.models.Chapter;
//import com.practical.edumasters.models.Quiz;
//
//import java.util.List;
//
//public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private final Context context;
//    private final List<Object> contentList; // List of both Chapters and Quizzes
//    private final OnChapterClickListener chapterClickListener;
//
//    // Define the view types for Chapter and Quiz
//    private static final int TYPE_CHAPTER = 0;
//    private static final int TYPE_QUIZ = 1;
//
//    public interface OnChapterClickListener {
//        void onChapterClick(Object chapterOrQuiz);
//
//    }
//
//
//
//    public ChapterAdapter(Context context, List<Object> contentList, OnChapterClickListener listener) {
//        this.context = context;
//        this.contentList = contentList;
//        this.chapterClickListener = listener;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // Check the type of item and return corresponding view type
//        if (contentList.get(position) instanceof Chapter) {
//            return TYPE_CHAPTER;
//        } else if (contentList.get(position) instanceof Quiz) {
//            return TYPE_QUIZ;
//        }
//        return -1; // Default case (should never happen)
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
//        if (viewType == TYPE_CHAPTER) {
//            return new ChapterViewHolder(view);
//        } else {
//            return new QuizViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof ChapterViewHolder) {
//            Chapter chapter = (Chapter) contentList.get(position);
//            ((ChapterViewHolder) holder).chapterTitle.setText(chapter.getTitle());
//            ((ChapterViewHolder) holder).chapterIcon.setImageResource(R.drawable.ic_chapter); // Chapter Icon
//            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(chapter));
//            Log.d("ChapterAdapter", "Chapter Binding view for position: " + position);
//
//
//        } else if (holder instanceof QuizViewHolder) {
//            Log.d("ChapterAdapter", "Quiz Binding view for position: " + position);
//
//            Quiz quiz = (Quiz) contentList.get(position);
//            ((QuizViewHolder) holder).chapterTitle.setText(quiz.getTitle());
//            ((QuizViewHolder) holder).chapterIcon.setImageResource(R.drawable.ic_quiz); // Quiz Icon
//            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(quiz));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return contentList.size();
//    }
//
//    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
//        TextView chapterTitle;
//        ImageView chapterIcon;
//
//        public ChapterViewHolder(View itemView) {
//            super(itemView);
//            chapterTitle = itemView.findViewById(R.id.chapterTitle);
//            chapterIcon = itemView.findViewById(R.id.chapterIcon);
//        }
//    }
//
//    public static class QuizViewHolder extends RecyclerView.ViewHolder {
//        TextView chapterTitle;
//        ImageView chapterIcon;
//
//        public QuizViewHolder(View itemView) {
//            super(itemView);
//            chapterTitle = itemView.findViewById(R.id.chapterTitle);
//            chapterIcon = itemView.findViewById(R.id.chapterIcon);
//        }
//    }
//}

package com.practical.edumasters.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.practical.edumasters.R;
import com.practical.edumasters.fragments.LessonFragment;
import com.practical.edumasters.models.Chapter;
import com.practical.edumasters.models.CurrentLessonCard;
import com.practical.edumasters.models.Quiz;

import java.util.List;
import java.util.Map;

//public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private final Context context;
//    private final List<Object> contentList; // List containing Chapters and Quizzes
//    private final OnChapterClickListener chapterClickListener;
//
//    // Define constants for view types
//    private static final int TYPE_CHAPTER = 0;
//    private static final int TYPE_QUIZ = 1;
//
//    // Interface for handling item click events
//    public interface OnChapterClickListener {
//        void onChapterClick(Object chapterOrQuiz);
//    }
//
//    // Constructor to initialize context, content list, and click listener
//    public ChapterAdapter(Context context, List<Object> contentList, OnChapterClickListener listener) {
//        this.context = context;
//        this.contentList = contentList;
//        this.chapterClickListener = listener;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // Return the view type based on the item type
//        if (contentList.get(position) instanceof Chapter) {
//            return TYPE_CHAPTER;
//        } else if (contentList.get(position) instanceof Quiz) {
//            return TYPE_QUIZ;
//        }
//        return -1; // Invalid case
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        // Inflate the appropriate layout based on the view type
//        if (viewType == TYPE_CHAPTER) {
//            View view = inflater.inflate(R.layout.item_chapter, parent, false);
//            return new ChapterViewHolder(view);
//        } else { // TYPE_QUIZ
//            View view = inflater.inflate(R.layout.item_chapter, parent, false); // Assuming same layout is used
//            return new QuizViewHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Object item = contentList.get(position);
//
//        if (holder instanceof ChapterViewHolder) {
//            Chapter chapter = (Chapter) item;
//            ((ChapterViewHolder) holder).bind(chapter);
//            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(chapter));
//            Log.d("ChapterAdapter", "Bound Chapter at position: " + position);
//        } else if (holder instanceof QuizViewHolder) {
//            Quiz quiz = (Quiz) item;
//            ((QuizViewHolder) holder).bind(quiz);
//            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(quiz));
//            Log.d("ChapterAdapter", "Bound Quiz at position: " + position);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return contentList.size();
//    }
//
//    // ViewHolder for Chapter items
//    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
//        private final TextView title;
//        private final ImageView icon;
//
//        public ChapterViewHolder(View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.chapterTitle);
//            icon = itemView.findViewById(R.id.chapterIcon);
//        }
//
//        public void bind(Chapter chapter) {
//            title.setText(chapter.getTitle());
//            icon.setImageResource(R.drawable.ic_chapter); // Use a default icon for chapters
//        }
//
//    }
//
//    // ViewHolder for Quiz items
//    public static class QuizViewHolder extends RecyclerView.ViewHolder {
//        private final TextView title;
//        private final ImageView icon;
//
//        public QuizViewHolder(View itemView) {
//            super(itemView);
//            title = itemView.findViewById(R.id.chapterTitle);
//            icon = itemView.findViewById(R.id.chapterIcon);
//        }
//
//        public void bind(Quiz quiz) {
//            title.setText(quiz.getTitle());
//            icon.setImageResource(R.drawable.ic_quiz); // Use a default icon for quizzes
//        }
//    }
//
//}

public class ChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Object> contentList; // List containing Chapter and Quiz objects
    private final OnChapterClickListener chapterClickListener;

    // Constants for view types
    private static final int TYPE_CHAPTER = 0;
    private static final int TYPE_QUIZ = 1;

    // Listener interface for handling item click events
    public interface OnChapterClickListener {
        void onChapterClick(Object chapterOrQuiz);
    }

    // Constructor to initialize context, content list, and click listener
    private Map<String, Boolean> completionStateMap;

    public ChapterAdapter(Context context, List<Object> contentList, Map<String, Boolean> completionStateMap, OnChapterClickListener listener) {
        this.context = context;
        this.contentList = contentList;
        this.completionStateMap = completionStateMap;
        this.chapterClickListener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        // Determine the item type (Chapter or Quiz) based on the object's class
        if (contentList.get(position) instanceof Chapter) {
            return TYPE_CHAPTER;
        } else if (contentList.get(position) instanceof Quiz) {
            return TYPE_QUIZ;
        }
        return -1; // Invalid type
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_CHAPTER) {
            // Inflate layout for Chapter items
            View view = inflater.inflate(R.layout.item_chapter, parent, false);
            return new ChapterViewHolder(view);
        } else { // TYPE_QUIZ
            // Inflate layout for Quiz items
            View view = inflater.inflate(R.layout.item_chapter, parent, false); // Separate layout for quizzes
            return new QuizViewHolder(view);
        }
    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Object item = contentList.get(position); // Get the current item
//
//        if (holder instanceof ChapterViewHolder) {
//            // Bind Chapter data
//            Chapter chapter = (Chapter) item;
//            ((ChapterViewHolder) holder).bind(chapter);
//            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(chapter));
//            Log.d("ChapterAdapter", "Bound Chapter at position: " + position);
//        } else if (holder instanceof QuizViewHolder) {
//            // Bind Quiz data
//            Quiz quiz = (Quiz) item;
//            ((QuizViewHolder) holder).bind(quiz);
//            holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(quiz));
//            Log.d("ChapterAdapter", "Bound Quiz at position: " + position);
//        }
//    }
@Override
public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    Object item = contentList.get(position);
    boolean isCompleted = false;

    if (item instanceof Chapter) {
        Chapter chapter = (Chapter) item;
        isCompleted = completionStateMap.getOrDefault(chapter.getId(), false);
        ((ChapterViewHolder) holder).bind(chapter, isCompleted);
    } else if (item instanceof Quiz) {
        Quiz quiz = (Quiz) item;
        isCompleted = completionStateMap.getOrDefault(quiz.getId(), false);
        ((QuizViewHolder) holder).bind(quiz, isCompleted);
    }

    holder.itemView.setOnClickListener(v -> chapterClickListener.onChapterClick(item));
}


    @Override
    public int getItemCount() {
        return contentList.size(); // Total number of items in the list
    }

    // ViewHolder class for Chapter items
    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView icon;

        private FrameLayout chapterIconContainer;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chapterTitle);
            icon = itemView.findViewById(R.id.chapterIcon);
            chapterIconContainer = itemView.findViewById(R.id.chapterIconContainer);
        }

        public void bind(Chapter chapter, boolean isCompleted) {
            title.setText(chapter.getTitle());
            // Set background of the container based on completion status
            chapterIconContainer.setBackgroundResource(isCompleted ? R.drawable.circle_background : R.drawable.circle_background_inactive);
            icon.setImageResource(R.drawable.ic_word); // The icon remains unchanged, only the background of the container changes
        }
    }

    // ViewHolder class for Quiz items
    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView icon;
        private final FrameLayout chapterIconContainer;

        public QuizViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chapterTitle);
            icon = itemView.findViewById(R.id.chapterIcon);
            chapterIconContainer = itemView.findViewById(R.id.chapterIconContainer);
        }

        public void bind(Quiz quiz, boolean isCompleted) {
            title.setText(quiz.getTitle());
            // Set background of the container based on completion status
            chapterIconContainer.setBackgroundResource(isCompleted ? R.drawable.circle_background : R.drawable.circle_background_inactive);
            icon.setImageResource(R.drawable.ic_bulb); // The icon remains unchanged, only the background of the container changes
        }
    }
}


