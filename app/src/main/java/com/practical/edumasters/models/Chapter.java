package com.practical.edumasters.models;

public class Chapter {
    private int id;
    private String title;
    private String type;
    private boolean isUnlocked;
    private int nextLessonId;

    // Constructors
    public Chapter(int id, String title, String type, boolean isUnlocked, int nextLessonId) {
        this.id = id;
        this.title = title;
        this.type = type;

        this.isUnlocked = isUnlocked;
        this.nextLessonId = nextLessonId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public int getNextLessonId() {
        return nextLessonId;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}
