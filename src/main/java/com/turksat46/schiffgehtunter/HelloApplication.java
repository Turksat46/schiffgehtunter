package com.turksat46.schiffgehtunter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private Stage stage = null;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 420);
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();
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