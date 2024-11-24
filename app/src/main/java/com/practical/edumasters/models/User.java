package com.practical.edumasters.models;
import java.util.List;

public class User {
    private String username;
    private String email;
    private String hashedPassword;
    private String registrationDate;
    private int xp;
    private String bio;
    private String avatar; // Avatar URL
    private List<String> courses; // List of enrolled courses

    // Default constructor required for Firebase deserialization
    public User() {}

    public User(String username, String email, String hashedPassword, String registrationDate, int xp, String bio, String avatar) {
        this.username = username;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.registrationDate = registrationDate;
        this.xp = xp;
        this.bio = bio;
        this.avatar = avatar;
        courses = null;
    }

    // Getters and setters for all fields
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
