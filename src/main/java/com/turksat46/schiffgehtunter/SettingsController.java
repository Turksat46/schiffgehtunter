package com.turksat46.schiffgehtunter;


import com.turksat46.schiffgehtunter.filemanagement.SettingsFileManager;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;


public class SettingsController {
    Music player = Music.getInstance();
    @FXML
    CheckBox toggleMusic;

    @FXML
    Slider volumeSlider;

    @FXML
    Button speichern;

    SettingsFileManager fileManager = new SettingsFileManager();


    public void initialize() {



        volumeSlider.setMin(0);
        volumeSlider.setMax(1);

        volumeSlider.setValue(fileManager.getVolume());
        player.volume(volumeSlider.getValue());

        volumeSlider.setBlockIncrement(0.01);

        // Füge Listener zum Lautstärkeregler hinzu
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Lautstärke anpassen, wenn der Slider bewegt wird
            player.volume(newValue.doubleValue());
        });


        toggleMusic.setSelected(fileManager.isMusicEnabled());

        toggleMusic.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Wenn die CheckBox aktiviert wird, Musik stoppen
                System.out.println("Musik wird gestoppt");
                player.stop();
            } else {
                // Wenn die CheckBox deaktiviert wird, Musik abspielen
                System.out.println("Musik wird abgespielt");
                player.play();
            }
        });

        speichern.setOnAction(event -> {
            fileManager.saveSettings( toggleMusic.isSelected(), volumeSlider.getValue());
            System.out.println("Speichern");
        });


    }

    public void setSettings() {

        boolean musicEnabled = fileManager.isMusicEnabled();
        double volume = fileManager.getVolume();

        // Setze die Musik ein/aus
        toggleMusic.setSelected(musicEnabled);
        if (musicEnabled) {
            player.stop();  // Musik abspielen
        } else {
            player.play();  // Musik stoppen
        }

        // Setze den Lautstärkeregler auf den angegebenen Wert
        volumeSlider.setValue(volume);
        player.volume(volume);

        // Speichere die Einstellungen, falls erforderlich
        fileManager.saveSettings(musicEnabled, volume);
    }
}