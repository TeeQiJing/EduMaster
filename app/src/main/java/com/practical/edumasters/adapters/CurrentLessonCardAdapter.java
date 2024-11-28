package com.practical.edumasters.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.practical.edumasters.R;

import com.practical.edumasters.fragments.LearnFragment;
import com.practical.edumasters.fragments.LessonFragment;
import com.practical.edumasters.fragments.ProfileFragment;
import com.practical.edumasters.models.CurrentLessonCard;

import java.util.ArrayList;
import java.util.Objects;

public class  CurrentLessonCardAdapter extends RecyclerView.Adapter<CurrentLessonCardAdapter.ViewHolder>{
    ArrayList<CurrentLessonCard> cards = new ArrayList<>();
    FragmentManager fragmentManager;
    FirebaseFirestore db;

    public CurrentLessonCardAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_lesson_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrentLessonCard currentCard = cards.get(position);
        Log.d("CurrentLessonCardAdapter", "Binding card: " + currentCard.getLessonId() + ", Progress: " + currentCard.getProgress());

        // Fetch lesson details based on lessonId (which is a DocumentReference)
        fetchLessonDetails(currentCard, holder);

        holder.RLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLesson(currentCard);
            }
        });
    }

    private void navigateToLesson(CurrentLessonCard currentLessonCard) {
        Bundle bundle = new Bundle();
        bundle.putString("lessonId", currentLessonCard.getLessonId().getId());

        LessonFragment lessonFragment = new LessonFragment();
        lessonFragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, lessonFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchLessonDetails(CurrentLessonCard currentCard, @NonNull ViewHolder holder) {
        Log.d("CurrentLessonCardAdapter", "Fetching lesson details for lessonId: " + currentCard.getLessonId().getId());

        db.collection("total_lesson")
                .document(currentCard.getLessonId().getId())  // Ensure you're using the correct path
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Fetch all lesson details
                            String title = document.getString("title");
                            String level = document.getString("level");

                            String rating = document.getString("rating");
                            String pattern = document.getString("pattern");

                            // Add more debug logs for fields
                            Log.d("CurrentLessonCardAdapter", "Fetched lesson details: Title: " + title + ", Level: " + level + ", Rating: " + rating + ", Pattern: " + pattern);

                            // Set the fetched values to the UI
                            holder.lessonLevel.setText(level);
                            holder.lessonTitle.setText(title);

                            switch (Objects.requireNonNull(title)){
                                case "GitHub":
                                    holder.imageView.setImageResource(R.drawable.ic_github);
                                    break;
                                case "Java":
                                    holder.imageView.setImageResource(R.drawable.ic_java);
                                    break;
                                case "HTML & CSS":
                                    holder.imageView.setImageResource(R.drawable.ic_html);
                                    break;

                                case "Python":
                                    holder.imageView.setImageResource(R.drawable.ic_python);
                                    break;
                                case "UI UX Design":
                                    holder.imageView.setImageResource(R.drawable.ic_uiux);
                                    break;
                                default:
                                    holder.imageView.setImageResource(R.drawable.ic_avatar);
                            }

                            // Set progress based on the current card progress value
                            if (currentCard.getProgress() != null) {
                                int progressValue = Integer.parseInt(currentCard.getProgress());
                                holder.progressBar.setProgress(progressValue);
                                holder.progress.setText(progressValue + "%");
                            } else {
                                holder.progressBar.setProgress(0);  // Default value if progress is null
                                holder.progress.setText("0%");
                            }
                        } else {
                            Log.e("CurrentLessonCardAdapter", "No document found for lessonId: " + currentCard.getLessonId().getId());
                        }
                    } else {
                        Log.e("CurrentLessonCardAdapter", "Error fetching lesson details", task.getException());
                    }
                });
    }

    private int getImageResource(String imageName) {
        switch (imageName) {
            case "GitHubImage":
                return R.drawable.ic_apple;
            case "PythonImage":
                return R.drawable.ic_certificate;
            case "JavaImage":
                return R.drawable.ic_google;
            case "HTMLCSSImage":
                return R.drawable.ic_facebook;
            case "UIUXImage":
                return R.drawable.ic_chat;
            default:
                return R.drawable.ic_launcher_background;  // Default image if no match found
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCard(ArrayList<CurrentLessonCard> cards) {
        Log.d("CurrentLessonCardAdapter", "Setting " + cards.size() + " cards in adapter.");
        this.cards = cards;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout RLayout;
        ImageView imageView;
        TextView lessonLevel;
        TextView lessonTitle;
        TextView lessonRating; // Add a TextView for rating
        TextView lessonPattern; // Add a TextView for pattern
        ProgressBar progressBar;
        TextView progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RLayout = itemView.findViewById(R.id.material_card_view_RLayout);
            imageView = itemView.findViewById(R.id.lesson_image);
            lessonLevel = itemView.findViewById(R.id.lesson_level);
            lessonTitle = itemView.findViewById(R.id.lesson_title);
            progressBar = itemView.findViewById(R.id.progress_bar);
            progress = itemView.findViewById(R.id.progress);
        }
    }
}

