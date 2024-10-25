package com.turksat46.schiffgehtunter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainGameController {
    @FXML
    public GridPane spielerstackpane;
    public Label label;

    Spielfeld spielfeld;

    int groesse;

    @FXML
    public void initialize() {
        setupSpielfeld(5);
        spielerstackpane.getChildren().add(spielfeld.gridPane);


    }

    public void setupSpielfeld(int groesse){
        this.groesse = groesse;
        spielfeld = new Spielfeld(groesse);

    }


}
