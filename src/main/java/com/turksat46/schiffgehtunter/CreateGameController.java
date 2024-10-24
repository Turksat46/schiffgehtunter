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
        Difficulty leicht = new Difficulty(0, "Noob");
        difficulties.add(leicht);
        Difficulty mittel = new Difficulty(1, "Average");
        difficulties.add(mittel);
        Difficulty schwer = new Difficulty(2, "Hardcore");
        difficulties.add(schwer);
        //cb.setItems(FXCollections.observableArrayList("Noob", "Average", "Hardcore"));
        cb.setItems();
        cb2.setItems(FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer"));
        mainGameController = new MainGameController();
        groessetextfield.setText(String.valueOf(groesseslider.getValue()));
    }

    public void updateTextField(){
        groessetextfield.setText(String.valueOf(groesseslider.getValue()));
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
        mainGameController.setupSpielfeld((int)groesseslider.getValue());
    }

    public void onBackPressed() {
        Stage stage = (Stage) cb.getScene().getWindow();
        stage.close();
    }
}
