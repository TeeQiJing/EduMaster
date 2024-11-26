package com.practical.edumasters.models;

public class Lesson {
    private String title;
    private String imageUrl; // Add the imageUrl field
    private double rating;

    // Constructor
    public Lesson(String title, String imageUrl, double rating) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;  // Getter for imageUrl
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
