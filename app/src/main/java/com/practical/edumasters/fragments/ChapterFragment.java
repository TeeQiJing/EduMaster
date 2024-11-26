package com.practical.edumasters.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.ChapterAdapter;
import com.practical.edumasters.models.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment {

    public ChapterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chapterRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Chapter> chapterList = fetchLessonsFromDatabase(); // Replace with your database fetching logic
        // Handle lesson click
        // Navigate to quiz
        // Open lesson content
        ChapterAdapter adapter = new ChapterAdapter(getContext(), chapterList, chapter -> {
            // Handle lesson click
            if ("quiz".equals(chapter.getType())) {
                // Navigate to quiz
            } else {
                // Open lesson content
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Chapter> fetchLessonsFromDatabase() {
        // Mock data for testing
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(new Chapter(1, "Topic 1: Introduction to Java", "video", true));
        chapters.add(new Chapter(2, "Topic 2: Advanced Java", "text", false));
        chapters.add(new Chapter(3, "Final Quiz", "quiz", false));
        return chapters;
    }
}
