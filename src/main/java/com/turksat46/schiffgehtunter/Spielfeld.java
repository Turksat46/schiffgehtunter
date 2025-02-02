package com.turksat46.schiffgehtunter;
import com.turksat46.schiffgehtunter.other.Feld;
import com.turksat46.schiffgehtunter.other.Music;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;
import java.util.Objects;

public class Spielfeld {

    public GridPane gridPane;
    int zellengroesse, groesse;
    public int[][] feld;
    public Feld[][] felder;
    MainGameController mainGameController;
    MultipayerMainGameController multipayerMainGameController;
    boolean istGegnerFeld;
    boolean isMultiplayer;

    Image sandTexture;
    Image tntTexture;

    public Spielfeld (int groesse, boolean istGegnerFeld, boolean isMultiplayer){
        this.feld= new int [groesse][groesse];
        this.isMultiplayer = isMultiplayer;
        this.gridPane = createGridPane();
        this.groesse = groesse;
        felder = new Feld[groesse][groesse];
        if (isMultiplayer){
            multipayerMainGameController = new MultipayerMainGameController();
        }
        else{
            mainGameController = new MainGameController();

        }
        this.istGegnerFeld = istGegnerFeld;
        initFeld(isMultiplayer);

        // Lade die Texturen
        try {
            sandTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/sand_texture.png")));
            tntTexture = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/turksat46/schiffgehtunter/images/tnt.png")));
        } catch (NullPointerException e) {
            System.err.println("Fehler beim Laden der Texturen. Stelle sicher, dass die Dateien im Ressourcenordner liegen.");
            throw e;
        }

    }

    public Spielfeld (int groesse, boolean istGegnerFeld, boolean isMultiplayer, Map<String, Object> data){
        this.feld= new int [groesse][groesse];
        this.isMultiplayer = isMultiplayer;
        this.gridPane = createGridPane();
        this.groesse = groesse;
        felder = new Feld[groesse][groesse];
        if (isMultiplayer){
            multipayerMainGameController = new MultipayerMainGameController();
        }
        else{
            mainGameController = new MainGameController();

        }
        this.istGegnerFeld = istGegnerFeld;
        initFeld(isMultiplayer);

    }

    // TODO:Ich schaue hier noch ob wir des so lassen
    // soll bei kleinen felder random schiffsgroeße aufgewöhlt werden oder greedy benutzen ???
    // TODO: IDEE ist jz umgedrehter greedy algorihtmus
    private void initFeld(boolean isMultiplayer){

        if(groesse <=5 ){
            zellengroesse=75;
        }else if(groesse > 5 && groesse <= 10){
            zellengroesse=50;

        }else if(groesse > 10 && groesse <= 20){
            zellengroesse=30;

        }else {
            zellengroesse=20;

        }


    }


    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        //grid.setMaxHeight(4000);
        //grid.setMaxWidth(4000);

        for (int zeile = 0; zeile < groesse; zeile++) {



            for (int reihe = 0; reihe < groesse; reihe++) {
                int col = reihe;
                int row = zeile;
                Rectangle cell = new Rectangle(zellengroesse,zellengroesse, Color.TRANSPARENT);
                cell.setStroke(Color.BLACK);
                // Klick-Event für jede Zelle
                cell.setOnMouseClicked(event -> {
                    if (!isMultiplayer) {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            mainGameController.handlePrimaryClick(this, row, col);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            mainGameController.handleSecondaryClick(this, row, col);
                        }
                    }
                    else {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            multipayerMainGameController.handlePrimaryClick(this, row, col);
                        } else if (event.getButton() == MouseButton.SECONDARY) {
                            multipayerMainGameController.handleSecondaryClick(this, row, col);
                        }
                    }

                });


                grid.add(cell, col, row);


            }
        }
        return grid;

    }

    public void selectFeld(int posx, int posy){
        //TODO: Eventuell hier die Farbe ändern, wenn ein Schiff angeklickt wird
        //felder[posx][posy].setFill(Color.RED);
        felder[posx][posy].setImage(sandTexture);
        Music sound = Music.getInstance();
        sound.playMiss();
    }

    //Wenn Feld ein Schiff ist, wird das ausgeführt
    public void selectFeld(int posx, int posy, Color color){
        //TODO: Eventuell hier die Farbe ändern, wenn ein Schiff angeklickt wird
        System.out.println("selectFeld mit Farbe wurde ausgewählt");
        felder[posx][posy].setImage(tntTexture);
        Music sound = Music.getInstance();
        sound.playHit();
    }

}