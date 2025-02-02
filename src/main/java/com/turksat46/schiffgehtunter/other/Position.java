package com.turksat46.schiffgehtunter.other;

import java.util.Objects;

/**
 * Die Klasse Position repräsentiert eine zweidimensionale Koordinate mit X- und Y-Werten.
 */
public class Position {
    public int x, y;

    public static final Position DOWN = new Position(0,1);
    public static final Position UP = new Position(0,-1);
    public static final Position LEFT = new Position(-1,0);
    public static final Position RIGHT = new Position(1,0);
    public static final Position ZERO = new Position(0,0);

    /**
     * Erstellt eine neue Position mit den angegebenen x- und y-Werten.
     *
     * @param x Die X-Koordinate der Position
     * @param y Die Y-Koordinate der Position
     */
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }
    /**
     * Erstellt eine neue Position als Kopie einer vorhandenen Position.
     *
     * @param positionToCopy Die zu kopierende Position
     */
    public Position(Position positionToCopy) {
        this.x = positionToCopy.x;
        this.y = positionToCopy.y;
    }

    /**
     * Gibt die X-Koordinate der Position zurück.
     *
     * @return Der X-Wert der Position
     */
    public int getX() {
        return x;
    }

    /**
     * Gibt die Y-Koordinate der Position zurück.
     *
     * @return Der Y-Wert der Position
     */
    public int getY() {
        return y;
    }


    /**
     * Addiert die Werte einer anderen Position zu dieser Position.
     *
     * @param otherPosition Die Position, deren Werte addiert werden sollen
     */
    public void add(Position otherPosition) {
        this.x += otherPosition.x;
        this.y += otherPosition.y;
    }

    /**
     * Vergleicht diese Position mit einem anderen Objekt auf Gleichheit.
     *
     * @param obj Das Objekt, mit dem verglichen werden soll
     * @return true, wenn die Positionen gleich sind, sonst false
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    /**
     * Gibt den Hashcode für diese Position zurück.
     *
     * @return Der Hashcode der Position
     */
    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
