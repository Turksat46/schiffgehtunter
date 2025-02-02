package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller-Klasse für das Pausenmenü des Spiels.
 * Diese Klasse verwaltet Benutzeraktionen im Pausenbildschirm,
 * einschließlich Fortsetzen, Speichern, Schließen des Spiels und Öffnen der Einstellungen.
 */
public class PauseGameController {
    @FXML
    public Button continuebutton;



    /**
     * Setzt das Spiel fort, indem das Pausenmenü geschlossen wird.
     */
    public void continueGame() {
        Stage stage = (Stage) continuebutton.getScene().getWindow();
        stage.close();
    }

    /**
     * Speichert das aktuelle Spiel über den Hauptspiel-Controller.
     */
    public void saveGame(){
        MainGameController mainGameController = new MainGameController();
        mainGameController.saveFile();
    }

    /**
     * Beendet das Spiel.
     */
    public void closeGame(){
        System.exit(0);
    }

    /**
     * Öffnet das Einstellungsmenü.
     *
     * @throws IOException Falls das Laden der FXML-Datei fehlschlägt.
     */
    public void openSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Einstellungen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }
}
