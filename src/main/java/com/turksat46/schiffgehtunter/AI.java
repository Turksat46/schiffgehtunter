package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class AI {
    int difficulty;
    int[][] feld;
    int groesse;

    List<Position> felder = new ArrayList<Position>();

    MainGameController mainGameController;

    public AI(int difficulty, int groesse, MainGameController mainGameController){
        this.difficulty = difficulty;
        this.groesse = groesse;
        setShips();
        this.mainGameController = mainGameController;
    }

    private void setShips() {
        //Platziere random Schiffe auf den Feldern


    }

    public void receiveMove(int posx, int posy){
        //Angriff wurde initiiert, Gegenangriff straten
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

    public void getNextMove(){

    }
}
