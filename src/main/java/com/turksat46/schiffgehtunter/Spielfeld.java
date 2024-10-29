package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Feld;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Spielfeld {

    public GridPane gridPane;
    public int spielfeldgroesse;
    int zellengroesse;
    int[][] feld;
    Stage stage;
    Feld cell;

    Boolean schiffesetzen = true;

    public Spielfeld (int hoehe, int breite, Stage stage){
        this.stage = stage;
        this.feld= new int [hoehe][breite];
        initFeld(hoehe, breite);
    }

    private int getQuadratGroesse() {
        int groesse = 0;
        if(spielfeldgroesse <= 5){
            return 75;
        }
        else if (spielfeldgroesse > 5 && spielfeldgroesse <= 10) {
            return 50;
        } else if (spielfeldgroesse > 10 && spielfeldgroesse <= 20) {
            return 30;
        }else{
            return 20;
        }
    }

    private void initFeld(int hoehe, int breite){

        gridPane = new GridPane();

        // Schleife zur Erstellung der Zellen (als Rectangle mit Text)
        for (int i = 0; i < hoehe; i++) {
            for (int j = 0; j < breite; j++) {
                int row = i;
                int col = j;
                this.feld[row][col] = 0;

                zellengroesse = getQuadratGroesse();


                // Rechteck und Text erstellen
                Feld cell = new Feld(30, 30);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.BLACK);

                Text cellText = new Text(String.valueOf(feld[row][col]));

                // StackPane für Rechteck und Text in einer Zelle
                StackPane cellPane = new StackPane();
                cellPane.getChildren().addAll(cell, cellText);

                // Klick-Event für jede Zelle und aktualisiert einen neuen wert später also das löschen
                cellPane.setOnMouseClicked(event -> {
                    cell.setzen();


                });

                // Zelle dem GridPane hinzufügen
                gridPane.add(cellPane, j, i);
            }
        }

    }

}