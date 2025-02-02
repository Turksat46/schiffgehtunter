package com.turksat46.schiffgehtunter.netzwerk;

import com.turksat46.schiffgehtunter.MultipayerMainGameController;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.turksat46.schiffgehtunter.MultipayerMainGameController.dyeCell;
import static com.turksat46.schiffgehtunter.MultipayerMainGameController.shootShipMultiplayer;

/**
 * Die {@code Server} Klasse repräsentiert einen Multiplayer-Spielserver, der die Kommunikation und den Spielfluss verwaltet.
 * Der Server wartet auf eine Verbindung vom Client, empfängt und sendet Nachrichten und steuert den Spielstatus, wie z.B. Schüsse und Schiffzustände.
 * Er verwaltet auch den Zugablauf zwischen Client und Server.
 */
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
    private static CountDownLatch latch = new CountDownLatch(1); // Latch zum Synchronisieren des Spielablaufs
    private static int lastx;
    private static int lasty;

    /**
     * Startet den Server, indem er auf eine Client-Verbindung wartet und den Spielfluss verwaltet.
     */
    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialisiert den Server-Socket, wartet auf eine Client-Verbindung und verarbeitet die Spielkommunikation.
     * @throws IOException Falls ein Problem mit der Eingabe/Ausgabe während der Verbindung oder der Kommunikation auftritt.
     */
    public static void startServer() throws IOException {
        server = new ServerSocket(port);
        System.out.println("Warte auf die Verbindung des Clients ...");
        s = server.accept();
        System.out.println("Verbindung hergestellt.");
        connectionEstablished = true;

        // Setze Eingabe- und Ausgabeströme für die Socket-Kommunikation
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new OutputStreamWriter(s.getOutputStream());

        // Setze den Standard-Eingabestrom für Benutzerinteraktionen
        usr = new BufferedReader(new InputStreamReader(System.in));

        // Warte, bis das Latch freigegeben wird, bevor das Spiel fortgesetzt wird
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handleGame();

        // Schalte den Ausgabestrom ab
        s.shutdownOutput();
        System.out.println("Verbindung geschlossen.");
    }

    /**
     * Setzt die Größe des Spielbretts.
     * @param x Die Größe des Spielfelds. Wenn 0 übergeben wird, wird die Größe auf 5 gesetzt.
     */
    public static void setGroesse(int x) {
        if (x == 0) {
            groesse = 5;
        } else {
            groesse = x;
        }
    }

    /**
     * Fügt eine Liste von Schiffen zum Spiel hinzu.
     * @param ship Eine Liste von Schiff-Positionen, die als Ganzzahlen dargestellt werden.
     */
    public static void setShips(List<Integer> ship) {
        ships.addAll(ship);
    }

    /**
     * Gibt das Latch frei, um das Spiel fortzusetzen.
     */
    public static void releaseLatch() {
        latch.countDown();
    }

    /**
     * Formatiert die Liste der Schiffe zu einer durch Leerzeichen getrennten Zeichenkette,
     * nachdem diese in aufsteigender Reihenfolge sortiert wurde.
     * @param ships Eine Liste von Schiff-Positionen.
     * @return Eine durch Leerzeichen getrennte Zeichenkette von sortierten Schiffs-Positionen.
     */
    private static String formatShips(List<Integer> ships) {
        List<Integer> sortedShips = ships.stream()
                .sorted()
                .collect(Collectors.toList());
        return sortedShips.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));
    }

    /**
     * Verarbeitet die Spiel-Logik, einschließlich des Empfangs und Sendens von Nachrichten
     * zu Spielaktionen. Diese Methode verwaltet den Spielfluss und die Spielerzüge.
     * @throws IOException Falls ein Problem mit der Eingabe/Ausgabe während der Spielkommunikation auftritt.
     */
    private static void handleGame() throws IOException {
        sendMessage("size " + groesse);
        receiveMessage();

        sendMessage("ships " + formatShips(ships));
        receiveMessage();

        // Warte, bis die Schiffe vom Client gesetzt wurden
        while (!MultipayerMainGameController.isButtonClicked) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sendMessage("ready");

        MultipayerMainGameController.currentState = 1;

        if (receiveMessage().equals("ready")) {

            int answerCounterWin = 0;
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

                if (message == null) break;

                // Prüfe Abbruchbedingungen

                String[] parts = message.split(" ");
                switch (parts[0]) {

                    case "shot":
                        int posx = Integer.parseInt(parts[1]);
                        int posy = Integer.parseInt(parts[2]);
                        System.out.println("Der Gegner hat auf folgende Position geschossen: (" + posx + ", " + posy + ")");
                        dyeCell(posx, posy);

                        String answer = handleShot(posx, posy);
                        sendMessage("answer " + answer); // 0 = Wasser / 1 = Schiff / 2 = Versenkt
                        if (answer.equals("2")) {
                            answerCounterWin = answerCounterWin + 1;
                            if (answerCounterWin == numberOfShips) {
                                MultipayerMainGameController temp2 = new MultipayerMainGameController();
                                MultipayerMainGameController.currentState = 2;
                                System.out.println("VERLOREN");
                                Platform.runLater(() -> temp2.handleWinForOpponent());
                                break;
                            }
                        }

                        // Wenn ein Treffer erfolgt, dann weitermachen, ansonsten "pass" und schießen
                        break;

                    case "answer":
                        if (parts[1].equals("0")) {
                            MultipayerMainGameController.currentState = 2;
                            sendMessage("pass");
                            break;
                        } else if (parts[1].equals("1")) {
                            MultipayerMainGameController.currentState = 1;
                            MultipayerMainGameController.gegnerspielfeld.selectFeld(lastx, lasty, Color.GREEN);
                            break;
                        } else if (parts[1].equals("2")) {
                            MultipayerMainGameController.currentState = 1;
                            MultipayerMainGameController.gegnerspielfeld.selectFeld(lastx, lasty, Color.GREEN);
                            ships.pop();
                            checkWin();
                            break;
                        }
                        break;

                    case "pass":
                        MultipayerMainGameController.currentState = 1;
                        break;
                }

            }
        }
    }

    /**
     * Sendet eine Nachricht an den Client.
     * @param message Die Nachricht, die an den Client gesendet werden soll.
     * @throws IOException Falls ein Problem beim Senden der Nachricht auftritt.
     */
    public static void sendMessage(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
        System.out.println("Server hat gesendet: " + message);
    }

    /**
     * Empfängt eine Nachricht vom Client.
     * @return Die empfangene Nachricht.
     * @throws IOException Falls ein Problem beim Empfangen der Nachricht auftritt.
     */
    private static String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Server hat empfangen: " + message);
        return message;
    }

    /**
     * Verarbeitet einen Schuss des Gegners, um zu überprüfen, ob er ein Schiff getroffen hat.
     * @param posx Die X-Position des Schusses.
     * @param posy Die Y-Position des Schusses.
     * @return Eine Zeichenkette, die den Status des Schusses angibt: "0" für Wasser, "1" für Schiff, "2" für Versenkt.
     * @throws IOException Falls ein Problem beim Verarbeiten des Schusses auftritt.
     */
    private static String handleShot(int posx, int posy) throws IOException {
        if (shootShipMultiplayer(posx, posy) == 0) {
            return "0";
        }
        if (shootShipMultiplayer(posx, posy) == 1) {
            return "1";
        }
        if (shootShipMultiplayer(posx, posy) == 2) {
            return "2";
        }
        return null;
    }

    /**
     * Setzt die letzte Position, an die ein Schuss abgegeben wurde.
     * @param posx Die X-Position des Schusses.
     * @param posy Die Y-Position des Schusses.
     */
    public static void setLastRowCol(int posx, int posy) {
        lastx = posx;
        lasty = posy;
    }

    /**
     * Überprüft, ob der Spieler gewonnen hat, indem er alle Schiffe des Gegners versenkt hat.
     * @throws IOException Falls ein Problem beim Überprüfen des Gewinns auftritt.
     */
    private static void checkWin() throws IOException {
        if (ships.size() == 0) {
            MultipayerMainGameController temp = new MultipayerMainGameController();
            System.out.println("DU HAST GEWONNEN");
            MultipayerMainGameController.currentState = 2;
            Platform.runLater(() -> temp.handleWinForPlayer());
        }
    }
}
