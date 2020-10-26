package com.crimsonbeet.notes.utils;

import androidx.annotation.Nullable;

import com.crimsonbeet.notes.models.Note;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonManager {
    public static String noteToJsonString(Note note) {
        Gson gson = new Gson();
        return gson.toJson(note);
    }

    public static String getJsonFilename(int nodeId) {
        return "note" + nodeId + ".json";
    }


    /**
     * Tries to create note object from given json string
     *
     * @param json Note object written as json
     * @return Note object or null
     */
    @Nullable
    public static Note jsonStringToNote(String json) {
        Gson gson = new Gson();

        try {
            return gson.fromJson(json, Note.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
