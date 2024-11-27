package com.practical.edumasters.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.practical.edumasters.R;
import com.practical.edumasters.models.CurrentLessonCard;
import com.practical.edumasters.adapters.CurrentLessonCardAdapter;
import com.practical.edumasters.models.PopularLessonCard;
import com.practical.edumasters.adapters.PopularLessonCardAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LearnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LearnFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<CurrentLessonCard> currentLessonCards;
    private ArrayList<PopularLessonCard> popularLessonCards;
    private CurrentLessonCardAdapter currentLessonCardAdapter;
    private PopularLessonCardAdapter popularLessonCardAdapter;
    private RecyclerView currentRecView;
    private RecyclerView popularRecView;
    private FirebaseFirestore db;


    public LearnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LearnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LearnFragment newInstance(String param1, String param2) {
        LearnFragment fragment = new LearnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        //Current Lesson
        currentLessonCards = new ArrayList<>();

        currentLessonCardAdapter = new CurrentLessonCardAdapter(requireActivity().getSupportFragmentManager());

        fetchCurrentLessonData();

        currentRecView = view.findViewById(R.id.current_lesson_rec_view);
        currentRecView.setAdapter(currentLessonCardAdapter);

        currentRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        //Popular Lesson
        popularLessonCards = new ArrayList<>();

        popularLessonCardAdapter = new PopularLessonCardAdapter(requireActivity().getSupportFragmentManager());

        fetchPopularLessonData();

        popularRecView = view.findViewById(R.id.popular_lesson_rec_view);
        popularRecView.setAdapter(popularLessonCardAdapter);

        popularRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void loadCurrentLessonData(CurrentLessonCard currentLessonCard) {
        db.collection("current_lesson").document().set(currentLessonCard);
    }

    public void fetchCurrentLessonData() {
        CollectionReference collectionReference = db.collection("current_lesson");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        CurrentLessonCard current = doc.toObject(CurrentLessonCard.class);
                        currentLessonCards.add(current);
                    }
                    currentLessonCardAdapter.setCard(currentLessonCards);
                }
                else {
                    Log.e("FirebaseFirestore", "Error fetching Current Lesson", task.getException());
                }
            }
        });
    }

    public void loadPopularLessonData(PopularLessonCard popularLessonCard) {
        db.collection("popular_lesson").document().set(popularLessonCard);
    }

    public void fetchPopularLessonData() {
        CollectionReference collectionReference = db.collection("popular_lesson");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        PopularLessonCard popularLessonCard = doc.toObject(PopularLessonCard.class);
                        popularLessonCards.add(popularLessonCard);
                    }
                    popularLessonCardAdapter.setCards(popularLessonCards);
                }
                else {
                    Log.e("FirebaseFirestore", "Error fetching Popular Lesson", task.getException());
                }
            }
        });
    }


}