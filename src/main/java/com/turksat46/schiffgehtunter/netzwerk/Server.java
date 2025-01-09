package com.turksat46.schiffgehtunter.netzwerk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Server {

    private ServerSocket server;
    private Socket s;
    private BufferedReader in;
    private Writer out;
    private BufferedReader usr;
    private final int port = 50000;

    public void startServer() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Waiting for client connection ...");
        s = server.accept();
        System.out.println("Connection established.");

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


    private void handleGame() throws IOException {
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



    private void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Server sent: " + message);
    }

    private String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Server received: " + message);
        return message;
    }

    private String getIp (){
        return null;
    }
}