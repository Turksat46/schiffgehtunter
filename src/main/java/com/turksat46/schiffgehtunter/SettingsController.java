package com.turksat46.schiffgehtunter;


import com.turksat46.schiffgehtunter.filemanagement.SettingsFileManager;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Der SettingsController verwaltet die Einstellungen der Anwendung,
 * insbesondere die Musiksteuerung und Lautstärkeregulierung.
 */
public class SettingsController {
    Music player = Music.getInstance();
    @FXML
    CheckBox toggleMusic;

    @FXML
    Slider volumeSlider;

    @FXML
    Button speichern;

    SettingsFileManager fileManager = new SettingsFileManager();

    /**
     * Initialisiert die Einstellungen, setzt die gespeicherten Werte
     * und fügt Event-Listener für UI-Komponenten hinzu.
     */
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

    /**
     * Lädt die gespeicherten Einstellungen und wendet sie an.
     */
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



    /**
     * Schließt das aktuelle Einstellungsfenster.
     *
     * @param actionEvent Das Event, das durch den Button-Klick ausgelöst wurde
     * @throws IOException Falls ein Fehler beim Schließen des Fensters auftritt
     */
    public void onBackPressed(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) toggleMusic.getScene().getWindow();
        stage.close();


    }
}