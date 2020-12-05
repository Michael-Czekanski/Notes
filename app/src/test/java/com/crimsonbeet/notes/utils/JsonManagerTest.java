package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.Note;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonManagerTest {


    private boolean deleteNoteJsonFile(String filename) {
        File file = new File(filename);
        return file.delete();
    }

    /**
     * Creates Json file
     *
     * @return Created Json filename
     */
    private String createNoteJsonFile(int noteId, String noteTitle, String noteContent) throws IOException {
        String filename = "note" + noteId + ".json";
        String jsonString = String.format("{\"id\":%d,\"title\":\"%s\",\"content\":\"%s\"}",
                noteId, noteTitle, noteContent);
        File file = new File(filename);

        if (file.exists()) {
            if (!deleteNoteJsonFile(filename)) {
                throw new IOException("File exists already and can not delete it.");
            }
        }

        if (file.createNewFile()) {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.close();
        } else {
            throw new IOException();
        }

        return filename;
    }

    @Test
    public void readNoteFromJson() throws IOException {
        JsonManager jsonManager = new JsonManager(new File("."));

        int expectedNoteId = 1;
        String expectedNoteTitle = "Title";
        String expectedNoteContent = "Content";
        // Create file
        String jsonFilename = createNoteJsonFile(expectedNoteId, expectedNoteTitle, expectedNoteContent);
        // Read from file
        Note actualNote = jsonManager.readNoteFromJson(expectedNoteId);
        // Delete file
        if (!deleteNoteJsonFile(jsonFilename)) {
            throw new IOException("Can not delete file");
        }

        Assert.assertEquals(expectedNoteId, actualNote.getId());
        Assert.assertEquals(expectedNoteTitle, actualNote.getTitle());
        Assert.assertEquals(expectedNoteContent, actualNote.getContent());
    }

    @Test
    public void writeNoteToJson() throws IOException {
        JsonManager jsonManager = new JsonManager(new File("."));

        Note note = new Note(1, "Title", "Content");
        String filename = jsonManager.writeNoteToJson(note);

        String expectedFileContent = String.format("{\"id\":%d,\"title\":\"%s\",\"content\":\"%s\"}",
                note.getId(), note.getTitle(), note.getContent());

        StringBuilder actualFileContent = new StringBuilder();

        FileReader fileReader = new FileReader(filename);
        int charAsInt = fileReader.read();
        while (charAsInt > -1) {
            actualFileContent.append((char) charAsInt);
            charAsInt = fileReader.read();
        }
        fileReader.close();

        if (!deleteNoteJsonFile(filename)) {
            throw new IOException("Can not delete test json file.");
        }

        Assert.assertEquals(expectedFileContent, actualFileContent.toString());
    }
}