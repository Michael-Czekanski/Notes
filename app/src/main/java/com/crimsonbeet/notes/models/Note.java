package com.crimsonbeet.notes.models;

public class Note {
    private int id;
    private String title;
    private String content;


    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
