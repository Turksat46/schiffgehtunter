package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.MainGameController;
import com.turksat46.schiffgehtunter.MultipayerMainGameController;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.turksat46.schiffgehtunter.MultipayerMainGameController.shootShipMultiplayer;

public class Server implements Runnable {

    private static ServerSocket server;
    private static Socket s;
    private static BufferedReader in;
    private static Writer out;
    private static BufferedReader usr;
    private static final int port = 50000;
    public static boolean connectionEstablished; // Callback-Funktion
    private static int groesse;
    private static Stack<Integer> ships = new Stack<>();
    private static CountDownLatch latch = new CountDownLatch(1);// Latch hinzufügen


    private static int lastRow;
    private static int lastCol;


    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startServer() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Waiting for client connection ...");
        s = server.accept();
        System.out.println("Connection established.");
        connectionEstablished = true; // Verbindung hergestellt

        // Ein- und Ausgabestrom des Sockets ermitteln
        // und als BufferedReader bzw. Writer verpacken
        // (damit man zeilen- bzw. zeichenweise statt byteweise arbeiten kann).
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new OutputStreamWriter(s.getOutputStream());

        // Standardeingabestrom ebenfalls als BufferedReader verpacken.
        usr = new BufferedReader(new InputStreamReader(System.in));


        // Warten, bis das Latch freigegeben wird
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        handleGame();

        // EOF ins Socket "schreiben".
        s.shutdownOutput();
        System.out.println("Connection closed.");
    }

    public static void setGroesse(int x) {
        if (x == 0) {
            groesse = 5;
        }
        else {
            groesse = x;
        }
    }

    public static void setShips(List<Integer> ship) {
        ships.addAll(ship);
    }

    public static void releaseLatch() {
        latch.countDown(); // Latch freigeben
    }

    private static String formatShips(List<Integer> ships) {
        // Sortiere die Schiffe aufsteigend
        List<Integer> sortedShips = ships.stream()
                .sorted()
                .collect(Collectors.toList());

        // Konvertiere die sortierte Liste von Schiffen in eine durch Leerzeichen getrennte Zeichenkette
        return sortedShips.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }

    private static void handleGame() throws IOException {


        sendMessage("size "+ groesse);
        receiveMessage();


        sendMessage("ships " + formatShips(ships));
        receiveMessage();


        //warten bis schiffe gesetzt sind
        // Warten bis der Button gedrückt wurde
        while (!MultipayerMainGameController.isButtonClicked) {
            try {
                Thread.sleep(100); // Kurze Pause, um das UI zu ermöglichen
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sendMessage("ready");

        MultipayerMainGameController.currentState=1;

        if (receiveMessage().equals("ready")) {

            int answerCounterWin=0;
            final int numberOfShips = ships.size();
            while (true) {
                String message = receiveMessage();


                if (message == null) break;
                //abbruch bedingungen prüfen

                String[] parts = message.split(" ");
                switch (parts[0]) {


                    // Überarbeiten


                    case "shot":
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        System.out.println("Opponent shot at: (" + row + ", " + col + ")");



                        String answer = handleShot(row, col);
                        sendMessage("answer " + answer); // 0 wasser/ 1 schiff/ 2 versenkt
                        if (answer=="2") {
                            answerCounterWin = answerCounterWin + 1;
                            if (answerCounterWin==numberOfShips){
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
    }


    public static void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Server sent: " + message);
    }

    private static String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Server received: " + message);
        return message;
    }

    private static String handleShot(int row, int col) throws IOException {

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