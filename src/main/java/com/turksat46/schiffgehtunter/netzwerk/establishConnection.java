package com.turksat46.schiffgehtunter.netzwerk;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.turksat46.schiffgehtunter.netzwerk.Server.startServer;

public class establishConnection{

    @FXML
    static ProgressBar progressBar = new ProgressBar();
    @FXML
    Button cancel = new Button();
    public Label ipAnzeige;
    Thread serverThread = new Thread(new Server());







    @FXML
    public void initialize() throws IOException {
        String ipAddress = getLocalIPAddress();
        if (ipAddress != null) {
            ipAnzeige.setText("Ihre IP-Adresse: " + ipAddress);
        } else {
            ipAnzeige.setText("IP-Adresse konnte nicht ermittelt werden.");
        }



        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    onCancelPressed();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        serverThread.start();

    }

    private void onCancelPressed() throws IOException {
        serverThread.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/turksat46/schiffgehtunter/createGame.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Neues Spiel erstellen");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        Stage thisstage = (Stage) cancel.getScene().getWindow();
        thisstage.close();
    }

    public static String getLocalIPAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostAddress(); // Rückgabe der IP-Adresse
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null; // Falls ein Fehler auftritt
        }
    }
}
