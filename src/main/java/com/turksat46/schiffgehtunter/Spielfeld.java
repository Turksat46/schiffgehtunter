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

    int belowoffset = 150;
    int spielfeldgroesse;
    int squaresize = 50;
    int canvasgroesse;
    Canvas canvas;
    GraphicsContext gc;

    public Spielfeld (int groesse){
        this.spielfeldgroesse = groesse;
        squaresize = getQuadratGroesse();
        canvasgroesse = spielfeldgroesse*squaresize;
        canvas = new Canvas(canvasgroesse, canvasgroesse+belowoffset);
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
        Scene scene = new Scene(root, canvasgroesse, canvasgroesse+belowoffset);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
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
}