package com.practical.edumasters.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.practical.edumasters.R;
import com.practical.edumasters.models.Lesson;
import com.squareup.picasso.Picasso;

public class LessonFragment extends Fragment {

    private TextView lessonTitleText;
    private TextView lessonRatingText;
    private ImageView lessonImage;

    public LessonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lesson, container, false);

        // Initialize the views
        lessonTitleText = rootView.findViewById(R.id.lessonTitle);
        lessonRatingText = rootView.findViewById(R.id.lessonRatingText);
        lessonImage = rootView.findViewById(R.id.lessonImage);

        // Load the lesson data (from database, API, etc.)
        loadLesson();

        return rootView;
    }

    private void loadLesson() {
        // Example lesson data, replace with actual data fetching logic
        Lesson lesson = new Lesson("Java Programming 101", "https://example.com/image1.jpg", 4.5);

        // Set lesson data to the views
        lessonTitleText.setText(lesson.getTitle());
        lessonRatingText.setText(String.format("Rating: %.1f", lesson.getRating()));
        Picasso.get().load(lesson.getImageUrl()).into(lessonImage);
    }
}
