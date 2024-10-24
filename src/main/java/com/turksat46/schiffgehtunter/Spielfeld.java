package com.turksat46.schiffgehtunter;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Spielfeld {

    int spielfeldgroesse;
    int squaresize = 50;
    int canvasgroesse;
    Canvas canvas;
    GraphicsContext gc;

    public Spielfeld (int groesse){
        this.spielfeldgroesse = groesse;
        canvasgroesse = spielfeldgroesse*squaresize;
        canvas = new Canvas(canvasgroesse, canvasgroesse);
        gc = canvas.getGraphicsContext2D();
        System.out.println ("Spielfeld groesse: " + canvasgroesse + " und groesse: "+groesse);
        for (int i = 0; i < groesse; i++){
            for (int j = 0; j < groesse; j++){
                gc.setFill(Color.LIGHTBLUE);
                gc.fillRect(j*squaresize, i*squaresize, squaresize-2, squaresize-2);
            }
        }

        canvas.setOnMouseClicked(mouseEvent -> {
            int col = (int)(mouseEvent.getSceneX()/squaresize);
            int row = (int)(mouseEvent.getSceneY()/squaresize);
            System.out.println(col+" "+row);
        });

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, canvasgroesse, canvasgroesse);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}