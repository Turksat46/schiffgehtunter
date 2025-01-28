package com.turksat46.schiffgehtunter.filemanagement;

import com.google.gson.Gson;
import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveFileManager {
    JFileChooser fileChooser = new JFileChooser();

    public SaveFileManager() {
        fileChooser.setDialogTitle("Spielstand laden/speichern");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Save-Dateien", "*.save"));
    }

    public void openFileChooser() {
        int returnValue = fileChooser.showOpenDialog(null);
    }

    public void openSaveFileChooserAndSave(String data) {
        int returnValue = fileChooser.showSaveDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            // Ensure the file has the .txt extension
            if (!filePath.endsWith(".txt")) {
                file = new File(filePath + ".save");
            }
            try{
                FileWriter writer = new FileWriter(file.getAbsolutePath());
                writer.write("Test");
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
