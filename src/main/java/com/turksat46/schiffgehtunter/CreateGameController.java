package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Difficulty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class CreateGameController {

    // cb = Schwierigkeit
    // cb2 = Spielstrategie

    @FXML
    ChoiceBox cb = new ChoiceBox();
    @FXML
    ChoiceBox cb2 = new ChoiceBox();

    @FXML
    Slider groesseslider = new Slider();
    @FXML
    TextField groessetextfield = new TextField();

    MainGameController mainGameController;

    ObservableList<Difficulty> difficulties = FXCollections.observableArrayList();

    ObservableList<String> skillLevels = FXCollections.observableArrayList("Noob", "Average", "Hardcore");
    ObservableList<String> gameModes = FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer");


    public void initialize() {
        cb.setItems(skillLevels);
        cb2.setItems(gameModes);

        cb.setValue(skillLevels.get(0));
        cb2.setValue(gameModes.get(0));
        groessetextfield.setText(Double.toString(groesseslider.getValue()));
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

        Stage stage = new Stage();
        stage.setTitle("Spiel");
        stage.setScene(new Scene(fxmlLoader.load()));

        mainGameController = fxmlLoader.getController();
        mainGameController.setCurrentMode(cb2.getSelectionModel().getSelectedIndex());
        mainGameController.setupSpielfeld((int)groesseslider.getValue(), stage);
        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }

    public void onBackPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Hello-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        Stage thisstage = (Stage) cb.getScene().getWindow();
        thisstage.close();
    }


}
