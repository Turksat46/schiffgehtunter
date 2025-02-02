package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.CreateGameController;
import com.turksat46.schiffgehtunter.MainGameController;
import com.turksat46.schiffgehtunter.MultipayerMainGameController;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.turksat46.schiffgehtunter.MultipayerMainGameController.dyeCell;
import static com.turksat46.schiffgehtunter.MultipayerMainGameController.shootShipMultiplayer;

/**
 * Die Client-Klasse ermöglicht die Kommunikation eines Clients in einem Multiplayer-Spiel.
 * Sie stellt eine Verbindung zu einem Server her, empfängt und sendet Nachrichten,
 * und steuert das Spiel zwischen zwei Spielern.
 */
public class Client implements Runnable {
    @FXML
    private volatile AnchorPane pane;
    @FXML
    private volatile TextField ipInput;
    @FXML
    private volatile Button connectButton;
    @FXML
    private volatile Button backButton = new Button();

    static MultipayerMainGameController gameController;

    final int port = 50000;
    Socket server;
    Socket s;
    private static Writer out;
    private BufferedReader in;
    private BufferedReader usr;

    private static Thread clientThread = new Thread(new Client());

    private static int lastx;
    private static int lasty;

    static Stack<Integer> ships = new Stack<>();

    private static volatile String ip;

    private static volatile Stage primaryStage;

    @FXML
    private volatile ChoiceBox cbGameMode = new ChoiceBox();
    private static volatile int gameMode = 0;

    ObservableList<String> gameModes = FXCollections.observableArrayList("Spieler", "Computer");

