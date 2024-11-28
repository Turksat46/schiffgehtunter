package com.turksat46.schiffgehtunter.filemanagement;

import javafx.stage.FileChooser;

import java.io.File;

public class SaveFileManager {
    FileChooser fileChooser = new FileChooser();

    public SaveFileManager() {
        fileChooser.setTitle("Open Save file");
        fileChooser.setInitialDirectory(new File("C:\\Users\\public\\Documents"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save-Dateien", "*.save"));
    }

    public void openFileChooser() {
        File save = fileChooser.showOpenDialog(null);
    }



}
