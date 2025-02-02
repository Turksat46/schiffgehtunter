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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.turksat46.schiffgehtunter.MultipayerMainGameController.shootShipMultiplayer;

public class Client implements Runnable{
    @FXML
    private volatile AnchorPane pane;
    @FXML
    private volatile TextField ipInput;
    @FXML
    private volatile Button connectButton;
    @FXML
    private volatile ProgressBar progressBar;

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


    private static int lastRow;
    private static int lastCol;

    static Stack<Integer> ships = new Stack<>();

    private static volatile String ip;

    private static volatile Stage primaryStage;



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



    @FXML
    public void initialize() throws IOException {
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
            ip = ipInput.getText();
            primaryStage = (Stage) connectButton.getScene().getWindow();

            System.out.println("Trying to connect to server at IP: " + ip + " on Port: " + port);
            if (clientThread.getState() == Thread.State.NEW) {
                clientThread.setDaemon(true);
                clientThread.start();
            } else if (!clientThread.isAlive()) {
                // Client-Thread wurde gestoppt oder läuft nicht mehr -> Neuen Thread erstellen
                clientThread = new Thread(new Client());
                clientThread.setDaemon(true);
                clientThread.start();

            } else {
                System.out.println("Connection thread is already running...");
            }
        });

    }

    @Override
    public void run() {
        try {
            startConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void startConnection() throws IOException {
        //progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        System.out.println("ip " +ip);
        s = new Socket(ip, port);

        System.out.println("Connection established.");

        //progressBar.setProgress(1.0);

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
                                gameController.setupSpiel(finalGroesse, newStage, 0, 1, scene, ships);

                                newStage.show();

                                primaryStage.close();
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
                    MultipayerMainGameController.currentState=2;
                    handleGame();
                    break;
            }
        }
    }

    private void handleGame () throws IOException {
        int answerCounterWin=0;
        final int numberOfShips = ships.size();

        while (true) {
            String message = receiveMessage();

            String[] parts = message.split(" ");
            switch (parts[0]) {
                case "shot":
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    System.out.println("Opponent shot at: (" + row + ", " + col + ")");




                    String answer = handleshot(row, col);
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

                    //wenn Treffer dann weiter ansonsten pass und schießen
                    break;

                case "answer":
                    if (parts[1].equals("0")) {
                        MultipayerMainGameController.currentState=2;
                        sendMessage("pass");
                        break;
                    }
                    else if (parts[1].equals("1")) {
                        MultipayerMainGameController.currentState=1;
                        MultipayerMainGameController.gegnerspielfeld.selectFeld(lastCol,lastRow, Color.GREEN);

                        break;
                    }
                    else if (parts[1].equals("2")) {
                        MultipayerMainGameController.currentState=1;
                        MultipayerMainGameController.gegnerspielfeld.selectFeld(lastCol,lastRow, Color.GREEN);
                        ships.pop();
                        checkWin();
                        break;
                    }
                    break;

                case "pass":
                   MultipayerMainGameController.currentState=1;
                   break;
            }
        }
    }
    public static void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Client sent: " + message);
    }

    private String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Client received: " + message);
        return message;
    }

    public String handleshot(int row, int col) throws IOException {
        //shootShipMultiplayer() liefert ob an dieser stelle ein schiff ist es zerstört wurde oder wasser
        if (shootShipMultiplayer(row,col)==0){
            return "0";
        }
        if (shootShipMultiplayer(row,col)==1){
            return "1";
        }
        if (shootShipMultiplayer(row,col)==2){
            return "2";
        }
        return null;
    }

    public static void setLastRowCol(int row, int col) {
        lastCol = col;
        lastRow = row;
    }

    private static void checkWin() throws IOException {
        if (ships.size()==0){
            MultipayerMainGameController temp = new MultipayerMainGameController();
            System.out.println("YOU WON");
            MultipayerMainGameController.currentState=2;
            Platform.runLater(() -> temp.handleWinForPlayer());
        }
    }


}
