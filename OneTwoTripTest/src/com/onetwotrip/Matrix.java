package com.onetwotrip;

public class Matrix {

    public final int rowCount;
    public final int columnCount;

    public final char[][] matrix;

    public Matrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;

        this.matrix = new char[rowCount][columnCount];
    }
}
