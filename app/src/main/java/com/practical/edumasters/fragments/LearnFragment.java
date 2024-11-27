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

import java.util.ArrayList;

public class LearnFragment extends Fragment {

    private ArrayList<CurrentLessonCard> currentLessonCards;
    private CurrentLessonCardAdapter currentLessonCardAdapter;
    private RecyclerView currentRecView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public LearnFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        Log.d("haha", String.valueOf(R.drawable.gradient_background));

        //Current Lesson

        currentLessonCards = new ArrayList<>();
        currentLessonCardAdapter = new CurrentLessonCardAdapter(requireActivity().getSupportFragmentManager());
        currentRecView = view.findViewById(R.id.current_lesson_rec_view);
        currentRecView.setAdapter(currentLessonCardAdapter);
        currentRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        Log.d("LearnFragment", "RecyclerView set up with adapter.");
        fetchCurrentLessonData();
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
                                } else {
                                    Log.d("LearnFragment", "CurrentLessonCard is null for document: " + document.getId());
                                }
                            }
                            currentLessonCardAdapter.setCard(currentLessonCards);
                        } else {
                            // No documents found for this user
                            Log.d("LearnFragment", "No current lessons found for user: " + userId);
                        }
                    } else {
                        // Error while fetching data
                        Log.e("LearnFragment", "Error getting documents: ", task.getException());
                    }
                });
    }
}
