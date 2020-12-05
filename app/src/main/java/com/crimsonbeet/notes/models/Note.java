package com.crimsonbeet.notes.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Note implements Parcelable {
    private final int id;
    private String title;
    private String content;


    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    /**
     * Copy constructor
     *
     * @param other Note to copy
     */
    public Note(Note other) {
        this.id = other.id;
        this.title = other.title;
        this.content = other.content;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(content);
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
