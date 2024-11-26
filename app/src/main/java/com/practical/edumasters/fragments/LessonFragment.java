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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.ChapterAdapter;
import com.practical.edumasters.models.Chapter;
import com.practical.edumasters.models.Quiz;

import java.util.ArrayList;
import java.util.List;

public class LessonFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView tvLessonTitle;
    private TextView tvLessonRatingText;
    private ImageView lessonImageView;
    private RecyclerView chapterRecyclerView;
    private ChapterAdapter chapterAdapter;
    private List<Object> contentList; // List to hold chapters and quizzes

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
        View rootView = inflater.inflate(R.layout.fragment_lesson, container, false);

        tvLessonTitle = rootView.findViewById(R.id.lessonTitle);
        tvLessonRatingText = rootView.findViewById(R.id.lessonRatingText);
        lessonImageView = rootView.findViewById(R.id.lessonImage);
        chapterRecyclerView = rootView.findViewById(R.id.chapterRecycleView);

        chapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chapterAdapter = new ChapterAdapter(getContext(), contentList, chapter -> {
            // Handle click event if needed
        });
        chapterRecyclerView.setAdapter(chapterAdapter);

        // Replace this with the actual lessonId you want to fetch
        String lessonId = "F0TVbpeNRWVtUVOcAAx2";
        loadLessonData(lessonId);

        return rootView;
    }

    private void loadLessonData(String lessonId) {
        db.collection("lessons")
                .document(lessonId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        tvLessonTitle.setText(document.getString("title"));
                        tvLessonRatingText.setText(String.valueOf(document.getDouble("rating")));
                        String pattern = document.getString("pattern");
                        loadContentInPattern(lessonId, pattern);
                    } else {
                        Log.e("LessonFragment", "Error fetching lesson", task.getException());
                        Toast.makeText(getContext(), "Failed to load lesson data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadContentInPattern(String lessonId, String pattern) {
        DocumentReference lessonRef = db.collection("lessons").document(lessonId);
        Log.d("LessonFragment", "Lesson reference: " + lessonRef.getPath());  // Log the reference

        List<Chapter> chapters = new ArrayList<>();
        List<Quiz> quizzes = new ArrayList<>();

        // Fetch Chapters
        db.collection("chapters")
                .whereEqualTo("ref", lessonRef) // Use DocumentReference here
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LessonFragment", "Chapters fetched: " + task.getResult().size());
                        for (DocumentSnapshot doc : task.getResult()) {
                            Log.d("LessonFragment", "Chapter document: " + doc.getData()); // Log the chapter document data
                            chapters.add(Chapter.fromSnapshot(doc));
                        }
                        if (!quizzes.isEmpty()) {
                            arrangeContentInPattern(chapters, quizzes, pattern);
                        } else {
                            Log.d("LessonFragment", "Chapters is empty after fetching.");
                        }
                    } else {
                        Log.e("LessonFragment", "Error fetching chapters", task.getException());
                    }
                });

        // Fetch Quizzes
        db.collection("quiz")
                .whereEqualTo("ref", lessonRef) // Use DocumentReference here
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LessonFragment", "Quizzes fetched: " + task.getResult().size());
                        for (DocumentSnapshot doc : task.getResult()) {
                            Log.d("LessonFragment", "Quiz document: " + doc.getData());  // Log the quiz document data
                            quizzes.add(Quiz.fromSnapshot(doc));
                        }
                        if (!chapters.isEmpty()) {
                            arrangeContentInPattern(chapters, quizzes, pattern);
                        } else {
                            Log.d("LessonFragment", "Quizzes is empty after fetching.");
                        }
                    } else {
                        Log.e("LessonFragment", "Error fetching quizzes", task.getException());
                    }
                });
    }

    private void arrangeContentInPattern(List<Chapter> chapters, List<Quiz> quizzes, String pattern) {
        // Ensure pattern is not null or empty
        if (pattern == null || pattern.isEmpty()) {
            Log.e("LessonFragment", "Invalid pattern: " + pattern);
            return;
        }

        int chapterIndex = 0, quizIndex = 0;

        for (char type : pattern.toCharArray()) {
            if (type == 'C' && chapterIndex < chapters.size()) {
                contentList.add(chapters.get(chapterIndex++));
            } else if (type == 'Q' && quizIndex < quizzes.size()) {
                contentList.add(quizzes.get(quizIndex++));
            }
        }

        chapterAdapter.notifyDataSetChanged();
    }

}
