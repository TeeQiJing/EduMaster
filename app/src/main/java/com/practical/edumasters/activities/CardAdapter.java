package com.practical.edumasters.activities;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.practical.edumasters.R;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{
    ArrayList<LessonCard> cards = new ArrayList<>();
    Context context;

    public CardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.lessonLevel.setText(cards.get(position).getLevel());
        holder.imageView.setImageResource(cards.get(position).getImage());
        holder.lessonTitle.setText(cards.get(position).getTitle());
        holder.progressBar.setProgress(Integer.valueOf(cards.get(position).getProgress()));
        holder.progress.setText(cards.get(position).getProgress() + "%");
        holder.RLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Enrolled Successfully", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context, "Thank You!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setActionTextColor(Color.RED)
                        .setTextColor(Color.YELLOW)
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setCard(ArrayList<LessonCard> cards) {
        this.cards = cards;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout RLayout;
        ImageView imageView;
        TextView lessonLevel;
        TextView lessonTitle;
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
