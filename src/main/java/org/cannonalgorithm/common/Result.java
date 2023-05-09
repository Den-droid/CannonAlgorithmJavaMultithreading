package org.cannonalgorithm.common;

public class Result {
    private int[][] result;

    public Result(int row, int column) {
        result = new int[row][column];
    }

    public void setElement(int row, int column, int element) {
        this.result[row][column] = element;
    }

    public void addToElement(int row, int column, int toAdd) {
        this.result[row][column] += toAdd;
    }

    public void reset() {
        result = new int[result.length][result[0].length];
    }

    public int[][] getResult() {
        return this.result;
    }
}
