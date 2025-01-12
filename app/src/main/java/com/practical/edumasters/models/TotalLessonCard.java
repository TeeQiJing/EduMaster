package com.practical.edumasters.models;

public class TotalLessonCard {
    private String image;
    private String level;
    private String pattern;
    private String rating;
    private String title;

    public TotalLessonCard() {}

    public TotalLessonCard(String image, String level, String pattern, String rating, String title) {
        this.image = image;
        this.level = level;
        this.pattern = pattern;
        this.rating = rating;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
