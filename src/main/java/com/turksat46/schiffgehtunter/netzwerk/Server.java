package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.CreateGameController;
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

public class Server implements Runnable {

    private static ServerSocket server;
    private static Socket s;
    private static BufferedReader in;
    private static Writer out;
    private static BufferedReader usr;
    private static final int port = 50000;
    public static boolean connectionEstablished; // Callback-Funktion

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


    private static void handleGame() throws IOException {
        //richtige size und ships senden
        sendMessage("size 10");
        receiveMessage();


        sendMessage("ships 5 4 4 3 3 3 2 2");
        receiveMessage();

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
                    //Treffer?

                    sendMessage("answer 1");
                    //wenn Treffer dann weiter ansonsten pass und schießen
                }
            }
        }
    }


    private static void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Server sent: " + message);
    }

    private static String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Server received: " + message);
        return message;
    }

}