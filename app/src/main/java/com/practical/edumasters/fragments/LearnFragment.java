package com.practical.edumasters.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.practical.edumasters.R;
import com.practical.edumasters.models.CurrentLessonCard;
import com.practical.edumasters.adapters.CurrentLessonCardAdapter;
import com.practical.edumasters.models.PopularLessonCard;
import com.practical.edumasters.adapters.PopularLessonCardAdapter;
import com.practical.edumasters.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class LearnFragment extends Fragment {

    private ArrayList<CurrentLessonCard> currentLessonCards;
    private ArrayList<String> popularLessonCards;
    private ArrayList<String> currentLessonId;
    private CurrentLessonCardAdapter currentLessonCardAdapter;
    private PopularLessonCardAdapter popularLessonCardAdapter;
    private RecyclerView currentRecView;
    private RecyclerView popularRecView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView greeting;

    public LearnFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn, container, false);
        greeting = rootView.findViewById(R.id.greeting);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentLessonId = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();


        Log.d("LearnFragment", String.valueOf(R.drawable.gradient_background));

        //Current Lesson

        currentLessonCards = new ArrayList<>();
        currentLessonCardAdapter = new CurrentLessonCardAdapter(requireActivity().getSupportFragmentManager());
        currentRecView = view.findViewById(R.id.current_lesson_rec_view);
        currentRecView.setAdapter(currentLessonCardAdapter);
        currentRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        Log.d("LearnFragment", "RecyclerView set up with adapter.");
        fetchCurrentLessonData();

        Log.d("learnFragment", currentLessonId.toString());

        loadUserProfile();

        //Popular Lesson
        popularLessonCards = new ArrayList<>();
        popularLessonCardAdapter = new PopularLessonCardAdapter(requireActivity().getSupportFragmentManager());
        popularRecView = view.findViewById(R.id.popular_lesson_rec_view);
        popularRecView.setAdapter(popularLessonCardAdapter);
        popularRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        // Check login streak and show dialog
        checkAndShowLoginStreakDialog();
    }
    private void checkAndShowLoginStreakDialog() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference loginStreakRef = db.collection("login_streak").document(userId);

        // Attach a real-time listener to the login streak document
        loginStreakRef.addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                Log.e("LearnFragment", "Error listening to login streak changes", error);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Boolean isPointCollected = documentSnapshot.getBoolean("isPointCollected");
                Long streak = documentSnapshot.getLong("streak");

                if (isPointCollected == null || streak == null) {
                    Log.e("LearnFragment", "Login streak document is missing required fields.");
                    return;
                }

                Log.d("Streak", "Real-time streak: " + streak);

                if (!isPointCollected) {
                    // Show the login streak dialog if isLogin is false
                    showLoginStreakDialog(loginStreakRef, streak);
                } else {
                    Log.d("LoginStreak", "Dialog already shown today, skipping.");
                }
            } else {
                Log.e("LearnFragment", "Login streak document does not exist.");
            }
        });
    }


    private void showLoginStreakDialog(DocumentReference loginStreakRef, long streak) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_login_streak, null);

        // Initialize dialog components
        TextView streakNumberTextView = dialogView.findViewById(R.id.streak_number);
        streakNumberTextView.setText(String.valueOf(streak)); // Set streak number

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setView(dialogView);

        AlertDialog streakDialog = dialogBuilder.create();
        streakDialog.show();

        // Collect Points button
        Button collectPointsButton = dialogView.findViewById(R.id.collect_points_button);
        int points = (int) streak * 5;
        collectPointsButton.setOnClickListener(v -> {



            String userId = mAuth.getCurrentUser().getUid();
            DocumentReference userPointsRef = db.collection("users").document(userId);

            userPointsRef.update("xp", FieldValue.increment(points))
                    .addOnSuccessListener(aVoid -> {
                        loginStreakRef.update("isPointCollected", true);
                        Log.d("LoginStreak", "isPointCollected updated to true after showing dialog");
                        Toast.makeText(requireContext(), points + " Points Collected!", Toast.LENGTH_SHORT).show();
                        streakDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LoginStreak", "Error updating isPointCollected field", e);
                        Toast.makeText(requireContext(), "Error collecting points: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            streakDialog.dismiss();
        });
    }

    private void fetchCurrentLessonData() {
        // Get the current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(userId);

        // Fetch the current lesson data for the user
        FirebaseFirestore.getInstance()
                .collection("current_lesson")  // The collection where current lessons are stored
                .whereEqualTo("userId", userReference)  // Query by userId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Log the query result
                        Log.d("LearnFragment", "Query successful, found " + querySnapshot.size() + " documents.");

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Loop through all the documents returned
                            for (DocumentSnapshot document : querySnapshot) {
                                // Deserialize the document to the CurrentLessonCard object
                                CurrentLessonCard currentLessonCard = document.toObject(CurrentLessonCard.class);

                                // Log the fetched data to help debug
                                if (currentLessonCard != null) {
                                    Log.d("LearnFragment", "Fetched current lesson: " + currentLessonCard.getLessonId());
                                    Log.d("LearnFragment", "Progress: " + currentLessonCard.getProgress());
                                    currentLessonCards.add(currentLessonCard);
                                    currentLessonId.add(currentLessonCard.getLessonId().getId());
                                    Log.d("showing", currentLessonCard.getLessonId().getId());
                                } else {
                                    Log.d("LearnFragment", "CurrentLessonCard is null for document: " + document.getId());
                                }
                            }
                            currentLessonCardAdapter.setCard(currentLessonCards);
                            fetchTotalLessonData(currentLessonId);
                        } else {
                            // No documents found for this user
                            Log.d("LearnFragment", "No current lessons found for user: " + userId);
                            currentLessonCardAdapter.setCard(currentLessonCards); // Ensure empty view if no current lessons
                            fetchTotalLessonData(null); // Pass null to fetch all lessons as popular
                        }
                    } else {
                        // Error while fetching data
                        Log.e("LearnFragment", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchTotalLessonData(@Nullable ArrayList<String> hold) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("total_lesson").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("LearnFragment", String.valueOf(currentLessonId.size()));

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String docId = doc.getId();
                        Log.d("LearnFragment", docId);

                        // Add to popular if no "current lessons" or lesson is not in the "current" list
                        if (hold == null || !hold.contains(docId)) {
                            popularLessonCards.add(docId);
                            Log.d("LearnFragment", popularLessonCards.toString());
                        }
                    }
                    popularLessonCardAdapter.setCards(popularLessonCards);
                    Log.d("LearnFragment", "Popular lessons updated: " + popularLessonCards.toString());
                } else {
                    Log.e("LearnFragment", "Error getting total_lesson documents: ", task.getException());
                }
            }
        });
    }
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        greeting.setText("Hi, " + user.getUsername());

                        // Safely handle the avatar field

                    }else {
                        greeting.setText("Hi, User");
                    }
                } else {
                    Log.e("LearnFragment", "Document does not exist or is null");
                    Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("LearnFragment", "Error fetching user profile", task.getException());
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
