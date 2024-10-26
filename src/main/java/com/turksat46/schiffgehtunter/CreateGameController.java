package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Difficulty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class CreateGameController {

    // cb = Schwierigkeit
    // cb2 = Spielstrategie

    @FXML
    ChoiceBox cb = new ChoiceBox();
    ChoiceBox cb2 = new ChoiceBox();

    @FXML
    Slider groesseslider = new Slider();
    @FXML
    TextField groessetextfield = new TextField();

    MainGameController mainGameController;

    ObservableList<Difficulty> difficulties = FXCollections.observableArrayList();

    public void initialize() {

        cb.setItems(FXCollections.observableArrayList("Noob", "Average", "Hardcore"));
        cb2.setItems(FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer"));
        groesseslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            groessetextfield.setText(Double.toString(newValue.intValue()));
        });
        groessetextfield.textProperty().addListener((observable, oldValue, newValue) -> {
                groesseslider.setValue(Double.valueOf(newValue));
        });

        mainGameController = new MainGameController();

    }

    public void onPlayPressed() throws IOException {
        startGame();
    }

    public void startGame() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-game-view.fxml"));
        // Load the FXML file and create a new scene
        Stage stage = new Stage();
        stage.setTitle("Spiel");
        stage.setScene(new Scene(fxmlLoader.load()));

        //init spielfeld
        mainGameController = fxmlLoader.getController();
        mainGameController.setupSpielfeld((int) groesseslider.getValue(), stage);

        // Show the stage
        stage.show();
    }

    public void onBackPressed() {
        Stage stage = (Stage) cb.getScene().getWindow();
        stage.close();
    }


}
