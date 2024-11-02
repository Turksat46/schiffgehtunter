package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Spielfeld {

    public GridPane gridPane;
    int zellengroesse;
    int[][] feld;
    int groesse;
    Stage stage;


    public Spielfeld (int groesse, Stage stage, GridPane spielerstackpane){
        this.stage = stage;
        this.feld= new int [groesse][groesse];
        this.gridPane = spielerstackpane;
        this.groesse = groesse;
        initFeld();
    }


    private void initFeld(){

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

                // Rechteck und Text erstellen
                Feld cell = new Feld(zellengroesse, zellengroesse);
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
                gridPane.setMinHeight(zellengroesse*groesse);
                gridPane.setMinWidth(zellengroesse*groesse);

            }
        }
        stage.show();
    }

}