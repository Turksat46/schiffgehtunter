package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Music;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private Stage stage = null;

    Music music = Music.getInstance();

    SettingsController settingsController;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 420);
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();
        music.play();
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("settings.fxml"));
        fxmlLoader2.load();
        settingsController = fxmlLoader2.getController();
        settingsController.setSettings();
    }

    public void setScene(String pfadFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(pfadFXML));
        Scene scene = new Scene(root);
        stage.setScene(scene);      // Scene setzen
        stage.show();               // Scene zeigen
    }

    public void showSettings() throws IOException {
        setScene("settings.fxml");
    }

    public static void main(String[] args) {
        launch();
    }
}