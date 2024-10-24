package com.turksat46.schiffgehtunter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class CreateGameController {

    @FXML
    ChoiceBox cb = new ChoiceBox();

    public void initialize() {
        cb.setItems(FXCollections.observableArrayList("Noob", "Average", "Hardcore"));
    }

    public void onBackPressed() {
        Stage stage = (Stage) cb.getScene().getWindow();
        stage.close();
    }
}
