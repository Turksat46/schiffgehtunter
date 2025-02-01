package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class PauseGameController {
    @FXML
    public Button continuebutton;

    public void initialize(){

    }

    public void continueGame() {
        //TODO: Wenn sich iwas geändert hat, reverten
        Stage stage = (Stage) continuebutton.getScene().getWindow();
        stage.close();
    }

    public void saveGame(){
        MainGameController mainGameController = new MainGameController();
        mainGameController.saveFile();
    }

    public void closeGame(){
        //TODO: Erst vllt abfragen, ob Spiel gespeichert werden soll, oder prüfen, ob gespeichert wurde
        System.exit(0);
    }

    public void openSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Einstellungen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }
}
