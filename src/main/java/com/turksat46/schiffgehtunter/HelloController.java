package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;


    @FXML
    protected void onHelloButtonClick() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGame.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Neues Spiel erstellen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        hideCurrentStage();
    }

    @FXML
    protected void onMultiplayerClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("createGame.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Spiel finden");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        hideCurrentStage();
    }

    public void hideCurrentStage() {
        Stage stage = (Stage) welcomeText.getScene().getWindow();
        stage.hide();
    }

    @FXML
    protected void onSettingsButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Einstellungen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    @FXML
    protected void onExitButtonClick() {
        System.exit(0);
    }
}