package com.turksat46.schiffgehtunter.netzwerk;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class Client {

    @FXML
    TextField ipInput = new TextField();
    @FXML
    Button connectButton = new Button();
    @FXML
    ProgressBar progressBar = new ProgressBar();

    @FXML
    Button backButton = new Button();

    final int port = 50000;
    Socket s;

    public void onBackPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        Stage thisstage = (Stage) progressBar.getScene().getWindow();
        thisstage.close();
    }

    public void initialize() {
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                connect();

            }
        });

        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    onBackPressed();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void connect() {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    }
}
