package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.backgroundgeneration.BackgroundGenerator;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.netzwerk.Client;
import com.turksat46.schiffgehtunter.netzwerk.Server;
import com.turksat46.schiffgehtunter.netzwerk.establishConnection;
import com.turksat46.schiffgehtunter.other.Cell;
import com.turksat46.schiffgehtunter.other.Position;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


public class MultipayerMainGameController extends MainGameController implements Initializable {


    @FXML
    public BorderPane spielerstackpane, gegnerstackpane;

    @FXML AnchorPane anchorPane;

    @FXML public HBox container;
    @FXML public Pane images;

    @FXML public HBox label1;
    @FXML private Button startButton;
    @FXML HBox draggableContainer;

    public static int currentState, currentMode, currentDifficulty, groesse;
    static GridPane feld;
    static Scene scene;
    private static AI bot;

    static boolean rotated;

    static newSpielfeld spielerspielfeld;
    public static Spielfeld gegnerspielfeld;

    BackgroundGenerator backgroundManager;

    public static volatile boolean isButtonClicked = false;
    private static boolean isServer;
    public static boolean isBotPlayer = false;


    //Drei States: place = Schiffe platzieren, offense = Angriff, defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    //Spielmodus P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
    }


    public void startGameMultiplayer() {
        //super.startGame();
        //currentState = 1;
        isButtonClicked = true;
        System.out.println("MultipayerMainGameController state 1");
        hinweistext.setVisible(false);
        startButton.setVisible(false);
        spielerspielfeld.changeEditableState(false);
    }

    @Override
    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse,  true, true, gegnerstackpane);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, draggableContainer);
        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        //gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        if(groesse <= 7){
            stage.setMinWidth(1110);
            stage.setMinHeight(650);
        }else if(groesse <= 15){
            stage.setMinWidth(1510);
            stage.setMinHeight(800);
        }else if(groesse > 15){
            stage.setMaximized(true);
        }

        // StackPane-Margen setzen
        HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 300)); // Abstand für gegnerstackpane


        super.setupBase(groesse, stage,currentDifficulty,currentMode,scene);


        if(currentMode == 2) {
            bot = new AI(currentDifficulty, groesse, this);
            isBotPlayer = true;
            System.out.println("Neuer bot");
        }

        isServer = true;
    }


    //Konstruktor mit schiff übergabe
    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene, List<Integer> ships) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse, true,  true, gegnerstackpane);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, ships, draggableContainer);
        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        //gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        if(groesse <= 7){
            stage.setMinWidth(1110);
            stage.setMinHeight(650);
        }else if(groesse <= 15){
            stage.setMinWidth(1510);
            stage.setMinHeight(800);
        }else if(groesse > 15){
            stage.setMaximized(true);
        }

        // StackPane-Margen setzen
        HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 300)); // Abstand für gegnerstackpane

        super.setupBase(groesse, stage,currentDifficulty,currentMode,scene);

        if(currentMode == 1) {
            bot = new AI(currentDifficulty, groesse, this);
            isBotPlayer = true;
            System.out.println("Neuer bot");
        }

        isServer = false;
    }

    @Override
    public void setPausierenEventHandler(Stage stage) {
        super.setPausierenEventHandler(stage);
    }

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

    @Override
    public void handleSecondaryClick(Spielfeld spielfeld, int posx, int posy) {
        super.handleSecondaryClick(spielfeld, posx, posy);
    }

    @Override
    public void handleWinForPlayer() {
        super.handleWinForPlayer();
    }

    @Override
    public  void handleWinForOpponent() {
        super.handleWinForOpponent();
    }




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

    //Shoot Funktionen Überarbeiten
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

    public static void dyeCell(int posx, int posy){
        spielerspielfeld.selectFeld(posx, posy, Color.DARKRED);
    }


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

    public int [] receiveBotShoot(int posx, int posy) {
        int [] result = new int[2];
        result[0] = posx;
        result[1] = posy;
        return result;
    }



    private void  watchShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe beobachten ");
    }


}