package com.turksat46.schiffgehtunter.other;

//Dies ist die Klasse für ein einzelnes Feld

import javafx.scene.shape.Rectangle;

public class Feld extends Rectangle {

    private int gesetzt=0;
    int hoehe, breite;

    public Feld(int h, int b){
        this.hoehe = h;
        this.setHeight((double)h);
        this.breite = b;
        this.setWidth((double)b);
    }

    public void setzen(){

        if(gesetzt == 0){
            System.out.println("leer oder schon abgeschossen");
            gesetzt = 1;
        }else if (gesetzt == 1){
            gesetzt = 0;
            System.out.println("getroffen");
        }
    }



}
