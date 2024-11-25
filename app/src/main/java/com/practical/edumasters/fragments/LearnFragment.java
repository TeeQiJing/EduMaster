package com.practical.edumasters.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practical.edumasters.R;
import com.practical.edumasters.activities.CardAdapter;
import com.practical.edumasters.activities.LessonCard;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<LessonCard> cards = new ArrayList<>();
        cards.add(new LessonCard(R.drawable.lesson_image, "Beginner", "Java Programming", "10"));
        cards.add(new LessonCard(R.drawable.gradient_background, "Intermediate", "Kotlin Programming", "40"));
        cards.add(new LessonCard(R.drawable.ic_launcher_background, "Advanced", "JavaScript Programming", "20"));

        CardAdapter adapter = new CardAdapter(requireContext());
        adapter.setCard(cards);

        RecyclerView currentRecView = view.findViewById(R.id.current_lesson_rec_view);
        currentRecView.setAdapter(adapter);

        RecyclerView popularRecView = view.findViewById(R.id.popular_lesson_rec_view);
        popularRecView.setAdapter(adapter);

        currentRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        popularRecView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true));
    }
}