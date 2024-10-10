package com.turksat46.schiffe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 720);
        stage.setTitle("Schiffe versenken");
        stage.setScene(scene);
        stage.show();

        FXMLLoader yeloader = new FXMLLoader(HelloApplication.class.getResource("main-game-view.fxml"));
        Scene yescene = new Scene(yeloader.load(), 900, 720);
        stage.setScene(yescene);
    }

    public static void main(String[] args) {
        launch();
    }
}