package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.Note;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonManagerTest {


    @Test
    public void noteToJsonString() {
        Note note = new Note(1, "Test", "Content");

        String expectedString = "{\"id\":1,\"title\":\"Test\",\"content\":\"Content\"}";
        String actualString = JsonManager.noteToJsonString(note);

        assertEquals(expectedString, actualString);
    }

    @Test
    public void getJsonFilename() {
        int noteId = 1;

        String expectedFilename = "note1.json";
        String actualFilename = JsonManager.getJsonFilename(noteId);

        assertEquals(expectedFilename, actualFilename);
    }
}