package com.onetwotrip;

import java.util.ArrayList;
import java.util.List;

public class SymbolParams {

    private List<Params> params;

    private List<Position> symbolPositions;

    public SymbolParams(char originalChar, int indexInLine) {
        this.params = new ArrayList<>();
        this.params.add(new Params(originalChar, indexInLine));

        this.symbolPositions = new ArrayList<>();
    }

    public boolean hasEmptyPositions() {
        return symbolPositions.size() < params.size();
    }

    public void addPosition(int row, int column) {
        symbolPositions.add(new Position(row, column));
    }

    public void addParams(char originalChar, int indexInLine) {
        params.add(new Params(originalChar, indexInLine));
    }

    public int size() {
        return params.size();
    }

    public int getOriginalIndexAt(int index) {
        return params.get(index).indexInLine;
    }

    public char getCharAt(int index) {
        return params.get(index).originalChar;
    }

    public int getRowPositionAt(int index) {
        if (symbolPositions.size() > index) {
            return symbolPositions.get(index).row;
        } else {
            return -1;
        }
    }

    public int getColumnPositionAt(int index) {
        if (symbolPositions.size() > index) {
            return symbolPositions.get(index).column;
        } else {
            return -1;
        }
    }

    public String toString(int index) {
        return String.valueOf(getCharAt(index)) +
                " - (" +
                getRowPositionAt(index) +
                ", " +
                getColumnPositionAt(index) +
                ")";
    }

    private static class Position {

        public final int row;
        public final int column;

        private Position(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }

    private static class Params {

        public final char originalChar;
        public final int indexInLine;

        private Params(char originalChar, int indexInLine) {
            this.originalChar = originalChar;
            this.indexInLine = indexInLine;
        }
    }
}
