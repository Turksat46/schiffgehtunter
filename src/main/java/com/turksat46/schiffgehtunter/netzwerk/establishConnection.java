package com.turksat46.schiffgehtunter.netzwerk;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import static com.turksat46.schiffgehtunter.netzwerk.Server.startServer;

public class establishConnection{

    @FXML
    ProgressBar progressBar = new ProgressBar();
    @FXML
    Button cancel = new Button();
    public Label ipAnzeige;
    Thread serverThread = new Thread(new Server());





    @FXML
    public void initialize(Stage primaryStage) throws IOException {
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
                    if (serverThread.isAlive()) {

                        /*  Überprüfungen serverThread
                        System.out.println("Thread Name: " + serverThread.getName());
                        System.out.println("  ID: " + serverThread.getId());
                        System.out.println("  Status: " + serverThread.getState());
                        System.out.println("  Alive: " + serverThread.isAlive());
                        System.out.println("  Is Interruptet: " + serverThread.isInterrupted());
                        System.out.println("  Is Daemon: " + serverThread.isDaemon());
                        System.out.println();
                         */


                        serverThread.interrupt();// Thread unterbrechen
                        System.out.println("Server thread interrupted");

                        /*  Überprüfungen serverThread
                        System.out.println("Thread Name: " + serverThread.getName());
                        System.out.println("  ID: " + serverThread.getId());
                        System.out.println("  Status: " + serverThread.getState());
                        System.out.println("  Alive: " + serverThread.isAlive());
                        System.out.println("  Is Interruptet: " + serverThread.isInterrupted());
                        System.out.println("  Is Daemon: " + serverThread.isDaemon());
                        System.out.println();
                         */
                    }
                    onCancelPressed();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        serverThread.setDaemon(true);
        serverThread.start();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (serverThread.isAlive()) {
                    serverThread.interrupt();// Thread unterbrechen
                    System.out.println("Server thread interrupted");
                    System.out.println(Thread.currentThread().getName());

                }
                System.exit(0);
            }
        });


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
