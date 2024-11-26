package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Ship;
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
import java.util.ArrayList;

public class MainGameController {
    @FXML
    public GridPane spielerstackpane, gegnerstackpane;
    public int currentState, currentMode, currentDifficulty, groesse;
    GridPane feld;
    AI bot;

    //Drei States: place = Schiffe platzieren offense = Angriff defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    //Spielmodus P = Spieler, C=Computer
    public String[] mode = {"PvsC", "PvsP", "CvsC"};


    public void setupSpiel(int groesse, Stage stage, int currentDifficulty, int currentMode){
        Spielfeld spielerfeld = new Spielfeld(groesse, stage, spielerstackpane);
        this.currentMode = currentMode;
        this.groesse = groesse;
        currentState = 0;
        this.currentDifficulty = currentDifficulty;
        System.out.println("Mode selected and set to: " + mode[currentMode]);
        System.out.println("State selected and set to: " + state[currentState]);
        bot = new AI(currentDifficulty, groesse);

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
       switch (currentState){
            //Schiffe setzen
            case 0:
                if(spielfeld.schiffe.isEmpty()) {return;}
                placeShip(spielfeld,posx, posy);
                break;

            //Schiffe erschießen
            case 1:
                shootShip(spielfeld, posx, posy);
                break;

            //Schiffe beobachten (Spieler: Klicks ignorieren)
            case 2:
                watchShip(spielfeld, posx, posy);
                break;

            default:
                //Errorstate, weil es nicht mehr als 3 states gibt
        }
    }

    public void handleSecondaryClick(Spielfeld spielfeld, int posx, int posy){
        if (currentState == 0){
            //Schiffe entfernen
            if(spielfeld.felder[posx][posy].gesetzt){
                spielfeld.felder[posx][posy].gesetzt = false;
                spielfeld.deselectRowAndColumn(spielfeld, posx, posy);
                System.out.println("Feld wurde abgewählt");
            }
        }
    }

    public void setFeld(GridPane feld){
        this.feld = feld;
    }


    public int nachbarFeldGewaehlt(Spielfeld spielfeld, int posx, int posy){
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int x = posx + dir[0];
            int y = posy + dir[1];

            // Überprüfen, ob die Koordinaten innerhalb des Spielfelds liegen
            if (x >= 0 && x < spielfeld.groesse && y >= 0 && y < spielfeld.groesse) {
                if (spielfeld.felder[x][y].gesetzt) {
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

        return maxLength;
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

    private void placeShip(Spielfeld spielfeld, int posx, int posy){

            if (!spielfeld.felder[posx][posy].gesetzt) {
                spielfeld.felder[posx][posy].gesetzt = true;
                spielfeld.selectFeld(posx, posy);

                // Entferne das passende Schiff aus der Liste
                int nachbarWert = nachbarFeldGewaehlt(spielfeld, posx, posy); // Berechnung nur einmal
                boolean entfernt = spielfeld.schiffe.removeIf(schiff -> schiff == nachbarWert);

                if (entfernt) {
                    System.out.println("Schiff mit Wert " + nachbarWert + " aus der Liste entfernt.");
                } else {
                    System.out.println("Kein Schiff mit Wert " + nachbarWert + " in der Liste gefunden.");
                }

            }
    }

    private void  shootShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe erschießen");
    }

    private void  watchShip(Spielfeld spielfeld, int posx, int posy){
        System.out.println("schiffe beobachten ");
    }

}
