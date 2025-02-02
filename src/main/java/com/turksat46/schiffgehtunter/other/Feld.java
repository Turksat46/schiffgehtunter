package com.turksat46.schiffgehtunter.other;

//Dies ist die Klasse für ein einzelnes Feld

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Feld extends Rectangle {

    public boolean gesetzt;
    public boolean istSchiff;
    public int posX, posY;
    public int hoehe, breite;
    public boolean wurdeGetroffen;

    public Feld(int h, int b, int posX, int posY) {
        this.hoehe = h;
        this.setHeight((double)h);
        this.breite = b;
        this.setWidth((double)b);
        this.posX = posX;
        this.posY = posY;
        this.istSchiff = false;
        this.wurdeGetroffen = false;
    }

    //Für Bot
    public Feld(){
        this.posX = posX;
        this.posY = posY;
        this.istSchiff = false;
        this.wurdeGetroffen = false;
    }


    public boolean pruefengesetzt() {
        return gesetzt;
    }

    public Rectangle getFeld() {
        return this;
    }

    //For beautyprinting NICHT FÜR LOGIK NUTZEN
    public int getPosX() {return posX+1;}

    public int getPosY() {return posY+1;}

    public void setImage(Image sandTexture) {
        this.setFill(new ImagePattern(sandTexture));
    }
}
