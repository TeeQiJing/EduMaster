package com.practical.edumasters.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.practical.edumasters.R;
import com.practical.edumasters.fragments.LessonFragment;
import com.practical.edumasters.fragments.ProfileFragment;
import com.practical.edumasters.models.Chapter;
import com.practical.edumasters.models.PopularLessonCard;

import java.util.ArrayList;

public class PopularLessonCardAdapter extends RecyclerView.Adapter<PopularLessonCardAdapter.ViewHolder> {

    ArrayList<PopularLessonCard> cards = new ArrayList<>();
    FragmentManager fragmentManager;

    public PopularLessonCardAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_lesson_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(cards.get(position).getImage());
        holder.level.setText(cards.get(position).getLevel());
        holder.title.setText(cards.get(position).getTitle());
        holder.ratings.setText(cards.get(position).getRatings());
        holder.RelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LessonFragment lessonFragment = new LessonFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, lessonFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCards(ArrayList<PopularLessonCard> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView level;
        TextView title;
        TextView ratings;
        RelativeLayout RelLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.popular_lesson_image);
            level = itemView.findViewById(R.id.popular_lesson_level);
            title = itemView.findViewById(R.id.popular_lesson_title);
            ratings = itemView.findViewById(R.id.ratings);
            RelLayout = itemView.findViewById(R.id.popular_card_view_RelLayout);
        }
    }

}
