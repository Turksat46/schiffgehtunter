package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.backgroundgeneration.BackgroundGenerator;
import com.turksat46.schiffgehtunter.filemanagement.SaveFileManager;
import com.turksat46.schiffgehtunter.netzwerk.Server;
import com.turksat46.schiffgehtunter.netzwerk.establishConnection;
import com.turksat46.schiffgehtunter.other.Cell;
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

    public static int currentState, currentMode, currentDifficulty, groesse;
    static GridPane feld;
    static Scene scene;
    private static AI bot;

    static boolean rotated;

    static newSpielfeld spielerspielfeld;
    static Spielfeld gegnerspielfeld;

    BackgroundGenerator backgroundManager;

    public static volatile boolean isButtonClicked = false;

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
        currentState = 1;
        isButtonClicked = true;
        System.out.println("MultipayerMainGameController state 1");
    }

    @Override
    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse,  true, true);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane);
        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        // StackPane-Margen setzen
        //HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        //HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 150)); // Abstand für gegnerstackpane

        super.setupBase(groesse, stage,currentDifficulty,currentMode,scene);
    }


    //Konstruktor mit schiff übergabe
    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode, Scene scene, List<Integer> ships) throws FileNotFoundException {
        //spielerspielfeld = new Spielfeld(groesse,  false);
        gegnerspielfeld = new Spielfeld(groesse, true,  true);

        spielerspielfeld = new newSpielfeld(groesse, false, spielerstackpane, ships);
        //gegnerspielfeld = new newSpielfeld(groesse, true, gegnerstackpane);

        //spielerstackpane.getChildren().add(newSpielfeld.gridPane);
        gegnerstackpane.getChildren().add(gegnerspielfeld.gridPane);

        // StackPane-Margen setzen
        //HBox.setMargin(spielerstackpane, new Insets(10, 10, 100, 10)); // Abstand für spielerstackpane
        //HBox.setMargin(gegnerstackpane, new Insets(10, 10, 100, 150)); // Abstand für gegnerstackpane

        super.setupBase(groesse, stage,currentDifficulty,currentMode,scene);
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
    public void handleWinForOpponent() {
        super.handleWinForOpponent();
    }




    public static int shootShipMultiplayer(int posx, int posy){
        Cell targetCell = new Cell(posx, posy);
        for (Set<Cell> cells : spielerspielfeld.shipCellMap.values()) {
            if (cells.equals(targetCell)) {
                spielerspielfeld.shipCellMap.remove(cells);
                return 1;
            }
        }
        return 0; // Kein Schiff
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
                    Server.sendMessage("shot "+ posx + " " + posy);
                    System.out.println(posx + " " + posy);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else{
                //Feld wurde bereits getroffen
                System.out.println("Feld wurde bereits getroffen. Klick wird ignoriert!");
            }
        }
    }

    public void receiveShoot(int posx, int posy){


        //spielerspielfeld.selectFeld(posx, posy, Color.DARKRED);
        //bot.receiveHit(posx, posy, spielerspielfeld.felder[posx][posy].istSchiff);
        currentState = 1;
    }

    private void  watchShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe beobachten ");
    }


}