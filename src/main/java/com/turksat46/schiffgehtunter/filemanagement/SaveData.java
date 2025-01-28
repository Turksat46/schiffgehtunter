package com.turksat46.schiffgehtunter.filemanagement;

import com.turksat46.schiffgehtunter.AI;
import com.turksat46.schiffgehtunter.MainGameController;
import com.turksat46.schiffgehtunter.Spielfeld;
import com.turksat46.schiffgehtunter.newSpielfeld;
import com.turksat46.schiffgehtunter.other.Cell;
import com.turksat46.schiffgehtunter.other.Feld;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SaveData {

    MainGameController mainGameController;
    newSpielfeld newSpielfeld;
    Spielfeld spielfeld;
    AI bot;

    //MainGameController
    public static int currentState, currentMode, currentDifficulty, groesse;

    //newSpielfeld
    Map<Group, Set<Cell>> shipCellMap = new HashMap<>();

    //Spielfeld
    int[][] feld;
    Feld[][] felder;
    ArrayList<Integer> schiffe = new ArrayList<>();


    public SaveData(MainGameController mainGameController, newSpielfeld newSpielfeld, Spielfeld gegnerfeld, AI bot){
        this.mainGameController = mainGameController;
        this.newSpielfeld = newSpielfeld;
        this.spielfeld = gegnerfeld;
        this.bot = bot;
    }

    //Diese Funktion wird für das Sammeln nötiger Daten gebraucht
    public String sampleData(){
        currentState = MainGameController.currentState;
        currentMode = MainGameController.currentMode;
        currentDifficulty = MainGameController.currentDifficulty;
        shipCellMap = newSpielfeld.shipCellMap;
        feld = spielfeld.feld;
        felder = spielfeld.felder;
        schiffe = spielfeld.schiffe;
        return this.toString();
    }

}
