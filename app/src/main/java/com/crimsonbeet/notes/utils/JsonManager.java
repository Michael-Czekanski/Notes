package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.Note;
import com.google.gson.Gson;

public class JsonManager {
    public static String noteToJsonString(Note note) {
        Gson gson = new Gson();
        return gson.toJson(note);
    }

    public static String getJsonFilename(int nodeId) {
        return "note" + nodeId + ".json";
    }

}
