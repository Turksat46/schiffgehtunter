package com.turksat46.schiffgehtunter.other;

import java.util.Objects;

public class Position {
    public int x, y;

    public static final Position DOWN = new Position(0,1);
    public static final Position UP = new Position(0,-1);
    public static final Position LEFT = new Position(-1,0);
    public static final Position RIGHT = new Position(1,0);
    public static final Position ZERO = new Position(0,0);

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Position(Position positionToCopy) {
        this.x = positionToCopy.x;
        this.y = positionToCopy.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void add(Position otherPosition) {
        this.x += otherPosition.x;
        this.y += otherPosition.y;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }
}