    /**
     * Wird aufgerufen, wenn der Benutzer auf den "Zurück"-Button klickt.
     * Lädt die Hauptmenü-Szene und schließt das aktuelle Fenster.
     *
     * @param event Das Ereignis, das den Button-Klick repräsentiert.
     * @throws IOException Wenn beim Laden der FXML-Datei ein Fehler auftritt.
     */
    @FXML
    private void onBackPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/turksat46/schiffgehtunter/hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(new Scene(root));
        stage.show();
        Stage thisstage = (Stage) backButton.getScene().getWindow();
        thisstage.close();
    }

    /**
     * Initialisiert die Benutzeroberfläche des Clients und setzt Event-Handler für die Schaltflächen.
     *
     * @throws IOException Wenn beim Laden von Ressourcen ein Fehler auftritt.
     */
    @FXML
    public void initialize() throws IOException {
        cbGameMode.setItems(gameModes);
        cbGameMode.getSelectionModel().select(0);

        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    onBackPressed(actionEvent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        connectButton.setOnAction(actionEvent -> {
            gameMode = cbGameMode.getSelectionModel().getSelectedIndex();
            ip = ipInput.getText();
            primaryStage = (Stage) connectButton.getScene().getWindow();

            System.out.println("Trying to connect to server at IP: " + ip + " on Port: " + port);
            if (clientThread.getState() == Thread.State.NEW) {
                clientThread.setDaemon(true);
                clientThread.start();
            } else if (!clientThread.isAlive()) {
                clientThread = new Thread(new Client());
                clientThread.setDaemon(true);
                clientThread.start();
            } else {
                System.out.println("Connection thread is already running...");
            }
        });
    }

    /**
     * Wird im neuen Thread ausgeführt, um die Verbindung zum Server herzustellen.
     *
     * @throws IOException Wenn bei der Verbindung zum Server ein Fehler auftritt.
     */
    @Override
    public void run() {
        try {
            startConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stellt die Verbindung zum Server her und initialisiert das Spiel.
     *
     * @throws IOException Wenn bei der Verbindung oder Kommunikation mit dem Server ein Fehler auftritt.
     */
    public void startConnection() throws IOException {
        System.out.println("ip " +ip);
        s = new Socket(ip, port);

        System.out.println("Connection established.");

        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new OutputStreamWriter(s.getOutputStream());

        usr = new BufferedReader(new InputStreamReader(System.in));

        initializeGame();
    }

    /**
     * Initialisiert das Spiel, indem Nachrichten vom Server empfangen und verarbeitet werden.
     *
     * @throws IOException Wenn beim Empfangen von Nachrichten oder der Initialisierung des Spiels ein Fehler auftritt.
     */
    private void initializeGame() throws IOException {
        int groesse = 0;
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
                    System.out.println(ships.size());
                    sendMessage("done");

                    if(groesse!=0 && ships.size()!=0) {
                        int finalGroesse = groesse;
                        Platform.runLater(() -> {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/turksat46/schiffgehtunter/multiplayer-main-game-view.fxml"));
                                Scene scene = new Scene(fxmlLoader.load());
                                Stage newStage = new Stage();
                                newStage.setTitle("Spielfeld");
                                newStage.setScene(scene);

                                MultipayerMainGameController gameController = fxmlLoader.getController();
                                gameController.setupSpiel(finalGroesse, newStage, 0, gameMode, scene, ships);

                                newStage.show();

                                primaryStage.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    break;
                case "ready":
                    while (!MultipayerMainGameController.isButtonClicked) {
                        try {
                            clientThread.sleep(100); // Kurze Pause, um das UI zu ermöglichen
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    sendMessage("ready");
                    MultipayerMainGameController.currentState=2;
                    handleGame();
                    break;
            }
        }
    }

    /**
     * Handhabt die Spielereignisse während des Spiels.
     * Empfängt Nachrichten und führt je nach Inhalt bestimmte Aktionen aus, wie das Schießen auf Schiffe oder das Empfangen von Antworten.
     *
     * @throws IOException Wenn beim Empfangen von Nachrichten oder der Spielsteuerung ein Fehler auftritt.
     */
    private void handleGame () throws IOException {
        int answerCounterWin=0;
        final int numberOfShips = ships.size();

        while (true) {
            if (MultipayerMainGameController.isBotPlayer && MultipayerMainGameController.currentState == 1) {
                MultipayerMainGameController tmp = new MultipayerMainGameController();

                try {
                    tmp.executeAITurn();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            String message = receiveMessage();

            String[] parts = message.split(" ");
            switch (parts[0]) {
                case "shot":
                    int posx = Integer.parseInt(parts[1]);
                    int posy = Integer.parseInt(parts[2]);
                    System.out.println("Opponent shot at: (" + posx + ", " + posy + ")");
                    dyeCell(posx, posy);

                    String answer = handleshot(posx, posy);
                    sendMessage("answer " + answer); // 0 wasser/ 1 schiff/ 2 versenkt
                    if (answer=="2") {
                        answerCounterWin = answerCounterWin + 1;
                        if (answerCounterWin == numberOfShips){
                            MultipayerMainGameController temp2 = new MultipayerMainGameController();
                            MultipayerMainGameController.currentState=2;
                            System.out.println("LOOSER");
                            Platform.runLater(() -> temp2.handleWinForOpponent());
                            break;
                        }
                    }
                    break;

                case "answer":
                    if (parts[1].equals("0")) {
                        MultipayerMainGameController.currentState=2;
                        sendMessage("pass");
                        break;
                    }
                    else if (parts[1].equals("1")) {
                        MultipayerMainGameController.currentState=1;
                        MultipayerMainGameController.gegnerspielfeld.selectFeld(lastx,lasty, Color.GREEN);
                        break;
                    }
                    else if (parts[1].equals("2")) {
                        MultipayerMainGameController.currentState=1;
                        MultipayerMainGameController.gegnerspielfeld.selectFeld(lastx,lasty, Color.GREEN);
                        ships.pop();
                        checkWin();
                        break;
                    }

                case "pass":
                    MultipayerMainGameController.currentState=1;
                    break;

            }
        }
    }

    /**
     * Sendet eine Nachricht an den Server.
     *
     * @param message Die Nachricht, die gesendet werden soll.
     * @throws IOException Wenn beim Senden der Nachricht ein Fehler auftritt.
     */
    public static void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Client sent: " + message);
    }

    /**
     * Empfängt eine Nachricht vom Server.
     *
     * @return Die empfangene Nachricht.
     * @throws IOException Wenn beim Empfangen der Nachricht ein Fehler auftritt.
     */
    private String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Client received: " + message);
        return message;
    }

    /**
     * Handhabt den Schuss eines Spielers und überprüft, ob ein Treffer, ein Schiff oder ein versenktes Schiff getroffen wurde.
     *
     * @param posx Die X-Position des Schusses.
     * @param posy Die Y-Position des Schusses.
     * @return Ein String, der den Status des Schusses beschreibt ("0" = Wasser, "1" = Schiff, "2" = Versenkt).
     * @throws IOException Wenn beim Verarbeiten des Schusses ein Fehler auftritt.
     */
    public String handleshot(int posx, int posy) throws IOException {
        if (shootShipMultiplayer(posx,posy)==0){
            return "0";
        }
        if (shootShipMultiplayer(posx,posy)==1){
            return "1";
        }
        if (shootShipMultiplayer(posx,posy)==2){
            return "2";
        }
        return null;
    }

    /**
     * Setzt die letzten Schusskoordinaten (x, y).
     *
     * @param posx Die X-Position des Schusses.
     * @param posy Die Y-Position des Schusses.
     */
    public static void setLastRowCol(int posx, int posy) {
        lastx = posx;
        lasty = posy;
    }

    /**
     * Überprüft, ob der Spieler gewonnen hat, indem überprüft wird, ob noch Schiffe übrig sind.
     *
     * @throws IOException Wenn beim Überprüfen des Spielstands ein Fehler auftritt.
     */
    private static void checkWin() throws IOException {
        if (ships.size()==0){
            MultipayerMainGameController temp = new MultipayerMainGameController();
            System.out.println("YOU WON");
            MultipayerMainGameController.currentState=2;
            Platform.runLater(() -> temp.handleWinForPlayer());
        }
    }
}
