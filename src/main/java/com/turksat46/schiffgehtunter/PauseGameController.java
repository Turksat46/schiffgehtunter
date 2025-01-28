package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
}
