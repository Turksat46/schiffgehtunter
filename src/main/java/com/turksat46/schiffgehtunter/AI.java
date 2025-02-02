package com.turksat46.schiffgehtunter;

import com.turksat46.schiffgehtunter.other.Feld;
import com.turksat46.schiffgehtunter.other.Music;
import com.turksat46.schiffgehtunter.other.Position;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.random.RandomGenerator;

public class AI {
    int difficulty;
    static int[][] feld;
    static Feld[][] spielerFeld;
    static int groesse;

    static private Map<Integer, List<Position>> ships = new HashMap<>();
    static private int shipId = 0;

    static Set<Position> felder = new HashSet<>(); // Geändert zu HashSet

    static List<Position> entdeckteSchiffe = new ArrayList<>();

    static MainGameController mainGameController;
    static MultipayerMainGameController multiplayerMainGameController;
    private boolean huntmode = true;
    private boolean targetmode = false;

    protected List<Position> validMoves;

    private boolean isMultiplayer;

    // Konstanten
    private static final double PROBABILITY_INCREASE = 0.2;

    public AI(int difficulty, int groesse, MainGameController mainGameController){
        this.difficulty = difficulty;
        this.groesse = groesse;
        feld = new int[groesse][groesse];
        setShips();
        this.mainGameController = mainGameController;
        createValidMoves();
        Collections.shuffle(validMoves);
        spielerFeld = new Feld[groesse][groesse];
        for (int x = 0; x < groesse; x++) {
            for (int y = 0; y < groesse; y++) {
                spielerFeld[x][y] = new Feld();
            }
        }
    }

    private void createValidMoves() {
        validMoves = new ArrayList<>();
        for(int x = 0; x < groesse; x++) {
            for(int y = 0; y < groesse; y++) {
                validMoves.add(new Position(x,y));
            }
        }
    }

    public AI(int difficulty, int groesse, MainGameController mainGameController, Map<String, Object>data){
        this.difficulty = difficulty;
        this.groesse = groesse;

    }

