package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.CreateGameController;
import com.turksat46.schiffgehtunter.MainGameController;
import com.turksat46.schiffgehtunter.MultipayerMainGameController;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;

public class Client implements Runnable{
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

    static MultipayerMainGameController gameController;


    final int port = 50000;
    Socket server;
    Socket s;
    private Writer out;
    private BufferedReader in;
    private BufferedReader usr;

    private static Thread clientThread = new Thread(new Client());


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

    @Override
    public void run() {
        try {
            startConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() throws IOException {
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

        connectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!clientThread.isAlive()) { // Überprüfen, ob der Thread schon läuft
                    clientThread.setDaemon(true);
                    clientThread.start();
                }
            }
        });
    }


    public void startConnection() throws IOException {
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        s = new Socket(ipInput.getText(), port);
        System.out.println("Connection established.");

        progressBar.setProgress(1.0);

        // Ein- und Ausgabestrom des Sockets ermitteln
        // und als BufferedReader bzw. Writer verpacken
        // (damit man zeilen- bzw. zeichenweise statt byteweise arbeiten kann).
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new OutputStreamWriter(s.getOutputStream());

        // Standardeingabestrom ebenfalls als BufferedReader verpacken.
        usr = new BufferedReader(new InputStreamReader(System.in));

        initializeGame();

    }

    private void initializeGame() throws IOException {
        int groesse = 0;
        List<Integer> ships = new ArrayList<>();
        while (true) {
            String message = receiveMessage();
            if (message == null) break;

            String[] parts = message.split(" ");
            switch (parts[0]) {
                case "size":
                    System.out.println("Received size: " + message);
                    groesse = Integer.parseInt(parts[1]);
                    sendMessage("done");
                    break;
                case "ships":
                    System.out.println("Received ships: " + message);
                    for (int i = 1; i < parts.length; i++) {
                        ships.add(Integer.parseInt(parts[i]));
                    }
                    sendMessage("done");

                    if(groesse!=0 && ships.size()!=0) {

                        int finalGroesse = groesse;
                        Platform.runLater(() -> {

                            try {

                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/turksat46/schiffgehtunter/multiplayer-main-game-view.fxml"));
                                Scene scene = new Scene(fxmlLoader.load());
                                Stage stage = new Stage();
                                stage.setTitle("Spielfeld");
                                stage.setScene(scene);

                                MultipayerMainGameController gameController = fxmlLoader.getController();
                                gameController.setupSpiel(finalGroesse, stage, 0, 1, scene, ships);

                                stage.show();


                                //Problem mit schließen der alten Stage!!


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    break;
                case "ready":


                    //warten bis schiffe gesetzt sind
                    // Warten bis der Button gedrückt wurde
                    while (!MultipayerMainGameController.isButtonClicked) {
                        try {
                            clientThread.sleep(100); // Kurze Pause, um das UI zu ermöglichen
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    sendMessage("ready");

                    break;
            }
        }
    }

    private void handleGame () throws IOException {
        while (true) {
            String message = receiveMessage();
            if (message == null) break;

            String[] parts = message.split(" ");
            switch (parts[0]) {


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
