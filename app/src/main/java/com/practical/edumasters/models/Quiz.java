package com.practical.edumasters.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class Quiz {
    private String id;
    private String title;
    private DocumentReference ref;

    public Quiz() {
        // Default constructor
    }

    public static Quiz fromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(Quiz.class);
    }

    // Getters and setters
    public DocumentReference getRef() {
        return ref;
    }

    public void setRef(DocumentReference ref) {
        this.ref = ref;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
