package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.MultipayerMainGameController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
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
    private static List<Integer> ships;
    private static CountDownLatch latch = new CountDownLatch(1); // Latch hinzufügen

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
        ships = ship;
    }

    public static void releaseLatch() {
        latch.countDown(); // Latch freigeben
    }

    private static String formatShips(List<Integer> ships) {
        // Konvertiere die Liste von Schiffen in eine durch Leerzeichen getrennte Zeichenkette
        return ships.stream()
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

        if (receiveMessage().equals("ready")) {


            while (true) {
                String message = receiveMessage();
                if (message == null) break;
                //abbruch bedingungen prüfen

                String[] parts = message.split(" ");
                if (parts[0].equals("shot")) {
                    int row = Integer.parseInt(parts[1]);
                    int col = Integer.parseInt(parts[2]);
                    reciveShoot(row, col); // antwort Senden


                    //wenn Treffer dann weiter ansonsten pass und schießen

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

    private static void reciveShoot(int row, int col) throws IOException {

        //shootShipMultiplayer() liefert ob an dieser stelle ein schiff ist es zerstört wurde oder wasser
        if (shootShipMultiplayer(row,col)==0){
            sendMessage("answer 0");
        }
        if (shootShipMultiplayer(row,col)==1){
            sendMessage("answer 1");
        }
        if (shootShipMultiplayer(row,col)==2){
            sendMessage("answer 2");
        }
    }

    private static boolean shoot() throws IOException {

        //feld auf das gecklickt wurde übergeben

        return true;
    }

}