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

    //Drei States
    //place = Schiffe platzieren
    //offense = Angriff
    //defense = Verteidigung bzw. auf Angriff vom Gegner warten
    public String[] state = {"place", "offense", "defense"};
    public String[] mode = {"PvsC", "PvsP", "CvsC"};

    public int currentState;
    public int currentMode;

    public void setupSpielfeld(int groesse, Stage stage){
        spielfeld = new Spielfeld(groesse, stage, spielerstackpane);
        currentState = 0;
    }

    public int getCurrentState(){
        return currentState;
    }

    public void setCurrentMode(int modeposition){
        currentMode = modeposition;
        System.out.println("Mode selected and set to: " + mode[currentMode]);
    }

    public void handleClick(int posx, int posy){
        //System.out.println(posx + " " + posy);

    }

}
