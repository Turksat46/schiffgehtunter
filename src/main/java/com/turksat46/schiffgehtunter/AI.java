package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

public class AI {
    int difficulty;
    int[][] feld;
    static int groesse;

    List<Position> felder = new ArrayList<Position>();

    MainGameController mainGameController;

    public AI(int difficulty, int groesse, MainGameController mainGameController){
        this.difficulty = difficulty;
        this.groesse = groesse;
        feld = new int[groesse][groesse];
        setShips();
        this.mainGameController = mainGameController;
    }

    private void setShips() {
        //Platziere random Schiffe auf den Feldern
        int mindestAnzahlSchiffe = (int)(groesse*groesse*0.3);
        int currentAnzahlSchiffe = 0;

        List<Position> eventuelleStartPositionen = new ArrayList<>();
        for(int i = 0; i < groesse; i++){
            for(int j = 0; j < groesse; j++){
                eventuelleStartPositionen.add(new Position(i, j));
            }
        }
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
                if(feld[x][y] != 0){
                    kannPlatziertwerden = false;
                    break;
                }
            }

            if(kannPlatziertwerden){
                for(int j = 0; j < schiffGroesse; j++){
                    int x = horizontal ? startPosition.getX() + j : startPosition.getX();
                    int y = horizontal ? startPosition.getY() : startPosition.getY() + j;
                    feld[x][y] = 1;
                }
                currentAnzahlSchiffe+=schiffGroesse;

                for(int j = 0; j < schiffGroesse; j++){
                    int x = horizontal ? startPosition.getX() + j : startPosition.getX();
                    int y = horizontal ? startPosition.getY() : startPosition.getY() + j;
                    for(int k = -1; k <= 1; k++){
                        for(int l = -1; l <= 1; l++){
                            if(x + k >= 0 && x + k < groesse && y + l >= 0 && y + l < groesse ){
                                System.out.println("Schiff wurde an "+x+k+","+y+l+ " gesetzt.");
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
            mainGameController.handleHit(posx, posy);
        }
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
