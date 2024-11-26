package com.practical.edumasters.models;

public class Quiz {
    private String id;
    private String title;

    public Quiz() {}

    public Quiz(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
