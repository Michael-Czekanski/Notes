package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.Note;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonManager {
    private final Gson gson;

    public JsonManager() {
        gson = new Gson();
    }

    private String noteToJsonString(Note note) {
        return gson.toJson(note);
    }

    private String getJsonFilename(int nodeId) {
        return "note" + nodeId + ".json";
    }

    private boolean deserializationSuccess(Note deserializedNote){
        boolean success = true;

        if(deserializedNote.getTitle() == null){
            success = false;
        }
        if(deserializedNote.getContent() == null){
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

}
