package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.EncryptedNote;
import com.crimsonbeet.notes.models.Note;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonManager {
    private final Gson gson;
    private final File filesDirectory;

    public JsonManager(File filesDirectory) {
        gson = new Gson();
        this.filesDirectory = filesDirectory;
    }

    private String noteToJsonString(Note note) {
        return gson.toJson(note);
    }

    private String getJsonFilename(int nodeId) {
        return filesDirectory + File.separator + "note" + nodeId + ".json";
    }

    private boolean deserializationSuccess(Note deserializedNote){
        boolean success = true;

        if (deserializedNote.getTitle() == null) {
            success = false;
        }
        if (deserializedNote.getContent() == null) {
            success = false;
        }
        return success;
    }

    /**
     * Checks if encrypted note deserialized successfully
     *
     * @param deserializedEncryptedNote
     * @return True or false
     */
    private boolean deserializationSuccess(EncryptedNote deserializedEncryptedNote) {
        boolean success = true;

        if (deserializedEncryptedNote.getEncryptedTitle() == null) {
            success = false;
        }
        if (deserializedEncryptedNote.getEncryptedContent() == null) {
            success = false;
        }
        if (deserializedEncryptedNote.getEncryptedNonce() == null) {
            success = false;
        }
        if (deserializedEncryptedNote.getSalt() == null) {
            success = false;
        }

        return success;
    }


    private FileReader openJsonFile(String filePath) throws FileNotFoundException {
        File jsonFile = new File(filePath);

        if (!jsonFile.exists()) {
            throw new FileNotFoundException();
        }
        return new FileReader(jsonFile);
    }

    /**
     * Tries to read note from json file.
     *
     * @param noteId Note's id
     * @return Read note or null if deserialization fails.
     * @throws FileNotFoundException If there is no corresponding note json file.
     */
    public Note readNoteFromJson(int noteId) throws IOException {
        String filename = getJsonFilename(noteId);
        FileReader reader = openJsonFile(filename);

        Note note = gson.fromJson(reader, Note.class);
        reader.close();
        if (deserializationSuccess(note)) {
            return note;
        }
        return null;
    }

    public String writeNoteToJson(Note note) throws IOException {
        String filename = getJsonFilename(note.getId());

        File file = new File(filename);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(noteToJsonString(note));
            fileWriter.close();
            return filename;
        } catch (IOException e) {
            throw new IOException("Error writing note to json file.");
        }
    }

    public boolean deleteNoteJsonFile(int noteId) {
        File file = new File(getJsonFilename(noteId));
        return file.delete();
    }

    public boolean deleteNoteJsonFile(Note note) {
        File file = new File(getJsonFilename(note.getId()));
        return file.delete();
    }

    public boolean deleteEncryptedNoteJsonFile(EncryptedNote encryptedNote) {
        File file = new File(getJsonFilename(encryptedNote.getId()));
        return file.delete();
    }

    public boolean deleteEncryptedNoteJsonFile(int noteId) {
        File file = new File(getJsonFilename(noteId));
        return file.delete();
    }

    /**
     * Makes json string from encrypted note.
     *
     * @param encryptedNote Note to convert to json string.
     * @return Json string.
     */
    public String encryptedNoteToJsonString(EncryptedNote encryptedNote) {
        return gson.toJson(encryptedNote);
    }

    /**
     * Writes encrypted note to json file.
     *
     * @param encryptedNote Note to write to json.
     * @return Json filename.
     * @throws IOException When there was error writing encrypted note to json file.
     */
    public String writeEncryptedNoteToJson(EncryptedNote encryptedNote) throws IOException {
        String filename = getJsonFilename(encryptedNote.getId());

        File file = new File(filename);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(encryptedNoteToJsonString(encryptedNote));
            fileWriter.close();
            return filename;
        } catch (IOException e) {
            throw new IOException("Error writing encrypted note to json file.");
        }
    }

    /**
     * Reads encrypted note from json file.
     *
     * @param noteId Note to read.
     * @return Deserialized encrypted note or null if deserialization was not successful.
     * @throws FileNotFoundException If there was no json file corresponding to given note id.
     * @throws IOException           If there was problem with closing file reader.
     */
    public EncryptedNote readEncryptedNoteFromJson(int noteId) throws IOException {
        String filename = getJsonFilename(noteId);
        FileReader reader = openJsonFile(filename);

        EncryptedNote encryptedNote = gson.fromJson(reader, EncryptedNote.class);
        reader.close();
        if (deserializationSuccess(encryptedNote)) {
            return encryptedNote;
        }
        return null;
    }



}
