package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Spielfeld {

    public GridPane gridPane;
    int zellengroesse, groesse;
    int[][] feld;
    Feld[][] felder;
    Stage stage;
    MainGameController mainGameController;
    ArrayList<Integer> schiffe= new ArrayList<>();

    public Spielfeld (int groesse, Stage stage, GridPane spielerstackpane){
        this.stage = stage;
        this.feld= new int [groesse][groesse];
        this.gridPane = spielerstackpane;
        this.groesse = groesse;
        felder = new Feld[groesse][groesse];
        mainGameController = new MainGameController();

        initFeld();
    }


    private void initFeld(){

        if(groesse <=5 ){
            zellengroesse=75;
            this.schiffe.add(2);

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
                cell.setFill(Color.LIGHTBLUE);

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
                gridPane.setMinHeight(zellengroesse*groesse);
                gridPane.setMinWidth(zellengroesse*groesse);

            }
        }
        mainGameController.setFeld(gridPane);
        stage.show();
    }

    public void selectFeld(int posx, int posy){
        felder[posx][posy].setFill(Color.BLUE);
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