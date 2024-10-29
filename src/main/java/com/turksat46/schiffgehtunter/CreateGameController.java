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
    Slider groessehoeheslider = new Slider();
    @FXML
    Slider groessebreiteslider = new Slider();
    @FXML
    TextField groessehoehetextfield = new TextField();
    @FXML
    TextField groessebreitetextfield = new TextField();

    MainGameController mainGameController;

    ObservableList<Difficulty> difficulties = FXCollections.observableArrayList();

    public void initialize() {

        cb.setItems(FXCollections.observableArrayList("Noob", "Average", "Hardcore"));
        cb2.setItems(FXCollections.observableArrayList("Spieler vs. Computer", "Spieler vs. Spieler", "Computer vs. Computer"));
        groessehoehetextfield.setText(Double.toString(groessehoeheslider.getValue()));
        groessebreitetextfield.setText(Double.toString(groessebreiteslider.getValue()));
        groessehoeheslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            groessehoehetextfield.setText(Double.toString(newValue.intValue()));
        });
        groessehoehetextfield.textProperty().addListener((observable, oldValue, newValue) -> {
                groessehoeheslider.setValue(Double.valueOf(newValue));
        });

        groessebreiteslider.valueProperty().addListener((observable, oldValue, newValue) -> {
            groessebreitetextfield.setText(Double.toString(newValue.intValue()));
        });
        groessebreitetextfield.textProperty().addListener((observable, oldValue, newValue) ->{
            groessebreiteslider.setValue(Double.valueOf(newValue));
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
        mainGameController.setupSpielfeld((int)groessehoeheslider.getValue(),(int)groessebreiteslider.getValue(), stage);
        stage.show();
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
