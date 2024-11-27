package com.practical.edumasters.models;

public class CurrentLessonCard {
    int image;
    String level;
    String title;
    String progress;

    public CurrentLessonCard() {}

    public CurrentLessonCard(int image, String level, String title, String progress) {
        this.image = image;
        this.level = level;
        this.title = title;
        this.progress = progress;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}
