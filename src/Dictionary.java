/*
 * Kian Dsouza - 1142463
 * Dictionary
 * */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class Dictionary {
    private JSONObject dictionaryData;

    private  File dictionaryFile;

    public Dictionary(File dictionaryFile) {
        try {
            this.dictionaryFile = dictionaryFile;
            String fileContent = new String(Files.readAllBytes(dictionaryFile.toPath()));
            dictionaryData = new JSONObject(fileContent);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public synchronized String search(String word) {
        if (dictionaryData.has(word)) {
            JSONArray meanings = dictionaryData.getJSONArray(word);
            StringBuilder meaningString = new StringBuilder();
            for (int i = 0; i < meanings.length(); i++) {
                meaningString.append(meanings.getString(i));
                if (i < meanings.length() - 1) {
                    meaningString.append(", ");
                }
            }
            return "Meaning of " + word + ": " + meaningString.toString();
        } else {
            return "Word not found.";
        }
    }

    public synchronized String add(String input) {
        try {
            String[] parts = input.split(" ", 2);
            if (parts.length == 2) {
                String word = parts[0];
                String[] meanings = parts[1].split(";");
                if (!dictionaryData.has(word)) {
                    dictionaryData.put(word, new JSONArray(meanings));
                    saveDictionary();
                    return "Added " + word + " successfully.";
                } else {
                    return "Word already exists.";
                }
            } else {
                return "Invalid input format.";
            }
        } catch (JSONException e) {
            return "Error: " + e.getMessage();
        }
    }

    public synchronized String remove(String word) {
        if (dictionaryData.has(word)) {
            dictionaryData.remove(word);
            saveDictionary();
            return "Removed " + word + " successfully.";
        } else {
            return "Word not found.";
        }
    }

    public synchronized String update(String input) {
        String[] parts = input.split(" ", 2);
        if (parts.length != 2) {
            return "Invalid input format.";
        }

        String word = parts[0];
        String[] newMeanings = parts[1].split(";");

        if (dictionaryData.has(word)) {
            dictionaryData.put(word, new JSONArray(newMeanings));
            saveDictionary();
            return "Updated " + word + " successfully.";
        } else {
            return "Word not found.";
        }
    }

    private void saveDictionary() {
        try (FileWriter writer = new FileWriter(this.dictionaryFile)) {
            writer.write(dictionaryData.toString(4));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
