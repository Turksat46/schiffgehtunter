package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import com.turksat46.schiffgehtunter.other.Ship;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class Spielfeld {

    public GridPane gridPane;
    int zellengroesse;
    int[][] feld;
    int groesse;
    Stage stage;
    ArrayList<Ship> schiffe = new ArrayList<>();
    MainGameController mainGameController;


    public Spielfeld (int groesse, Stage stage, GridPane spielerstackpane){
        this.stage = stage;
        this.feld= new int [groesse][groesse];
        this.gridPane = spielerstackpane;
        this.groesse = groesse;

        mainGameController = new MainGameController();

        initFeld();
    }


    private void initFeld(){

        if(groesse <=5 ){
            zellengroesse=75;
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
        }else if(groesse > 5 && groesse <= 10){
            zellengroesse=50;
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("U-Boot", 3));
            schiffe.add(new Ship("U-Boot", 3));
        }else if(groesse > 10 && groesse <= 20){
            zellengroesse=30;
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("U-Boot", 3));
            schiffe.add(new Ship("Kreuzer", 3));
            schiffe.add(new Ship("Kreuzer", 3));
            schiffe.add(new Ship("Schlachtschiff", 4));
            schiffe.add(new Ship("Schlachtschiff", 4));
        }else {
            zellengroesse=20;
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("Zerstörer", 2));
            schiffe.add(new Ship("U-Boot", 3));
            schiffe.add(new Ship("U-Boot", 3));
            schiffe.add(new Ship("Kreuzer", 3));
            schiffe.add(new Ship("Schlachtschiff", 4));
            schiffe.add(new Ship("Schlachtschiff", 4));
            schiffe.add(new Ship("Flugzeugträger", 5));
        }

        System.out.println("Anzahl der Schiffe: " + schiffe.size());
        for (Ship schiff : schiffe) {
            System.out.println("Schiff: " + schiff.getName() + ", Groesse: " + schiff.getGroesse());
        }


        // Schleife zur Erstellung der Zellen (als Rectangle mit Text)
        for (int i = 0; i < groesse; i++) {
            for (int j = 0; j < groesse; j++) {
                int row = i;
                int col = j;
                this.feld[row][col] = 0;

                // Rechteck und Text erstellen und position der zelle
                Feld cell = new Feld(zellengroesse, zellengroesse, row, col);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.BLACK);

                Text cellText = new Text(String.valueOf(feld[row][col]));

                // StackPane für Rechteck und Text in einer Zelle
                StackPane cellPane = new StackPane();
                cellPane.getChildren().addAll(cell, cellText);


                // Klick-Event für jede Zelle und aktualisiert einen neuen wert später also das löschen
                cellPane.setOnMouseClicked(event -> {
                    //System.out.println("ist cell gesetzt ? "+cell.pruefengesetzt());
                   //feld[cell.getPosX()][cell.getPosY()] = 1; evtl nur mit Feld klasse machen und nicht mit der feld vairable
                   // System.out.println(feld[cell.getPosX()][cell.getPosY()]);#

                    //placeShip();
                    mainGameController.handleClick(col, row);
                });

                // Zelle dem GridPane hinzufügen
                gridPane.add(cellPane, j, i);
                gridPane.setMinHeight(zellengroesse*groesse);
                gridPane.setMinWidth(zellengroesse*groesse);

            }
        }
        stage.show();
    }

    public void placeShip(){
        //TODO: noch implementieren aber eher mit der class Feld ud nicht mit der variable feld
    }

}