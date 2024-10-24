package com.turksat46.schiffgehtunter;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainGameController {
    @FXML
    public StackPane spielerstackpane;
    public Label label;


    Stage stage;

    //TODO: Größe mithilfe der Feldanzahl beim Erstellen berechnen? (Vllt Größe gleich halten)
    int squaresize = 50;

    @FXML
    public void initialize() {
    }

    public void setupSpielfeld(int groesse){
        //TODO: Spielfeldgröße vom Erstellen übernehmen

        Spielfeld spielfeld = new Spielfeld(groesse);

    }

}
