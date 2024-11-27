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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.activities.MainActivity;
import com.practical.edumasters.adapters.ChapterAdapter;
import com.practical.edumasters.models.Chapter;
import com.practical.edumasters.models.Quiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonFragment extends Fragment {

    private FirebaseFirestore db;
    private static final String TAG = "LessonFragment";
    private TextView tvLessonTitle;
    private TextView tvLessonRatingText;
    private ImageView lessonImageView, btnBack;
    private RecyclerView chapterRecyclerView;
    private Button btnEnroll;

    private FirebaseAuth mAuth;
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
        mAuth = FirebaseAuth.getInstance();
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
        btnBack = rootView.findViewById(R.id.btnBack);  // Initialize btnBack

        chapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chapterAdapter = new ChapterAdapter(getContext(), contentList, chapter -> {
            Toast.makeText(getContext(), "Clicked: " + chapter.toString(), Toast.LENGTH_SHORT).show();
        });
        chapterRecyclerView.setAdapter(chapterAdapter);

        // Replace this with the actual lessonId you want to fetch
        String lessonId = "OwNi8xTsohGN7T8dc8Jh";
        loadLessonData(lessonId);

        btnEnroll.setOnClickListener(view -> {
            String userId = mAuth.getCurrentUser().getUid();  // Retrieve this dynamically for the current user

            enrollInLesson(userId,lessonId);
        });

        // Set the Back button listener
        btnBack.setOnClickListener(v -> navigateBackToLearnFragment());

        return rootView;
    }

    private void navigateBackToLearnFragment() {
        FragmentManager fragmentManager = getActivity() != null ? getActivity().getSupportFragmentManager() : null;
        if (fragmentManager != null) {
            // Replace current fragment with LearnFragment
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new LearnFragment()) // Replace fragment container with LearnFragment
                    .addToBackStack(null) // Add to back stack to enable navigation back
                    .commitAllowingStateLoss(); // Avoid state loss errors
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up your fragment (lesson details) view here

        // Handle back press explicitly to navigate to LearnFragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to LearnFragment
                getParentFragmentManager().popBackStack();
            }
        });
    }


    private void loadLessonData(String lessonId) {
        db.collection("total_lesson")
                .document(lessonId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        tvLessonTitle.setText(document.getString("title"));
                        tvLessonRatingText.setText(document.getString("rating"));
                        String pattern = document.getString("pattern");
                        loadContentInPattern(lessonId, pattern);
                    } else {
                        Log.e("LessonFragment", "Error fetching lesson", task.getException());
                        Toast.makeText(getContext(), "Failed to load lesson data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadContentInPattern(String lessonId, String pattern) {
        DocumentReference lessonRef = db.collection("total_lesson").document(lessonId);
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


    private void enrollInLesson(String userId, String lessonId) {
        // Create a reference to the current_lesson collection
        CollectionReference currentLessonRef = db.collection("current_lesson");

        // Create references to the user and lesson documents
        DocumentReference userRef = db.collection("users").document(userId);
        DocumentReference lessonRef = db.collection("total_lesson").document(lessonId);

        // Create the data to insert into current_lesson collection
        Map<String, Object> currentLessonData = new HashMap<>();
        currentLessonData.put("userId", userRef); // Reference to user
        currentLessonData.put("lessonId", lessonRef); // Reference to lesson
        currentLessonData.put("progress", "0"); // Reference to lesson

        // Add the data to Firestore
        currentLessonRef.add(currentLessonData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("EnrollLesson", "Enrolled successfully with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "Enrolled successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to LearnFragment (Home Page)
                    FragmentManager fragmentManager = getActivity() != null ? getActivity().getSupportFragmentManager() : null;
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, new LearnFragment()) // Replace fragment container with LearnFragment
                                .addToBackStack(null) // Add to back stack to enable navigation back
                                .commitAllowingStateLoss(); // Avoid state loss errors
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EnrollLesson", "Failed to enroll in lesson", e);
                    Toast.makeText(getContext(), "Enrollment failed!", Toast.LENGTH_SHORT).show();
                });
    }






}
