

package com.practical.edumasters.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.ChapterAdapter;
import com.practical.edumasters.models.Chapter;
import com.practical.edumasters.models.Quiz;
import com.practical.edumasters.models.User;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LessonFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "LessonFragment";


    private TextView tvLessonTitle;
    private TextView tvLessonRatingText;

    private ConstraintLayout CLbtn;
    private ImageView lessonImageView, btnFav;
    private RecyclerView chapterRecyclerView;
    private Button btnEnroll;

    private ChapterAdapter chapterAdapter;
    private List<Object> contentList; // List to hold chapters and quizzes
    private String lessonId;
    private boolean isEnrolled = false; // Flag to check enrollment status
    private boolean isFavorited = false;

    public LessonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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
        btnFav = rootView.findViewById(R.id.btnFav);

        CLbtn = rootView.findViewById(R.id.CLbtn);
        MaterialToolbar toolbar = rootView.findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        btnEnroll.setVisibility(View.GONE);



        chapterRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chapterAdapter = new ChapterAdapter(getContext(), contentList, item -> {
            if (!isEnrolled) {
                Toast.makeText(getContext(), "Please enroll in the lesson first.", Toast.LENGTH_SHORT).show();
            } else {
                if (item instanceof Chapter) {  // Check if the item is a Chapter
                    // Handle chapter click
                    Chapter selectedChapter = (Chapter) item;
                    String chapterId = selectedChapter.getId();  // Get the chapter ID
                    if (chapterId != null && !chapterId.isEmpty()) {
                        ContentFragment contentFragment = new ContentFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("chapterId", chapterId);  // Pass the chapter ID
                        bundle.putString("chapterTitle", selectedChapter.getTitle());  // Pass the chapter Title
                        contentFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity() != null ? getActivity().getSupportFragmentManager() : null;
                        if (fragmentManager != null) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.slide_in_right,  // Animation for fragment entry
                                            R.anim.slide_out_left, // Animation for fragment exit
                                            R.anim.slide_in_left,  // Animation for returning to the fragment
                                            R.anim.slide_out_right // Animation for exiting back
                                    )
                                    .replace(R.id.fragment_container, contentFragment)
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss();
                        }
                    } else {
                        Toast.makeText(getContext(), "Chapter content is empty", Toast.LENGTH_SHORT).show();
                    }
                } else if (item instanceof Quiz) {  // Check if the item is a Quiz
                    // Handle quiz click
                    Quiz selectedQuiz = (Quiz) item;
                    String quizTitle = selectedQuiz.getTitle();  // Get the quiz title
                    if (quizTitle != null && !quizTitle.isEmpty()) {
                        QuizFragment quizFragment = new QuizFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("quizTitle", quizTitle);  // Pass the quiz title
                        bundle.putString("quizId", selectedQuiz.getId());  // Pass the quiz ID (if you need it)
                        quizFragment.setArguments(bundle);
                        FragmentManager fragmentManager = getActivity() != null ? getActivity().getSupportFragmentManager() : null;
                        if (fragmentManager != null) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.slide_in_right,  // Animation for fragment entry
                                            R.anim.slide_out_left, // Animation for fragment exit
                                            R.anim.slide_in_left,  // Animation for returning to the fragment
                                            R.anim.slide_out_right // Animation for exiting back
                                    )
                                    .replace(R.id.fragment_container, quizFragment)
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss();
                        }
                    } else {
                        Toast.makeText(getContext(), "Quiz content is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Unknown item type", Toast.LENGTH_SHORT).show();
                }
            }
        });



        chapterRecyclerView.setAdapter(chapterAdapter);

        lessonId = getArguments().getString("lessonId");
        loadLessonData(lessonId);
        checkEnrollmentStatus();

        btnEnroll.setOnClickListener(view -> {
            String userId = mAuth.getCurrentUser().getUid();
            enrollInLesson(userId, lessonId);
        });



        btnFav.setOnClickListener(v -> {
            // Load the click animation
            Animation clickAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_click_animation);

            // Start the animation
            v.startAnimation(clickAnimation);

            // Toggle favorite status
            if (isFavorited) {
                // Handle unfavorite
                isFavorited = false;
                btnFav.setImageResource(R.drawable.ic_love); // Replace with "not favorite" icon
            } else {
                // Handle favorite
                isFavorited = true;
                btnFav.setImageResource(R.drawable.ic_love_filled); // Replace with "favorite" icon
            }

            // Additional logic (like saving the state or updating the UI can go here)
        });



        return rootView;
    }

    private void checkEnrollmentStatus() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("current_lesson")
                .whereEqualTo("userId", db.collection("users").document(userId))
                .whereEqualTo("lessonId", db.collection("total_lesson").document(lessonId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        isEnrolled = true;
                        btnEnroll.setVisibility(View.GONE);
                        CLbtn.setVisibility(View.GONE);
                    } else {
                        isEnrolled = false;
                        btnEnroll.setVisibility(View.VISIBLE);
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
                        loadLessonImage(Objects.requireNonNull(document.getString("title")));
                        loadContentInPattern(lessonId, pattern);
                    } else {
                        Log.e(TAG, "Error fetching lesson", task.getException());
                        Toast.makeText(getContext(), "Failed to load lesson data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadLessonImage(String title){
        switch (title){
            case "GitHub":
                lessonImageView.setImageResource(R.drawable.ic_github);
                break;
            case "Java":
                lessonImageView.setImageResource(R.drawable.ic_java);
                break;
            case "HTML & CSS":
                lessonImageView.setImageResource(R.drawable.ic_html);
                break;

            case "Python":
                lessonImageView.setImageResource(R.drawable.ic_python);
                break;
            case "UI UX Design":
                lessonImageView.setImageResource(R.drawable.ic_uiux);
                break;
            default:
                lessonImageView.setImageResource(R.drawable.ic_avatar);
        }
    }


private void loadContentInPattern(String lessonId, String pattern) {
    DocumentReference lessonRef = db.collection("total_lesson").document(lessonId);

    List<Chapter> chapters = new ArrayList<>();
    List<Quiz> quizzes = new ArrayList<>();

    // Counter to track completed tasks
    AtomicInteger tasksCompleted = new AtomicInteger(0);
    int totalTasks = 2; // Number of tasks to complete (chapters + quizzes)

    // Define a common callback for when data is ready
    Runnable onDataReady = () -> {
        if (tasksCompleted.incrementAndGet() == totalTasks) {
            arrangeContentInPattern(chapters, quizzes, pattern);
        }
    };

    // Fetch chapters
    db.collection("chapters")
            .whereEqualTo("ref", lessonRef)
            .orderBy("groupId")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        chapters.add(Chapter.fromSnapshot(doc));
                    }
                } else {
                    Log.e(TAG, "Error fetching chapters", task.getException());
                }
                onDataReady.run(); // Notify that chapters are ready
            });

    // Fetch quizzes
    db.collection("quiz")
            .whereEqualTo("ref", lessonRef)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        quizzes.add(Quiz.fromSnapshot(doc));
                    }
                } else {
                    Log.e(TAG, "Error fetching quizzes", task.getException());
                }
                onDataReady.run(); // Notify that quizzes are ready
            });
}

    private void arrangeContentInPattern(List<Chapter> chapters, List<Quiz> quizzes, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return;
        }

        // Clear the existing contentList to avoid duplicates
        contentList.clear();

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
        CollectionReference currentLessonRef = db.collection("current_lesson");

        Map<String, Object> currentLessonData = new HashMap<>();
        currentLessonData.put("userId", db.collection("users").document(userId));
        currentLessonData.put("lessonId", db.collection("total_lesson").document(lessonId));
        currentLessonData.put("progress", "0");

        currentLessonRef.add(currentLessonData)
                .addOnSuccessListener(documentReference -> {
                    isEnrolled = true;
                    btnEnroll.setVisibility(View.GONE);
                    CLbtn.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Enrolled successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Enrollment failed!", Toast.LENGTH_SHORT).show();
                });
    }
}


