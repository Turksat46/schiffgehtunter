package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Spielfeld {

    public GridPane gridPane;
    public int spielfeldgroesse;
    int zellengroesse;
    int[][] feld;
    Stage stage;
    Feld cell;


    public Spielfeld (int groesse, Stage stage, GridPane spielerstackpane){
        this.stage = stage;
        this.feld= new int [groesse][groesse];
        this.gridPane = spielerstackpane; // VLT nicht in attribut rein sondern eif direkt des benutzen
        initFeld(groesse);
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

    private void initFeld(int groesse){

        // Schleife zur Erstellung der Zellen (als Rectangle mit Text)
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < groesse; j++) {
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