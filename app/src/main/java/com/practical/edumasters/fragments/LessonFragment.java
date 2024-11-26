package com.practical.edumasters.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.ChapterAdapter;
import com.practical.edumasters.models.Chapter;
import com.practical.edumasters.models.Quiz;
import com.practical.edumasters.models.Lesson;

import java.util.ArrayList;
import java.util.List;

public class LessonFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView tvLessonTitle;
    private TextView tvLessonRatingText;
    private ImageView lessonImageView;
    private RecyclerView chapterRecyclerView;
    private ChapterAdapter chapterAdapter;
    private List<Object> contentList; // List that will hold both Chapters and Quizzes

    public LessonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        contentList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            View rootView = inflater.inflate(R.layout.fragment_lesson, container, false);
            tvLessonTitle = rootView.findViewById(R.id.lessonTitle);
            tvLessonRatingText = rootView.findViewById(R.id.lessonRatingText);
            lessonImageView = rootView.findViewById(R.id.lessonImage);
            chapterRecyclerView = rootView.findViewById(R.id.chapterRecycleView);

            chapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chapterAdapter = new ChapterAdapter(getContext(), contentList, chapter -> {
                // Handle click
            });
            chapterRecyclerView.setAdapter(chapterAdapter);

            loadLessonData("F0TVbpeNRWVtUVOcAAx2");

            return rootView;
        } catch (Exception e) {
            Log.e("LessonFragment", "Error in onCreateView", e);
            return null; // Or show an error UI
        }
    }


    private void loadLessonData(String lessonId) {
        DocumentReference lessonRef = db.collection("lessons").document(lessonId);

        lessonRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Lesson lesson = documentSnapshot.toObject(Lesson.class);
                    if (lesson != null) {
                        tvLessonTitle.setText(lesson.getTitle());
                        tvLessonRatingText.setText(String.valueOf(lesson.getRating()));

                        // Fetch the pattern from the lesson data
                        String pattern = lesson.getPattern();
                        if (pattern != null) {
                            loadContentInPattern(lessonId, pattern);
                        }
                    } else {
                        Log.e("LessonFragment", "Lesson data is null.");
                    }
                }
            } else {
                Log.e("LessonFragment", "Failed to load lesson data: " + task.getException());
                Toast.makeText(getContext(), "Failed to load lesson data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadContentInPattern(String lessonId, String pattern) {
        List<Chapter> chapters = new ArrayList<>();
        List<Quiz> quizzes = new ArrayList<>();

        // Fetch chapters
        db.collection("lessons")
                .document(lessonId)
                .collection("chapters")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Chapter chapter = documentSnapshot.toObject(Chapter.class);
                            if (chapter != null) {
                                chapters.add(chapter);
                            }
                        }
                        // After fetching chapters, fetch quizzes
                        loadQuizzesAndArrangeContent(chapters, quizzes, pattern);
                    } else {
                        Log.e("LessonFragment", "Failed to fetch chapters: " + task.getException());
                    }
                });

        // Fetch quizzes
        db.collection("lessons")
                .document(lessonId)
                .collection("quizzes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            Quiz quiz = documentSnapshot.toObject(Quiz.class);
                            if (quiz != null) {
                                quizzes.add(quiz);
                            }
                        }
                        // After fetching quizzes, arrange content
                        loadQuizzesAndArrangeContent(chapters, quizzes, pattern);
                    } else {
                        Log.e("LessonFragment", "Failed to fetch quizzes: " + task.getException());
                    }
                });
    }


    private void loadQuizzesAndArrangeContent(List<Chapter> chapters, List<Quiz> quizzes, String pattern) {
        int chapterIndex = 0;
        int quizIndex = 0;

        // Step 2: Arrange content based on the pattern
        for (int i = 0; i < pattern.length(); i++) {
            char type = pattern.charAt(i);
            if (type == 'C' && chapterIndex < chapters.size()) {
                contentList.add(chapters.get(chapterIndex++));
            } else if (type == 'Q' && quizIndex < quizzes.size()) {
                contentList.add(quizzes.get(quizIndex++));
            }
        }

        // Notify the adapter to update the RecyclerView
        chapterAdapter.notifyDataSetChanged();
    }
}
