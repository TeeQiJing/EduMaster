package com.practical.edumasters.models;

public class Chapter {

    private int id;
    private String title;
    private String type; // This field represents the type of chapter (video, text, quiz)
    private boolean unlocked; // Represents whether the chapter is unlocked or not
    private int lessonsCount; // Number of lessons in the chapter

    // Constructor
    public Chapter(int id, String title, String type, boolean unlocked) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.unlocked = unlocked;

    }

    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }


}
