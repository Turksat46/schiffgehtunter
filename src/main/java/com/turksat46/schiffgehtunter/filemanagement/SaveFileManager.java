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

public class SaveFileManager {
    JFileChooser fileChooser = new JFileChooser();

    public SaveFileManager() {
        fileChooser.setDialogTitle("Spielstand laden/speichern");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Save-Dateien", "*.save"));
    }

    //Load Data
    public Map<String, Object> openFileChooser() {
        int returnValue = fileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            Gson gson = new GsonBuilder().create();
            try{
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
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public void openSaveFileChooserAndSave(String data) {
        int returnValue = fileChooser.showSaveDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            // Ensure the file has the .txt extension
            if (!filePath.endsWith(".save")) {
                file = new File(filePath + ".save");
            }
            try{
                FileWriter writer = new FileWriter(file.getAbsolutePath());
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
