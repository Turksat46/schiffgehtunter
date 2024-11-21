package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class MainGameController {
    @FXML
    public GridPane spielerstackpane;
    @FXML
    public GridPane gegnerstackpane;

    public Spielfeld spielfeld;
    //Drei States: place = Schiffe platzieren offense = Angriff defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};

    public int gesamtlänge = 5;
    public int platzierteLänge = 0;

    //Spielmodis
    //P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};
    public int currentState;
    public int currentMode;
    public int currentDifficulty;
    public int groesse;
    GridPane feld;

    //TODO: Hier vllt bekannte Paare eintragen?
    public int[][] paare;

    AI bot;

    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode){
        this.spielfeld = new Spielfeld(groesse, stage, spielerstackpane);
        paare = new int[groesse][groesse];
        System.out.println(this.spielfeld);
        this.currentMode = currentMode;
        this.groesse = groesse;
        currentState = 0;
        this.currentDifficulty = currentDifficulty;
        System.out.println("Mode selected and set to: " + mode[currentMode]);
        System.out.println("State selected and set to: " + state[currentState]);
        bot = new AI(currentDifficulty, groesse);
        stage.setTitle("Spiel: Schiffe platzieren");

        setPausierenEventHandler(stage);
    }

    public void setupGegnerFeld(int groesse, Stage stage) {

        Spielfeld gegnerspielfeld = new Spielfeld(groesse, stage, gegnerstackpane);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        stage.setX((screenSize.width - stage.getWidth()) / 2 - stage.getWidth());
    }

    public void setPausierenEventHandler(Stage stage){
        stage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.ESCAPE){
                    //Spiel pausieren
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("PauseGame.fxml"));
                        Stage stage = new Stage();
                        stage.setTitle("Pause");
                        stage.setScene(new Scene(loader.load()));
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });
    }


    public void handlePrimaryClick(Spielfeld spielfeld, int posx, int posy){
        System.out.println("Clicked at: " + posx + ", " + posy);
        //
        /*TODO: prüfe die aktuelle State*/

        switch (currentState){
            //Schiffe setzen
            case 0:
                //TODO: Hier eventuell prüfen, ob man Schiff setzen kann
                if(platzierteLänge <= gesamtlänge){
                    if(!spielfeld.felder[posx][posy].gesetzt){
                        spielfeld.felder[posx][posy].gesetzt = true;
                        spielfeld.selectFeld(posx, posy);
                        platzierteLänge++;
                        if(nachbarFeldGewaehlt(spielfeld, posx, posy)){
                            System.out.println("Nachbarfeld ist gewaehlt");
                        }
                    }else{
                        System.out.println("Spielfeld bereits gewählt");
                    }
                }

                break;

            //Schiffe erschießen
            case 1:
                break;

            //Schiffe beobachten (Spieler: Klicks ignorieren)
            case 2:
                break;

            default:
                //Errorstate, weil es nicht mehr als 3 states gibt
        }
    }

    public void handleSecondaryClick(Spielfeld spielfeld, int posx, int posy){
        System.out.println("Right-Clicked at: " + posx + ", " + posy);
        //
        /*TODO: prüfe die aktuelle State*/

        if (currentState == 0){
            //Schiffe entfernen
                //TODO: Hier eventuell prüfen, ob man Schiff setzen kann
                if(platzierteLänge >= 0){
                    if(spielfeld.felder[posx][posy].gesetzt){
                        spielfeld.felder[posx][posy].gesetzt = false;
                        spielfeld.deselectRowAndColumn(spielfeld, posx, posy);
                        System.out.println("Feld wurde abgewählt");
                    }else{
                        System.out.println("Feld ist nicht gewählt");
                    }
                }
        }
    }

    public void setFeld(GridPane feld){
        this.feld = feld;
    }


    public boolean nachbarFeldGewaehlt(Spielfeld spielfeld, int posx, int posy){
        // Prüfen, ob ein Nachbarfeld bereits gesetzt ist
        boolean neighborSet = false;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int x = posx + dir[0];
            int y = posy + dir[1];



            // Überprüfen, ob die Koordinaten innerhalb des Spielfelds liegen
            if (x >= 0 && x < spielfeld.groesse && y >= 0 && y < spielfeld.groesse) {
                if (spielfeld.felder[x][y].gesetzt) {
                    neighborSet = true;

                    // Färbe das aktuelle und das Nachbarfeld grün
                    spielfeld.felder[x][y].setFill(Color.GREEN);
                    spielfeld.felder[posx][posy].setFill(Color.GREEN);

                    break; // Sobald ein gesetztes Nachbarfeld gefunden wurde, abbrechen
                }
            }
        }


        // Prüfe die Nachbarschaft und bestimme die Paarlänge
        int maxLength = 1; // Mindestens das aktuelle Feld
        for (int[] dir : directions) {
            int lengthInDir = getPairLength(spielfeld, posx, posy, dir[0], dir[1]) +
                    getPairLength(spielfeld, posx, posy, -dir[0], -dir[1]);
            maxLength = Math.max(maxLength, lengthInDir + 1);
        }



        System.out.println("Die Länge des Paares beträgt: " + maxLength);

        if (!neighborSet) {
            return false;
        } else {
            System.out.println("Ein Nachbarfeld ist bereits belegt.");
            return true;
        }
    }

    private int getPairLength(Spielfeld spielfeld, int x, int y, int dx, int dy) {

        int length = 0;
        int nx = x + dx;
        int ny = y + dy;

        while (nx >= 0 && nx < spielfeld.groesse && ny >= 0 && ny < spielfeld.groesse && spielfeld.felder[nx][ny].gesetzt) {
            length++;
            nx += dx;
            ny += dy;
        }
        return length;
    }

}