    public AI(int difficulty, int groesse, MultipayerMainGameController multiplayerMainGameController){
        this.difficulty = difficulty;
        this.groesse = groesse;
        feld = new int[groesse][groesse];
        isMultiplayer = true;
        this.multiplayerMainGameController = multiplayerMainGameController;
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
                            if(isValid(new Position(x + k, y + l))){
                                System.out.println("Schiff wurde an "+(x+k)+","+(y+l)+ " gesetzt.");
                                eventuelleStartPositionen.remove(new Position(x + k, y + l));
                            }
                        }
                    }
                }
            }

        }

    }

    public void receiveMove(int posx, int posy, Spielfeld spielfeld){
        //Angriff wurde initiiert, Gegenangriff starten
        Position hitPosition = new Position(posx, posy);
        if(feld[posx][posy] == 1){
            System.out.println(posx + " " + posy + " wurde getroffen!");
            feld[posx][posy] = 0;
            spielfeld.selectFeld(posx,posy, Color.GREEN);

            Iterator<Map.Entry<Integer, List<Position>>> iterator = ships.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, List<Position>> entry = iterator.next();
                List<Position> shipPositions = entry.getValue();
                if (shipPositions.remove(hitPosition)) { // Entfernt das Element, falls vorhanden, und gibt true zurück
                    if (shipPositions.isEmpty()) {
                        System.out.println("Schiff " + entry.getKey() + " versenkt!");
                        iterator.remove(); // Sicher entfernen mit iterator.remove()
                        Music sound = Music.getInstance();
                        sound.playShipDestroyed();
                        if(ships.isEmpty()){
                            //Spieler hat gewonnen
                            mainGameController.handleWinForPlayer();
                            //Durch diesen Return wird der State niemals zurückgesetzt, sprich es ist kein weiterer Zug möglich (Feature-Bug :D)
                            return;
                        }
                    }
                }
            }
        }
        mainGameController.handleHit(posx, posy);
        getNextMove(); //Diese Methode darf erst aufgerufen werden, wenn receiveMove fertig ist (wegen der Exception)
    }

    public void receiveHit(int x, int y, boolean isShip, boolean wholeShip){
        System.out.println("AI.java: Position an: " + x + " " + y + " ist ein Schiff: "+isShip+" und wurde zerstört: "+wholeShip);
        if(isShip){
            entdeckteSchiffe.add(new Position(x, y));
        }
        if(isShip && wholeShip){
            updateShipHits(new Position(x, y));
        }
    }

    private boolean containsAllPositions(List<Position> positionsToSearch, List<Position> listToSearchIn) {
        for(Position searchPosition : positionsToSearch) {
            boolean found = false;
            for(Position searchInPosition : listToSearchIn) {
                if(searchInPosition.equals(searchPosition)) {
                    found = true;
                    break;
                }
            }
            if(!found) return false;
        }
        return true;
    }

    public void allShipsShot(){
        mainGameController.handleWinForOpponent();
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

    private void midMove() {
        double[][] probabilities = calculateProbabilities();
        Random random = new Random();
        double probability = random.nextDouble();
        if(probability < 0.5){
            Position bestMove = selectBestMove(probabilities);
            validMoves.remove(bestMove);
            mainGameController.receiveShoot(bestMove.x, bestMove.y);
        }else{
            easyMove();
        }

    }


    private double[][] calculateProbabilities() {
        double[][] probabilities = new double[groesse][groesse];
        // Initialisierung mit Grundwahrscheinlichkeit (z.B. 1/Anzahl verbleibender Felder)
        double baseProbability = 1.0 / validMoves.size();
        for (int x = 0; x < groesse; x++) {
            for (int y = 0; y < groesse; y++) {
                probabilities[x][y] = baseProbability;
            }
        }

        // Erhöhe Wahrscheinlichkeit um entdeckte Schiffe herum
        for (Position hit : entdeckteSchiffe) {
            for (Position adjacent : getBestCells(hit)) {
                if (isValid(adjacent)) {
                    probabilities[adjacent.x][adjacent.y] += PROBABILITY_INCREASE;
                }
            }
        }

        // Reduziere Wahrscheinlichkeit für bereits beschossene Felder
        for (Position move : felder) {
            probabilities[move.x][move.y] = 0;
        }

        return probabilities;
    }

    private Position selectBestMove(double[][] probabilities) {
        Position bestMove = null;
        double highestProbability = -1;
        for (Position move : validMoves) {
            if (probabilities[move.x][move.y] > highestProbability) {
                highestProbability = probabilities[move.x][move.y];
                bestMove = move;
            }
        }
        return bestMove;
    }

    private void godlikeMove() {
        Position selectedMove;
        if(!entdeckteSchiffe.isEmpty()){
            System.out.println("Schiff entdeckt!");
            selectedMove = getBestStrategy();
        }else{
            System.out.println("Random move!");
            selectedMove = getBestHuntingMove();
        }
        //updateShipHits(selectedMove);
        validMoves.remove(selectedMove);
        mainGameController.receiveShoot(selectedMove.x, selectedMove.y);
    }

    private void updateShipHits(Position selectedMove) {
        Iterator<Position> iterator = entdeckteSchiffe.iterator();
        while (iterator.hasNext()) {
            Position shipPosition = iterator.next();
            iterator.remove(); // Sicher entfernen mit Iterator.remove()
        }
        System.out.println("entdeckteSchiffe: " + entdeckteSchiffe);
    }

    private Position getBestHuntingMove() {
        Position position = validMoves.get(0);;
        int highestNotAttacked = -1;
        for(int i = 0; i < validMoves.size(); i++) {
            int testCount = getAdjacentNotAttackedCount(validMoves.get(i));
            if(testCount == 4) { // Maximum found, just return immediately
                return validMoves.get(i);
            } else if(testCount > highestNotAttacked) {
                highestNotAttacked = testCount;
                position = validMoves.get(i);
            }
        }
        return position;
    }

    private int getAdjacentNotAttackedCount(Position position) {
        List<Position> adjacentCells = getBestCells(position);
        int notAttackedCount = 0;
        for (Position adjacentCell : adjacentCells) {
            if (validMoves.contains(adjacentCell)) {
                notAttackedCount++;
            }
        }
        return notAttackedCount;
    }

    private Position getBestStrategy() {
        List<Position> suggestedMoves = getNextBestMoves();
        if (suggestedMoves.isEmpty()) {
            return getBestHuntingMove(); // Fallback, falls keine vorgeschlagenen Züge mehr vorhanden sind.
        }
        Position bestMove = suggestedMoves.get(0);
        int bestScore = -1;
        for (Position move : suggestedMoves) {
            int score = getAdjacentNotAttackedCount(move);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private boolean atLeastTwoHitsInDirection(Position start, Position direction) {
        Position testPosition = new Position(start);
        testPosition.add(direction);
        if(!entdeckteSchiffe.contains(testPosition)) return false;
        testPosition.add(direction);
        if(!entdeckteSchiffe.contains(testPosition)) return false;
        return true;
    }

    private List<Position> getNextBestMoves() {
        List<Position> result = new ArrayList<>();
        for (Position shipHitPosition : entdeckteSchiffe) {
            List<Position> nextBestMoves = getBestCells(shipHitPosition);
            for (Position adjacentPosition : nextBestMoves) {
                if (validMoves.contains(adjacentPosition)) {
                    result.add(adjacentPosition);
                }
            }
        }
        return result;
    }

    private List<Position> getBestCells(Position position) {
        List<Position> result = new ArrayList<>();
        if(isValid(new Position(position.getX()-1, position.getY()))) {
            Position left = new Position(position);
            left.add(Position.LEFT);
            result.add(left);
        }
        if(isValid(new Position(position.getX()+1, position.getY()))) {
            Position right = new Position(position);
            right.add(Position.RIGHT);
            result.add(right);
        }
        if(isValid(new Position(position.getX(), position.getY()-1))) {
            Position up = new Position(position);
            up.add(Position.UP);
            result.add(up);
        }
        if(isValid(new Position(position.getX(), position.getY()+1))) {
            Position down = new Position(position);
            down.add(Position.DOWN);
            result.add(down);
        }
        return result;
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

    public int[] calculateMultiplayerMove() {
        while (true) {
            int nextposx = RandomGenerator.getDefault().nextInt(0, groesse);
            int nextposy = RandomGenerator.getDefault().nextInt(0, groesse);
            Position pos = new Position(nextposx, nextposy);
            if (!felder.contains(pos)) {
                felder.add(pos);
                return new int[]{nextposx, nextposy}; // Gib die Position [x, y] für den nächsten Zug zurück.
            }
        }
    }

    // Hilfsmethode, um zu prüfen, ob eine Position valide ist
    private boolean isValid(Position p) {
        return p.x >= 0 && p.x < groesse && p.y >= 0 && p.y < groesse;
    }

}