package com.practical.edumasters.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class Chapter {

    private DocumentReference ref;
    private String title;
    private String id;
    private List<Map<String, Object>> content; // List to hold content blocks

    // Static method to create a Chapter object from Firestore snapshot
    public static Chapter fromSnapshot(DocumentSnapshot snapshot) {
        Chapter chapter = snapshot.toObject(Chapter.class); // Automatically populate fields
        DocumentReference ref = snapshot.getDocumentReference("ref"); // Get the reference field
        if (ref != null) {
            assert chapter != null;
            chapter.setRef(ref); // Set the ref field if it's not null
        }
        chapter.setId(snapshot.getId()); // Set the Firestore document ID
        return chapter;
    }

    // Getters and setters
    public DocumentReference getRef() {
        return ref;
    }

    public void setRef(DocumentReference ref) {
        this.ref = ref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Map<String, Object>> getContent() {
        return content;
    }

    public void setContent(List<Map<String, Object>> content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
