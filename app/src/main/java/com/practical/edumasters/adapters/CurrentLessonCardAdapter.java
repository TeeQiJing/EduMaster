package com.practical.edumasters.adapters;

import android.content.Context;
import android.graphics.Color;
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
<<<<<<< HEAD
=======

>>>>>>> 1c9a2eb0d5d68f9a993be94854d0062ad706b594
import com.practical.edumasters.fragments.LearnFragment;
import com.practical.edumasters.fragments.LessonFragment;
import com.practical.edumasters.fragments.ProfileFragment;
import com.practical.edumasters.models.CurrentLessonCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//
//public class CurrentLessonCardAdapter extends RecyclerView.Adapter<CurrentLessonCardAdapter.ViewHolder>{
//    ArrayList<CurrentLessonCard> cards = new ArrayList<>();
//    FragmentManager fragmentManager;
//
//    public CurrentLessonCardAdapter(FragmentManager fragmentManager) {
//        this.fragmentManager = fragmentManager;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_lesson_card, parent, false);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        CurrentLessonCard currentCard = cards.get(position);
//
//        // Get the lessonId from the current card
//        String lessonId = currentCard.getCurrentLessonId();  // Assuming you have a `lessonId` field in CurrentLessonCard
//
//        // Fetch lesson details based on lessonId from total_lesson collection
//        fetchLessonDetails(lessonId, holder, currentCard);
//    }
//
//    private void fetchLessonDetails(String lessonId, @NonNull ViewHolder holder, CurrentLessonCard currentCard) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        // Query total_lesson collection using the lessonId
//        db.collection("current_lesson")
//                .document(lessonId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                // Retrieve the lesson details
//                                String title = document.getString("title");
//                                String level = document.getString("level");
//                                String image = document.getString("image");
//
//                                // Update the UI with the fetched details
//                                holder.lessonLevel.setText(level);
//                                holder.lessonTitle.setText(title);
//                                // Assuming images are stored as resource IDs or URLs, set it accordingly
//                                int imageResource = getImageResource(image);
//                                holder.imageView.setImageResource(imageResource);
//
//                                // Check if progress is not null, then set progress bar and progress text
//                                if (currentCard.getProgress() != null) {
//                                    int progressValue = Integer.parseInt(currentCard.getProgress());
//                                    holder.progressBar.setProgress(progressValue);
//                                    holder.progress.setText(progressValue + "%");
//                                } else {
//                                    holder.progressBar.setProgress(0); // Default value if progress is null
//                                    holder.progress.setText("0%");
//                                }
//                            } else {
//                                Log.d("Lesson Details", "No such lesson found!");
//                            }
//                        } else {
//                            Log.d("Lesson Details", "Failed to get lesson details", task.getException());
//                        }
//                    }
//                });
//    }
//
//    private int getImageResource(String imageName) {
//        // Example: Assuming you have images as resources
//        switch (imageName) {
//            case "GitHubImage":
//                return R.drawable.ic_apple;
//            case "PythonImage":
//                return R.drawable.ic_certificate;
//            case "JavaImage":
//                return R.drawable.ic_google;
//            case "HTMLCSSImage":
//                return R.drawable.ic_facebook;
//            case "UIUXImage":
//                return R.drawable.ic_chat;
//            default:
//                return R.drawable.ic_launcher_background; // A default image
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return cards.size();
//    }
//
//    public void setCard(ArrayList<CurrentLessonCard> cards) {
//        this.cards = cards;
//        notifyDataSetChanged();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        RelativeLayout RLayout;
//        ImageView imageView;
//        TextView lessonLevel;
//        TextView lessonTitle;
//        ProgressBar progressBar;
//        TextView progress;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            RLayout = itemView.findViewById(R.id.material_card_view_RLayout);
//            imageView = itemView.findViewById(R.id.lesson_image);
//            lessonLevel = itemView.findViewById(R.id.lesson_level);
//            lessonTitle = itemView.findViewById(R.id.lesson_title);
//            progressBar = itemView.findViewById(R.id.progress_bar);
//            progress = itemView.findViewById(R.id.progress);
//        }
//    }
//
//}

<<<<<<< HEAD

public class CurrentLessonCardAdapter extends RecyclerView.Adapter<CurrentLessonCardAdapter.ViewHolder> {
=======
public class  CurrentLessonCardAdapter extends RecyclerView.Adapter<CurrentLessonCardAdapter.ViewHolder>{
>>>>>>> 42bab3598f64f74bb20e98c7fd8a03f360648a9a
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
<<<<<<< HEAD
        CurrentLessonCard currentCard = cards.get(position);
        Log.d("CurrentLessonCardAdapter", "Binding card: " + currentCard.getLessonId() + ", Progress: " + currentCard.getProgress());

        // Fetch lesson details based on lessonId (which is a DocumentReference)
        fetchLessonDetails(currentCard, holder);
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
                            String image = document.getString("image");
                            String rating = document.getString("rating");
                            String pattern = document.getString("pattern");

                            // Add more debug logs for fields
                            Log.d("CurrentLessonCardAdapter", "Fetched lesson details: Title: " + title + ", Level: " + level + ", Image: " + image + ", Rating: " + rating + ", Pattern: " + pattern);

                            // Set the fetched values to the UI
                            holder.lessonLevel.setText(level);
                            holder.lessonTitle.setText(title);


                            holder.imageView.setImageResource(getImageResource(image));  // Handle the image resource

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
=======
        holder.lessonLevel.setText(cards.get(position).getLevel());
        holder.imageView.setImageResource(Integer.valueOf(cards.get(position).getImage()));
        holder.lessonTitle.setText(cards.get(position).getTitle());
        holder.progressBar.setProgress(Integer.valueOf(cards.get(position).getProgress()));
        holder.progress.setText(cards.get(position).getProgress() + "%");
<<<<<<< HEAD
        holder.RLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LessonFragment lessonFragment = new LessonFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, lessonFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
=======

>>>>>>> 1c9a2eb0d5d68f9a993be94854d0062ad706b594
>>>>>>> 42bab3598f64f74bb20e98c7fd8a03f360648a9a
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
