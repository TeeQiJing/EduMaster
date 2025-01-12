package com.practical.edumasters.models;

public class ContentBlock {
    private String type;
    private String value;

    public ContentBlock(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}