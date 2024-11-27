package com.practical.edumasters.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private static final String TAG = "LessonFragment";
    private TextView tvLessonTitle;
    private TextView tvLessonRatingText;
    private ImageView lessonImageView;
    private RecyclerView chapterRecyclerView;
    private Button btnEnroll;
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
        btnEnroll = rootView.findViewById(R.id.btnEnroll);

        chapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chapterAdapter = new ChapterAdapter(getContext(), contentList, chapter -> {
            Toast.makeText(getContext(), "Clicked: " + chapter.toString(), Toast.LENGTH_SHORT).show();
        });
        chapterRecyclerView.setAdapter(chapterAdapter);

        // Replace this with the actual lessonId you want to fetch
        String lessonId = "F0TVbpeNRWVtUVOcAAx2";
        loadLessonData(lessonId);
        btnEnroll.setOnClickListener(view -> {
            String userId = "gykOwUi0wQZrznz7TanZQ1kuswo1";  // Retrieve this dynamically for the current user
            int lessonIndex = 0;  // For example, lesson 1 (you can adjust this based on the current lesson)

            // Mark the lesson as completed when the user clicks the button
            markLessonAsCompleted(userId, lessonIndex);
        });

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

    private void markLessonAsCompleted(String userId, int lessonIndex) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Object> courseList = (List<Object>) documentSnapshot.get("courses");

                        if (courseList != null && lessonIndex * 2 + 1 < courseList.size()) {
                            // Update the completion status to true
                            courseList.set(lessonIndex * 2 + 1, true);

                            // Write the updated array back to Firestore
                            db.collection("users").document(userId)
                                    .update("courses", courseList)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("LessonUpdate", "Lesson " + lessonIndex + " marked as completed.");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("LessonUpdate", "Failed to update lesson completion", e);
                                    });
                        } else {
                            Log.e("LessonUpdate", "Invalid lesson index or course data is null.");
                        }
                    } else {
                        Log.e("LessonUpdate", "User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> Log.e("LessonUpdate", "Failed to fetch user data", e));
    }



}
