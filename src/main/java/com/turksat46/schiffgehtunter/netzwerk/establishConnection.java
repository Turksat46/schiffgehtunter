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
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Die {@code establishConnection} Klasse verwaltet den Prozess, eine Verbindung zu einem Server herzustellen.
 * Sie umfasst UI-Elemente wie eine Fortschrittsanzeige und eine Schaltfläche zum Abbrechen.
 * Die Klasse verwaltet die Initialisierung, die Anzeige der lokalen IP-Adresse,
 * das Thread-Management für das Starten und Unterbrechen des Servers
 * sowie die Aktionen, die mit dem Abbrechen des Verbindungsprozesses verbunden sind.
 */
public class establishConnection {

    /**
     * Fortschrittsanzeige, die den Status des Verbindungsprozesses anzeigt.
     */
    @FXML
    ProgressBar progressBar = new ProgressBar();

    /**
     * Schaltfläche, die es dem Benutzer ermöglicht, den Verbindungsprozess abzubrechen.
     */
    @FXML
    Button cancel = new Button();

    /**
     * Label, das die lokale IP-Adresse anzeigt.
     */
    public Label ipAnzeige;

    /**
     * Thread, der den Server ausführt.
     */
    public Thread serverThread = new Thread(new Server());

    /**
     * Initialisiert die {@code establishConnection}-Ansicht.
     * Setzt die lokale IP-Adresse und behandelt die Aktion des Abbrechen-Buttons.
     * Ein Server-Thread wird ebenfalls gestartet und dessen Fortschritt überwacht.
     *
     * @param primaryStage die Hauptbühne (Stage) der Anwendung
     * @throws IOException wenn ein I/O-Fehler bei der Initialisierung auftritt
     */
    @FXML
    public void initialize(Stage primaryStage) throws IOException {
        String ipAddress = getLocalIPAddress();
        if (ipAddress != null) {
            ipAnzeige.setText("Ihre IP-Adresse: " + ipAddress);
        } else {
            ipAnzeige.setText("IP-Adresse konnte nicht ermittelt werden.");
        }

        // Handle die Aktion des Abbrechen-Buttons
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if (serverThread.isAlive()) {
                        serverThread.interrupt(); // Unterbricht den Server-Thread
                        System.out.println("Server-Thread unterbrochen");
                    }
                    onCancelPressed(); // Handhabt das Ereignis des Abbrechen-Buttons
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Setzt die Fortschrittsanzeige auf einen unbestimmten Zustand
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        serverThread.setDaemon(true); // Setzt den Server-Thread als Daemon-Thread
        serverThread.start(); // Startet den Server-Thread

        // Handhabt das Ereignis des Schließens des Fensters
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (serverThread.isAlive()) {
                    serverThread.interrupt(); // Unterbricht den Server-Thread beim Schließen
                    System.out.println("Server-Thread unterbrochen");
                    System.out.println(Thread.currentThread().getName());
                }
                System.exit(0); // Schließt die Anwendung
            }
        });
    }

    /**
     * Handhabt die Aktion, wenn der Abbrechen-Button gedrückt wird.
     * Unterbricht den Server-Thread und öffnet das Fenster "Neues Spiel erstellen".
     *
     * @throws IOException wenn ein I/O-Fehler beim Laden der Szene auftritt
     */
    private void onCancelPressed() throws IOException {
        serverThread.interrupt(); // Unterbricht den Server-Thread
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/turksat46/schiffgehtunter/createGame.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Neues Spiel erstellen"); // Setzt den Titel für das "Neues Spiel erstellen"-Fenster
        stage.setScene(new Scene(fxmlLoader.load())); // Setzt die Szene für das "Neues Spiel erstellen"-Fenster
        stage.show(); // Zeigt das Fenster an
        Stage thisstage = (Stage) cancel.getScene().getWindow();
        thisstage.close(); // Schließt die aktuelle Bühne
    }

    /**
     * Gibt die lokale IP-Adresse des Computers zurück.
     *
     * @return die lokale IP-Adresse als {@code String}, oder {@code null}, wenn die Adresse nicht ermittelt werden konnte
     */
    public static String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress(); // Gibt die lokale IP-Adresse zurück
        } catch (UnknownHostException e) {
            e.printStackTrace(); // Gibt den Stack-Trace aus, falls ein Fehler auftritt
            return null; // Gibt null zurück, wenn die IP-Adresse nicht ermittelt werden konnte
        }
    }
}
