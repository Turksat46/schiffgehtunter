package com.turksat46.schiffgehtunter.other;

//Dies ist die Klasse für ein einzelnes Feld

public class Feld {

    //Für Debugging
    int wert = 0;
    int[][] position;
    public Feld(int[][] position){
        this.position = position;
    }

    public int[][] getPosition(){
        return position;
    }

    public void setWert(int[][] position, int wert){
        this.wert = wert;
    }

    public int getWert(int[][] position){
        return wert;
    }

    public void selectingFeld(int x, int y){
        System.out.println("Feld selecting");
    }
}
