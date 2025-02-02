package com.turksat46.schiffgehtunter.other;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * Die Klasse {@code Feld} repräsentiert ein einzelnes Spielfeld im Spiel.
 * Sie erbt von {@code Rectangle} und kann als ein Rasterelement mit bestimmten Eigenschaften betrachtet werden.
 */
public class Feld extends Rectangle {

    public boolean istSchiff;
    public int posX, posY;
    public int hoehe, breite;
    public boolean wurdeGetroffen;

    /**
     * Erstellt ein neues Feld mit einer definierten Höhe, Breite und Position.
     *
     * @param h    Die Höhe des Feldes.
     * @param b    Die Breite des Feldes.
     * @param posX Die X-Koordinate des Feldes.
     * @param posY Die Y-Koordinate des Feldes.
     */
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
    /**
     * Standardkonstruktor für den Bot.
     * Initialisiert ein Feld ohne spezifische Abmessungen oder Position.
     */
    public Feld(){
        this.posX = posX;
        this.posY = posY;
        this.istSchiff = false;
        this.wurdeGetroffen = false;
    }


    /**
     * Setzt das Hintergrundbild des Feldes mit einer gegebenen Textur.
     *
     * @param sandTexture Das zu setzende Bild als {@code Image}.
     */
    public void setImage(Image sandTexture) {
        this.setFill(new ImagePattern(sandTexture));
    }
}
