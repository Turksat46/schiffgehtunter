package com.turksat46.schiffgehtunter;

import java.util.random.RandomGenerator;

public class AI {
    int difficulty;
    int[][] feld;
    int groesse;

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
        int nextposx = RandomGenerator.getDefault().nextInt(0, groesse);
        int nextposy = RandomGenerator.getDefault().nextInt(0, groesse);
        mainGameController.receiveShoot(nextposx, nextposy);
    }

    public void getNextMove(){

    }
}
