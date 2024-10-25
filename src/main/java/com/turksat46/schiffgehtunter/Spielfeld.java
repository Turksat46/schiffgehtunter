package com.turksat46.schiffgehtunter;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.layout.GridPane;

public class Spielfeld {

    int zellengroesse;
    int[][] feld;

    public Spielfeld (int groesse){
        Stage stage = new Stage();


        GridPane gridPane = new GridPane();


        this.feld= new int [groesse][groesse];

        // Schleife zur Erstellung der Zellen (als Rectangle mit Text)
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < groesse; j++) {
                int row = i;
                int col = j;
                this.feld[row][col] = 0;

                // Rechteck und Text erstellen
                Rectangle cell = new Rectangle(30, 30);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.BLACK);

                Text cellText = new Text(String.valueOf(feld[row][col]));

                // StackPane für Rechteck und Text in einer Zelle
                StackPane cellPane = new StackPane();
                cellPane.getChildren().addAll(cell, cellText);

                // Klick-Event für jede Zelle und aktualisiert einen neuen wert später also das löschen
                cellPane.setOnMouseClicked(event -> {
                    feld[row][col] = 1;
                    cellText.setText("1");
                });

                // Zelle dem GridPane hinzufügen
                gridPane.add(cellPane, j, i);
            }
        }

        //TODO: Hier wird aber neue fenster geöffnet veruschen in alte Fenster zu bleiben
        Scene scene = new Scene(gridPane);
        stage.setTitle("spielfeld");
        stage.setScene(scene);
        stage.setWidth(320);
        stage.setHeight(340);
        stage.show();
    }


}