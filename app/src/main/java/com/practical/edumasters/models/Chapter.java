package com.practical.edumasters.models;

public class Chapter {
    private String id;
    private String title;

    public Chapter() {}

    public Chapter(String id, String title) {
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
