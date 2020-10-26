package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.Note;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

    private Scanner openJsonFile(String filePath) throws FileNotFoundException {
        File jsonFile = new File(filePath);

        if (!jsonFile.exists()) {
            throw new FileNotFoundException();
        }
        return new Scanner(jsonFile);
    }

}
