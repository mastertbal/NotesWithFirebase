package com.example.noteswithfirebase;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Note {
    private String title;
    private String text;
    private Timestamp timestamp;

    public Note() {}

    public Note(String title, String text, Timestamp timestamp) {
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String fromTimeStampToString(Timestamp timestamp) {
        return new SimpleDateFormat("MM:dd:yyyy", Locale.getDefault()).format(timestamp.toDate());
    }
}
