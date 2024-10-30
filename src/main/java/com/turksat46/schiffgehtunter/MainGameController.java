package com.turksat46.schiffgehtunter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainGameController {
    @FXML
    public GridPane spielerstackpane;
    public Label label;

    Spielfeld spielfeld;
    
    public void setupSpielfeld(int groesse, Stage stage){
        spielfeld = new Spielfeld(groesse, stage);
        spielerstackpane.getChildren().add(spielfeld.gridPane);
    }


}
