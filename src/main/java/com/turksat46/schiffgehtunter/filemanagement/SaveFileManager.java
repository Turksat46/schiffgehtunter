package com.turksat46.schiffgehtunter.filemanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Die Klasse SaveFileManager verwaltet das Laden und Speichern von Spielstand-Dateien im JSON-Format.
 * Sie bietet Methoden, um eine Datei auszuwählen, die Daten zu laden und eine neue Datei zu speichern.
 */
public class SaveFileManager {
    JFileChooser fileChooser = new JFileChooser();

    /**
     * Konstruktor für die Klasse SaveFileManager. Dieser initialisiert den JFileChooser,
     * legt den Titel für den Dialog fest und beschränkt die Auswahl auf Dateien mit der Erweiterung .save.
     */
    public SaveFileManager() {
        fileChooser.setDialogTitle("Spielstand laden/speichern");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Save-Dateien", "save"));
    }

    /**
     * Öffnet den Datei-Chooser zum Laden einer Spielstand-Datei.
     * Liest die ausgewählte Datei im JSON-Format und gibt die Daten als Map zurück.
     *
     * @return Eine Map mit den geladenen Daten oder null, wenn der Benutzer den Vorgang abbricht.
     */
    public Map<String, Object> openFileChooser() {
        int returnValue = fileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            Gson gson = new GsonBuilder().create();
            try {
                FileReader fileReader = new FileReader(filePath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String jsonString = stringBuilder.toString();
                Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> map = gson.fromJson(jsonString, mapType);
                map.forEach((k, v) -> {
                    System.out.println(k + ": "+ v);
                });
                return map;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Öffnet den Datei-Chooser zum Speichern von Daten.
     * Wenn der Benutzer eine Datei auswählt, wird die übergebene Datenzeichenkette in einer .save-Datei gespeichert.
     *
     * @param data Die zu speichernden Daten als JSON-String.
     */
    public void openSaveFileChooserAndSave(String data) {
        int returnValue = fileChooser.showSaveDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            // Sicherstellen, dass die Datei die Endung .save hat
            if (!filePath.endsWith(".save")) {
                file = new File(filePath + ".save");
            }
            try {
                FileWriter writer = new FileWriter(file.getAbsolutePath());
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
