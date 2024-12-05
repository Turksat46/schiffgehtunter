package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Position;

import java.io.Console;
import java.util.*;
import java.util.random.RandomGenerator;

public class AI {
    int difficulty;
    static int[][] feld;
    static int groesse;

    static private Map<Integer, List<Position>> ships = new HashMap<>();
    static private int shipId = 0;

    static List<Position> felder = new ArrayList<Position>();

    static MainGameController mainGameController;

    public AI(int difficulty, int groesse, MainGameController mainGameController){
        this.difficulty = difficulty;
        this.groesse = groesse;
        feld = new int[groesse][groesse];
        setShips();
        this.mainGameController = mainGameController;
    }

    private void setShips() {
        //Platziere random Schiffe auf den Feldern
        int mindestAnzahlSchiffe = (int)((groesse*groesse)*0.3);
        int currentAnzahlSchiffe = 0;

        List<Position> eventuelleStartPositionen = new ArrayList<>();
        for(int i = 0; i < groesse; i++){
            for(int j = 0; j < groesse; j++){
                eventuelleStartPositionen.add(new Position(i, j));
            }
        }

        shipId = 0; // Reset shipId
        ships.clear(); // Clear the ships map

        while(currentAnzahlSchiffe < mindestAnzahlSchiffe){
            Collections.shuffle(eventuelleStartPositionen);
            Position startPosition = eventuelleStartPositionen.get(0);

            int schiffGroesse = RandomGenerator.getDefault().nextInt(2, 5);

            boolean horizontal = RandomGenerator.getDefault().nextBoolean();
            if(horizontal && startPosition.getX() + schiffGroesse > groesse){
                horizontal = false;
            } else if (!horizontal && startPosition.getY() + schiffGroesse > groesse) {
                horizontal = true;
            }

            boolean kannPlatziertwerden = true;
            for(int i = 0; i < schiffGroesse; i++){
                int x = horizontal ? startPosition.getX() + i : startPosition.getX();
                int y = horizontal ? startPosition.getY() : startPosition.getY() + i;
                System.out.println(x+ " " + y + "wurden markiert!");
                // Check if the coordinates are within bounds
                if (x >= groesse || y >= groesse) {
                    kannPlatziertwerden = false;
                    break;
                }

                if(feld[x][y] != 0){
                    kannPlatziertwerden = false;
                    break;
                }
            }
            List<Position> shipPositions = new ArrayList<>();
            if(kannPlatziertwerden){
                for(int j = 0; j < schiffGroesse; j++){
                    int x = horizontal ? startPosition.getX() + j : startPosition.getX();
                    int y = horizontal ? startPosition.getY() : startPosition.getY() + j;
                    feld[x][y] = 1;
                    shipPositions.add(new Position(x, y));
                }
                ships.put(shipId, shipPositions);
                shipId++;
                currentAnzahlSchiffe+=schiffGroesse;

                for(int j = 0; j < schiffGroesse; j++){
                    int x = horizontal ? startPosition.getX() + j : startPosition.getX();
                    int y = horizontal ? startPosition.getY() : startPosition.getY() + j;
                    for(int k = -1; k <= 1; k++){
                        for(int l = -1; l <= 1; l++){
                            if(x + k >= 0 && x + k < groesse && y + l >= 0 && y + l < groesse ){
                                System.out.println("Schiff wurde an "+(x+k)+","+(y+l)+ " gesetzt.");
                                eventuelleStartPositionen.remove(new Position(x + k, y + l));
                            }
                        }
                    }
                }
            }

        }

    }

    public void receiveMove(int posx, int posy){
        //Angriff wurde initiiert, Gegenangriff starten
        if(feld[posx][posy] == 1){
            System.out.println(posx + " " + posy + " wurde getroffen!");
            feld[posx][posy] = 0;
            Map<Integer, List<Position>> shipsCopy = ships;

            for (Map.Entry<Integer, List<Position>> entry : shipsCopy.entrySet()) {
                List<Position> shipPositions = entry.getValue();
                shipPositions.removeIf(p -> p.getX() == posx && p.getY() == posy);
                if (shipPositions.isEmpty()) {
                    System.out.println("Schiff " + entry.getKey() + " versenkt!");
                    ships.remove(entry.getKey());
                    if(ships.isEmpty()){
                        //Spieler hat gewonnen
                        mainGameController.handleWinForPlayer();
                    }
                }
            }
            mainGameController.handleHit(posx, posy);
        }

        getNextMove();

    }

    public void getNextMove(){
        switch (difficulty){
            case 0:
                easyMove();
                break;
            case 1:
                midMove();
                break;
            case 2:
                godlikeMove();
                break;
            default:
                break;
        }

    }

    private void godlikeMove() {
    }

    private void midMove() {
    }

    private void easyMove(){
        while(true){
            int nextposx = RandomGenerator.getDefault().nextInt(0, groesse);
            int nextposy = RandomGenerator.getDefault().nextInt(0, groesse);
            Position pos = new Position(nextposx, nextposy);
            System.out.println(pos);
            if(!felder.contains(pos)){
                System.out.println(pos + "ist neu, wird hinzugefügt");
                felder.add(pos);
                mainGameController.receiveShoot(nextposx, nextposy);
                break;
            }else{
                System.out.println(pos + "wird ignoriert");
            }

        }
    }
}
