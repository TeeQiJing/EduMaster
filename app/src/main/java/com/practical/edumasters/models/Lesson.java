package com.practical.edumasters.models;

public class Lesson {
    private String id;
    private String title;
    private double rating;
    private String pattern;

    public Lesson() {}

    public Lesson(String id, String title, double rating, String pattern) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.pattern = pattern;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getRating() {
        return rating;
    }

    public String getPattern() {
        return pattern;
    }
}
