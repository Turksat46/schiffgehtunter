package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.CreateGameController;
import com.turksat46.schiffgehtunter.CreateGameController.*;
import com.turksat46.schiffgehtunter.MultipayerMainGameController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

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

    public static void setShips(List<Integer> ships) {
        Server.ships = ships;
    }

    private static void handleGame() throws IOException {


        sendMessage("size "+ groesse);
        receiveMessage();

        //Richtige schiffe übergeben
        sendMessage("ships 3 2 2");
        receiveMessage();


        //warten bis schiffe gesetzt sind


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