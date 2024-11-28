package com.practical.edumasters.fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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


//    private void fetchTotalLessonData(ArrayList<String> hold) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("total_lesson").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    Log.d("LearnFragment", String.valueOf(currentLessonId.size()));
//                    for (QueryDocumentSnapshot doc: task.getResult()) {
//                        String docId =  doc.getId();
//                        Log.d("LearnFragment", docId);
//                        if (!hold.contains(docId)) {
//                            popularLessonCards.add(docId);
//                            Log.d("LearnFragment", popularLessonCards.toString());
//                        }
//                    }
//                    popularLessonCardAdapter.setCards(popularLessonCards);
//                    Log.d("LearnFragment", popularLessonCards.toString());
//                }
//                else {
//                    Log.e("LearnFragment", "Error getting total_card documents: ", task.getException());
//                }
//            }
//        });
//    }


//    private void fetchCurrentLessonData() {
//        // Get the current user ID
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DocumentReference userReference = FirebaseFirestore.getInstance().collection("users").document(userId);
//
//        // Fetch the current lesson data for the user
//        FirebaseFirestore.getInstance()
//                .collection("current_lesson")  // The collection where current lessons are stored
//                .whereEqualTo("userId", userReference)  // Query by userId
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//
//                        // Log the query result
//                        Log.d("LearnFragment", "Query successful, found " + querySnapshot.size() + " documents.");
//
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            // Loop through all the documents returned
//                            for (DocumentSnapshot document : querySnapshot) {
//                                // Deserialize the document to the CurrentLessonCard object
//                                CurrentLessonCard currentLessonCard = document.toObject(CurrentLessonCard.class);
//
//                                // Log the fetched data to help debug
//                                if (currentLessonCard != null) {
//                                    Log.d("LearnFragment", "Fetched current lesson: " + currentLessonCard.getLessonId());
//                                    Log.d("LearnFragment", "Progress: " + currentLessonCard.getProgress());
//                                    currentLessonCards.add(currentLessonCard);
//                                    currentLessonId.add(currentLessonCard.getLessonId().getId());
//                                    Log.d("showing", currentLessonCard.getLessonId().getId());
//                                } else {
//                                    Log.d("LearnFragment", "CurrentLessonCard is null for document: " + document.getId());
//                                }
//                            }
//                            currentLessonCardAdapter.setCard(currentLessonCards);
//                            fetchTotalLessonData(currentLessonId);
//                        } else {
//                            // No documents found for this user
//                            Log.d("LearnFragment", "No current lessons found for user: " + userId);
//                        }
//                    } else {
//                        // Error while fetching data
//                        Log.e("LearnFragment", "Error getting documents: ", task.getException());
//                    }
//                });
//    }

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
