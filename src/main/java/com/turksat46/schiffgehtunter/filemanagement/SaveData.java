package com.turksat46.schiffgehtunter.filemanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.turksat46.schiffgehtunter.AI;
import com.turksat46.schiffgehtunter.MainGameController;
import com.turksat46.schiffgehtunter.Spielfeld;
import com.turksat46.schiffgehtunter.newSpielfeld;
import com.turksat46.schiffgehtunter.other.Cell;
import com.turksat46.schiffgehtunter.other.Feld;
import com.turksat46.schiffgehtunter.other.Position;
import javafx.scene.Group;

import java.util.*;

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


    Map<String, Object> data = new HashMap<>();

    //AI Daten
    static int[][] kifeld;
    static private Map<Integer, List<Position>> kiships = new HashMap<>();
    static List<Position> kientdeckteSchiffe = new ArrayList<>();
    static Set<Position> kifelder = new HashSet<>(); // Geändert zu HashSet
    public List<Position> kivalidMoves;



    public SaveData(MainGameController mainGameController, newSpielfeld newSpielfeld, Spielfeld gegnerfeld, AI bot){
        this.mainGameController = mainGameController;
        this.newSpielfeld = newSpielfeld;
        this.spielfeld = gegnerfeld;
        this.bot = bot;

        this.currentState = MainGameController.currentState;
        this.currentMode = MainGameController.currentMode;
        this.currentDifficulty = MainGameController.currentDifficulty;
        this.groesse = MainGameController.groesse;

        this.shipCellMap = newSpielfeld.getShipCellMap();

        this.feld = gegnerfeld.feld;
        //this.felder = gegnerfeld.felder;

        this.kifeld = AI.feld;
        this.kiships = AI.ships;
        this.kientdeckteSchiffe = AI.entdeckteSchiffe;
        this.kifelder = AI.felder;

        this.kivalidMoves = bot.validMoves;


    }

    //Diese Funktion wird für das Sammeln nötiger Daten gebraucht
    public String sampleData(){
        data.put("currentState", currentState);
        data.put("currentMode", currentMode);
        data.put("currentDifficulty", currentDifficulty);
        data.put("groesse", groesse);
        // --- Modified Ship Saving Logic ---
        List<Map<String, Object>> shipsDataList = new ArrayList<>();
        int shipIndexCounter = 0; // To create unique ship IDs

        for (Map.Entry<Group, Set<Cell>> entry : shipCellMap.entrySet()) {
            Group shipGroup = entry.getKey();
            Set<Cell> cellSet = entry.getValue();

            if (cellSet.isEmpty()) continue; // Skip empty ships if any

            Map<String, Object> shipData = new HashMap<>();
            shipData.put("shipId", "ship_" + shipIndexCounter++); // Unique ID
            shipData.put("cells", new ArrayList<>(cellSet)); // Save cells as List (Gson serializes Set directly, but List is clearer in JSON)

            // Determine and save orientation (example, adjust logic as needed)
            boolean isHorizontal = false;
            if (cellSet.size() >= 2) {
                Cell[] cellsArray = cellSet.toArray(new Cell[0]);
                if (cellsArray[0].getRow() == cellsArray[1].getRow()) {
                    isHorizontal = true;
                }
            }
            shipData.put("isHorizontal", isHorizontal);
            shipsDataList.add(shipData);
        }
        data.put("ships", shipsDataList); // Save the list of ship data instead of shipCellMap directly
        data.put("feld", feld);
        data.put("felder", felder);

        //Von KI
        data.put("kifeld", kifeld);
        data.put("kiships", kiships);
        data.put("kientdeckteSchiffe", kientdeckteSchiffe);
        data.put("kifelder", kifelder);
        data.put("kivalidMoves", kivalidMoves);


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        return json;

    }

}
