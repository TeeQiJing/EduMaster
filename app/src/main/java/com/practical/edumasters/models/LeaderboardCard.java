package com.practical.edumasters.models;

public class LeaderboardCard {
    String testing_name;
    String testing_mark;

    public LeaderboardCard(String testing_name, String testing_mark) {
        this.testing_name = testing_name;
        this.testing_mark = testing_mark;
    }

    public String getTesting_name() {
        return testing_name;
    }

    public String getTesting_mark() {
        return testing_mark;
    }
}
