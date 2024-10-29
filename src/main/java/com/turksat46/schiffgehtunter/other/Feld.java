package com.turksat46.schiffgehtunter.other;

//Dies ist die Klasse für ein einzelnes Feld

import javafx.scene.shape.Rectangle;

public class Feld extends Rectangle {

    //Für Debugging
    int wert = 0;

    int hoehe, breite;

    public Feld(int h, int b){
        this.hoehe = h;
        this.setHeight((double)h);
        this.breite = b;
        this.setWidth((double)b);
    }

}
