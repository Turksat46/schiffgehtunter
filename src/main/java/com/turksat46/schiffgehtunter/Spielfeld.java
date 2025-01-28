package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import com.turksat46.schiffgehtunter.other.Ship;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Spielfeld {

    public GridPane gridPane;
    int zellengroesse, groesse;
    public int[][] feld;
    public Feld[][] felder;
    MainGameController mainGameController;
    public ArrayList<Integer> schiffe= new ArrayList<>();
    boolean istGegnerFeld;

    public Spielfeld (int groesse, boolean istGegnerFeld){
        this.feld= new int [groesse][groesse];
        this.gridPane = new GridPane();
        this.groesse = groesse;
        felder = new Feld[groesse][groesse];
        mainGameController = new MainGameController();
        this.istGegnerFeld = istGegnerFeld;
        initFeld();

    }

    // TODO:Ich schaue hier noch ob wir des so lassen
    // soll bei kleinen felder random schiffsgroeße aufgewöhlt werden oder greedy benutzen ???
    // TODO: IDEE ist jz umgedrehter greedy algorihtmus
    private void initFeld(){

        int[] schiffsGroessen = {5, 4, 3, 2}; // Größen der Schiffe
        int totalCells = groesse * groesse; // Gesamtanzahl der Zellen im Spielfeld
        int shipCount = (int) (totalCells * 0.3); // 30 % der Zellen für Schiffe


        // Greedy-Algorithmus zum Auffüllen der Zellen
        //des hier bei feldgroesse von 10+
        for (int size : schiffsGroessen) {
            while (shipCount >= size) {
                schiffe.add(size); // Schiff hinzufügen
                shipCount -= size; // Zellen zählen
            }
        }

        // Ausgabe der Ergebnisse
        System.out.println("Schiffe in Zellen: " + schiffe);
        System.out.println("Verbleibende Zellen: " + shipCount);

        if(groesse <=5 ){
            zellengroesse=75;
        }else if(groesse > 5 && groesse <= 10){
            zellengroesse=50;

        }else if(groesse > 10 && groesse <= 20){
            zellengroesse=30;

        }else {
            zellengroesse=20;

        }

        // Schleife zur Erstellung der Zellen (als Rectangle mit Text)
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < groesse; j++) {
                int row = i;
                int col = j;
                this.feld[row][col] = 0;

                // Rechteck und Text erstellen und position der zelle
                Feld cell = new Feld(zellengroesse, zellengroesse, row, col);
                felder[row][col] = cell;
                cell.setFill(Color.TRANSPARENT);

                cell.setStroke(Color.BLACK);

                Text cellText = new Text(String.valueOf(feld[row][col]));

                // StackPane für Rechteck und Text in einer Zelle
                StackPane cellPane = new StackPane();
                cellPane.getChildren().addAll(cell, cellText);


                // Klick-Event für jede Zelle
                cellPane.setOnMouseClicked(event -> {
                    if (event.getButton()== MouseButton.PRIMARY) {
                        mainGameController.handlePrimaryClick(this, row, col);
                    }
                    else if (event.getButton()== MouseButton.SECONDARY) {
                        mainGameController.handleSecondaryClick(this, row, col);
                    }

                });

                // Zelle dem GridPane hinzufügen
                gridPane.add(cellPane, j, i);
            }
        }

    }

    public void selectFeld(int posx, int posy){
        //TODO: Eventuell hier die Farbe ändern, wenn ein Schiff angeklickt wird
        if(istGegnerFeld){
            felder[posx][posy].setFill(Color.RED);
        }else{
            felder[posx][posy].setFill(Color.BLUE);
        }
    }

    public void selectFeld(int posx, int posy, Color color){
        //TODO: Eventuell hier die Farbe ändern, wenn ein Schiff angeklickt wird
            felder[posx][posy].setFill(color);
    }

    public void deselectRowAndColumn(Spielfeld spielfeld, int posx, int posy) {
        felder[posx][posy].setFill(Color.LIGHTBLUE);
        spielfeld.felder[posx][posy].gesetzt = false;

        // Horizontale Richtung: Nach links
        for (int x = posx - 1; x >= 0; x--) {
            if (spielfeld.felder[x][posy].gesetzt) {
                spielfeld.felder[x][posy].gesetzt = false;
                felder[x][posy].setFill(Color.LIGHTBLUE);
            } else {
                break; // Stoppen, wenn ein freies Feld gefunden wird
            }
        }

        // Horizontale Richtung: Nach rechts
        for (int x = posx + 1; x < spielfeld.groesse; x++) {
            if (spielfeld.felder[x][posy].gesetzt) {
                spielfeld.felder[x][posy].gesetzt = false;
                felder[x][posy].setFill(Color.LIGHTBLUE);
            } else {
                break; // Stoppen, wenn ein freies Feld gefunden wird
            }
        }

        // Vertikale Richtung: Nach oben
        for (int y = posy - 1; y >= 0; y--) {
            if (spielfeld.felder[posx][y].gesetzt) {
                spielfeld.felder[posx][y].gesetzt = false;
                felder[posx][y].setFill(Color.LIGHTBLUE);
            } else {
                break; // Stoppen, wenn ein freies Feld gefunden wird
            }
        }

        // Vertikale Richtung: Nach unten
        for (int y = posy + 1; y < spielfeld.felder[posx].length; y++) {
            if (spielfeld.felder[posx][y].gesetzt) {
                spielfeld.felder[posx][y].gesetzt = false;
                felder[posx][y].setFill(Color.LIGHTBLUE);
            } else {
                break; // Stoppen, wenn ein freies Feld gefunden wird
            }
        }
    }
}