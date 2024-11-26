package com.practical.edumasters.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.practical.edumasters.R;
import com.practical.edumasters.adapters.LessonAdapter;
import com.practical.edumasters.models.Lesson;
import java.util.ArrayList;
import java.util.List;

public class LessonFragment extends Fragment {

    public LessonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chaptersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Lesson> lessonList = fetchLessonsFromDatabase(); // Replace with your database fetching logic
        // Handle lesson click
        // Navigate to quiz
        // Open lesson content
        LessonAdapter adapter = new LessonAdapter(getContext(), lessonList, lesson -> {
            // Handle lesson click
            if ("quiz".equals(lesson.getType())) {
                // Navigate to quiz
            } else {
                // Open lesson content
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Lesson> fetchLessonsFromDatabase() {
        // Mock data for testing
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(new Lesson(1, "Topic 1: Introduction to Java", "video", true, 2));
        lessons.add(new Lesson(2, "Topic 2: Advanced Java", "text", false, 3));
        lessons.add(new Lesson(3, "Final Quiz", "quiz", false, 0));
        return lessons;
    }
}
