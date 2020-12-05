package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.EncryptedNote;
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

    /**
     * Creates Json file. <br>
     * Writes this string to json:
     * "{\"id\":1,\"encryptedTitle\":[1,1,1,1],\"encryptedContent\":[2,2,2,2],\"salt\":[24,23,21,-13,14],\"encryptedNonce\":[11,-21,3,-56,1,2]}" <br>
     * You should make EncryptedNote object so that it matches this json string.
     *
     * @return Created Json filename
     */
    private String createEncryptedNoteJsonFile() throws IOException {
        String filename = "note" + 1 + ".json";
        String jsonString = "{\"id\":1,\"encryptedTitle\":[1,1,1,1],\"encryptedContent\":[2,2,2,2],\"salt\":[24,23,21,-13,14],\"encryptedNonce\":[11,-21,3,-56,1,2]}";
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

    @Test
    public void deleteEncryptedNoteJsonFile() throws IOException {
        JsonManager jsonManager = new JsonManager(new File("."));
        String filename = createEncryptedNoteJsonFile();

        jsonManager.deleteEncryptedNoteJsonFile(1);
    }

    @Test
    public void writeEncryptedNoteToJson() throws IOException {
        JsonManager jsonManager = new JsonManager(new File("."));

        int noteId = 1;
        byte[] encryptedTitle = new byte[]{1, 1, 1, 1};
        byte[] encryptedContent = new byte[]{2, 2, 2, 2};

        byte[] salt = new byte[]{24, 23, 21, -13, 14};
        byte[] encryptedNonce = new byte[]{11, -21, 3, -56, 1, 2};

        EncryptedNote encryptedNote = new EncryptedNote(noteId, encryptedTitle, encryptedContent, salt, encryptedNonce);
        String filename = jsonManager.writeEncryptedNoteToJson(encryptedNote);

        String expectedFileContent = "{\"id\":1,\"encryptedTitle\":[1,1,1,1],\"encryptedContent\":[2,2,2,2],\"salt\":[24,23,21,-13,14],\"encryptedNonce\":[11,-21,3,-56,1,2]}";

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

    @Test
    public void readEncryptedNoteFromJson() throws IOException {
        JsonManager jsonManager = new JsonManager(new File("."));

        int noteId = 1;
        byte[] encryptedTitle = new byte[]{1, 1, 1, 1};
        byte[] encryptedContent = new byte[]{2, 2, 2, 2};

        byte[] salt = new byte[]{24, 23, 21, -13, 14};
        byte[] encryptedNonce = new byte[]{11, -21, 3, -56, 1, 2};

        EncryptedNote encryptedNote = new EncryptedNote(noteId, encryptedTitle, encryptedContent, salt, encryptedNonce);

        createEncryptedNoteJsonFile();

        String jsonFilename = createEncryptedNoteJsonFile();

        // Read from file
        EncryptedNote actualNote = jsonManager.readEncryptedNoteFromJson(noteId);
        // Delete file
        if (!deleteNoteJsonFile(jsonFilename)) {
            throw new IOException("Can not delete file");
        }

        Assert.assertEquals(noteId, actualNote.getId());
        Assert.assertArrayEquals(encryptedTitle, actualNote.getEncryptedTitle());
        Assert.assertArrayEquals(encryptedContent, actualNote.getEncryptedContent());
        Assert.assertArrayEquals(salt, actualNote.getSalt());
        Assert.assertArrayEquals(encryptedNonce, actualNote.getEncryptedNonce());

    }
}