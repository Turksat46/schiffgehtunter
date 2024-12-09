package com.turksat46.schiffgehtunter.netzwerk;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Client {
    @FXML
    AnchorPane pane;
    @FXML
    TextField ipInput = new TextField();
    @FXML
    Button connectButton = new Button();
    @FXML
    ProgressBar progressBar = new ProgressBar();

    @FXML
    Button backButton = new Button();

    final int port = 50000;
    Socket server;
    Socket s;
    private Writer out;
    private BufferedReader in;
    private BufferedReader usr;

    public void onBackPressed() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/turksat46/schiffgehtunter/hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(new Scene(root));
        stage.show();
        Stage thisstage = (Stage) backButton.getScene().getWindow();
        thisstage.close();
    }


    public void initialize() {
        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    startConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

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


    public void startConnection() throws IOException {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        s = new Socket(ipInput.getText(), port);
        System.out.println("Connection established.");

        // Ein- und Ausgabestrom des Sockets ermitteln
        // und als BufferedReader bzw. Writer verpacken
        // (damit man zeilen- bzw. zeichenweise statt byteweise arbeiten kann).
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new OutputStreamWriter(s.getOutputStream());

        // Standardeingabestrom ebenfalls als BufferedReader verpacken.
        usr = new BufferedReader(new InputStreamReader(System.in));

        handleGame();

    }
    private void handleGame () throws IOException {
        while (true) {
            String message = receiveMessage(); // Nachricht vom Server
            if (message == null) break;

            String[] parts = message.split(" ");
            switch (parts[0]) {
                case "size":
                    System.out.println("Received size: " + message);
                    sendMessage("done");
                    break;
                case "ships":
                    System.out.println("Received ships: " + message);
                    sendMessage("done");
                    break;
                case "ready":
                    //Sollte nur sein wenn size und ships empfangen wurde
                    System.out.println("Server is ready.");
                    sendMessage("ready");
                    break;
                // Überarbeiten
                case "shot":
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    System.out.println("Opponent shot at: (" + row + ", " + col + ")");
                    //handle schuss
                    handleMultiplayerShoot(row, col);
                    sendMessage("answer 0"); // wasser/schiff
                    //wenn Treffer dann weiter ansonsten pass und schießen
            }
        }
    }
    private void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Client sent: " + message);
    }

    private String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Client received: " + message);
        return message;
    }

    public String handleMultiplayerShoot(int row, int col) throws IOException {
        return null;
    }


}
