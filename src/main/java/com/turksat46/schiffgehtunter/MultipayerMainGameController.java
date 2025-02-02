package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.netzwerk.Client;
import com.turksat46.schiffgehtunter.netzwerk.Server;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class MultipayerMainGameController extends MainGameController implements Initializable {

    /**
     * State ist für die den status des Spiels also ob man verteidigt oder schiffe platziert oder angreift
     * mode ozeigt an in welchen modus man ist also SpielervSpieler, ComputervComputer oder SpielervComputer
     */
    @FXML
    public BorderPane spielerstackpane, gegnerstackpane;
    @FXML AnchorPane anchorPane;
    @FXML public HBox container;
    @FXML public Pane images;
    @FXML public HBox label1;
    @FXML private Button startButton;
    public static int currentState, currentMode, currentDifficulty, groesse;
    private static AI bot;;
    static newSpielfeld spielerspielfeld;
    public static Spielfeld gegnerspielfeld;
    public static volatile boolean isButtonClicked = false;
    private static boolean isServer;
    public static boolean isBotPlayer = false;
    public String[] state = {"place", "offense", "defense"};
    public String[] mode = {"PvsC", "PvsP", "CvsC"};


    /**
     * Der Hintergrdund wird hier geladen und der view zugefügt.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }

    /**
     * der mulitplayer startet
     */
    public void startGameMultiplayer() {
        isButtonClicked = true;
        startButton.setVisible(false);
    }


    /**
     * Für das Setup eines Spiels aus einer Datei.
     * @param stage selbe fenster
     * @throws FileNotFoundException
     */
    @Override
    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {

        gegnerspielfeld = new Spielfeld(groesse,  true, true, gegnerstackpane);
        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, draggableContainer);
        gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        super.setupBase(groesse, stage,currentDifficulty,currentMode,scene);

        if(currentMode == 2) {
            bot = new AI(currentDifficulty, groesse, this);
            isBotPlayer = true;
            System.out.println("Neuer bot");
        }

        isServer = true;
    }

    /**
     * Für das Setup eines Spiels aus einer Datei.
     * @param stage selbe fenster
     * @throws FileNotFoundException
     */
    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene, List<Integer> ships) throws FileNotFoundException {
      gegnerspielfeld = new Spielfeld(groesse, true,  true, gegnerstackpane);

      spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, ships);

      gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);


      super.setupBase(groesse, stage,currentDifficulty,currentMode,scene);

      if(currentMode == 1) {
            bot = new AI(currentDifficulty, groesse, this);
            isBotPlayer = true;
            System.out.println("Neuer bot");
      }

        isServer = false;
    }

    /**
     * Setzt den Event-Handler für das Pausieren des Spiels.
     *
     * @param stage Die Hauptbühne der Anwendung.
     */
    @Override
    public void setPausierenEventHandler(Stage stage) {
        super.setPausierenEventHandler(stage);
    }

    /**
     * Behandelt den Primärklick auf das Spielfeld.
     *
     * @param spielfeld Das betroffene Spielfeld.
     * @param posx      Die X-Koordinate des Klicks.
     * @param posy      Die Y-Koordinate des Klicks.
     */
    @Override
    public void handlePrimaryClick(Spielfeld spielfeld, int posx, int posy){
        System.out.println(currentState);
        switch (currentState){
            //Schiffe setzen
            case 0:
                if(!spielfeld.istGegnerFeld){
                    //placeShip(spielfeld,posx, posy);
                }else{
                    System.out.println("Spielfeld ist gegner");
                }
                break;

            //Schiffe erschießen
            case 1:
                //shootEnemyShipMultiplayer(spielfeld, posx, posy);
                System.out.println("Multiplayer Klick mit State 1 entdeckt");
                System.out.println("koordinate " + posx + " " + posy);
                shootEnemyShipMultiplayer(spielfeld, posx, posy);
                break;

            //Schiffe beobachten (Spieler: Klicks ignorieren)
            case 2:
                watchShip(spielfeld, posx, posy);
                break;

            default:
                //Errorstate, weil es nicht mehr als 3 states gibt
        }
    }


    /**
     * Behandelt den Sieg des Spielers.
     */
    @Override
    public void handleWinForPlayer() {
        super.handleWinForPlayer();
    }

    /**
     * Behandelt den Sieg des Gegners.
     */
    @Override
    public  void handleWinForOpponent() {
        super.handleWinForOpponent();
    }



    /**
     * Überprüft, ob ein Schiff an der angegebenen Position getroffen wurde.
     *
     * @param posx Die X-Koordinate des Schusses.
     * @param posy Die Y-Koordinate des Schusses.
     * @return 1, wenn ein Schiff getroffen wurde, 2 wenn ein Teil eines versenkten Schiffs getroffen wurde, sonst 0.
     */
    public static int shootShipMultiplayer(int posx, int posy){
        if (spielerspielfeld.isShipAtPosition(posx, posy) == 1){
            return 1;
        }
        if (spielerspielfeld.isShipAtPosition(posx, posy) == 2){
            return 2;
        }
        else
            return 0;
    }

    /**
     * Verarbeitet einen Schuss auf das gegnerische Spielfeld im Multiplayer-Modus.
     *
     * @param spielfeld Das gegnerische Spielfeld.
     * @param posx      Die X-Koordinate des Schusses.
     * @param posy      Die Y-Koordinate des Schusses.
     */
    private void  shootEnemyShipMultiplayer(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe erschießen");
        if(spielfeld.istGegnerFeld){
            if(spielfeld.felder[posx][posy].wurdeGetroffen == false){
                currentState = 2;
                System.out.println("Feld ist gegnerfeld");
                spielfeld.felder[posx][posy].wurdeGetroffen = true;
                spielfeld.selectFeld(posx,posy);

                try {
                    if(isServer) {
                        Server.sendMessage("shot " + posx + " " + posy);
                        Server.setLastRowCol(posx, posy);
                    }
                    else {
                        Client.sendMessage("shot " + posx + " " + posy);
                        Client.setLastRowCol(posx, posy);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else{
                //Feld wurde bereits getroffen
                System.out.println("Feld wurde bereits getroffen. Klick wird ignoriert!");
            }
        }
    }

    /**
     * Markiert eine Zelle auf dem Spielfeld als getroffen.
     *
     * @param posx Die X-Koordinate der Zelle.
     * @param posy Die Y-Koordinate der Zelle.
     */
    public static void dyeCell(int posx, int posy){
        spielerspielfeld.selectFeld(posx, posy, Color.DARKRED);
    }

    /**
     * Führt den Spielzug der KI aus.
     *
     * @throws InterruptedException Falls der Thread unterbrochen wird.
     */
    public void executeAITurn() throws InterruptedException {

        // Simuliere KI-Klick auf das Gegner-Spielfeld
        if (currentState == 1 && gegnerspielfeld != null) {
            int[] nextMove = bot.calculateMultiplayerMove(); // KI gibt den nächsten Zug [x, y]
            int posx = nextMove[0];
            int posy = nextMove[1];
            System.out.println("KI-Schuss an Position: " + posx + ", " + posy);
            Thread.currentThread().sleep(250);
            shootEnemyShipMultiplayer(gegnerspielfeld, posx, posy);
        }
    }


    /**
     * Beobachtet ein Schiff auf dem Spielfeld (für Zuschauermodus oder Analyse).
     *
     * @param spielfeld Das Spielfeld, auf dem das Schiff beobachtet wird.
     * @param posx      Die X-Koordinate der Beobachtung.
     * @param posy      Die Y-Koordinate der Beobachtung.
     */
    private void  watchShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe beobachten ");
    }


}