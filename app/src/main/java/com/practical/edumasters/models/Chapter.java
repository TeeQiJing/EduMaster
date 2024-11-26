package com.practical.edumasters.models;

import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.DocumentSnapshot;

public class Chapter {
    private DocumentReference ref;  // Change ref to DocumentReference
    private String title;

    private String id;

    private String content;

    public static Chapter fromSnapshot(DocumentSnapshot snapshot) {
        Chapter chapter = snapshot.toObject(Chapter.class);
        // Explicitly handle 'ref' as DocumentReference
        DocumentReference ref = snapshot.getDocumentReference("ref");
        if (ref != null) {
            assert chapter != null;
            chapter.setRef(ref);
        }
        return chapter;
    }

    // Getters and setters for fields
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
