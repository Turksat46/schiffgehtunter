package com.turksat46.schiffgehtunter;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateGameController {

    // cb = Schwierigkeit
    // cb2 = Spielstrategie

    @FXML
    ChoiceBox cb = new ChoiceBox();
    ChoiceBox cb2 = new ChoiceBox();

    MainGameController mainGameController;

    public void initialize() {
        cb.setItems(FXCollections.observableArrayList("Noob", "Average", "Hardcore"));
        cb2.setItems(FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer"));
        mainGameController = new MainGameController();
    }

    public void onPlayPressed() throws IOException {
        startGame();
    }

    public void startGame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));

        Stage stage = new Stage();
        stage.setTitle("Spiel");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        mainGameController.setupSpielfeld();
    }

    public void onBackPressed() {
        Stage stage = (Stage) cb.getScene().getWindow();
        stage.close();
    }
}
