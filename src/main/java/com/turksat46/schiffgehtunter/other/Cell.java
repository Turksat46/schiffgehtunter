package com.turksat46.schiffgehtunter.other;

import java.util.Objects;

public class Cell {
    int col;
    int row;

    public Cell(int col, int row) {
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return col == cell.col && row == cell.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return col + ", " + row;
    }
}