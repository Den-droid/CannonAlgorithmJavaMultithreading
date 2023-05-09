package org.cannonalgorithm.algorithms.cannonparallel;

import org.cannonalgorithm.utils.MatrixUtil;

import java.util.concurrent.RecursiveAction;

public class ShiftTask extends RecursiveAction {
    private int[][] matrix;
    private int start;
    private int amount;
    private boolean shiftLeft;

    public ShiftTask(int[][] matrix, int start, int amount, boolean shiftLeft) {
        this.matrix = matrix;
        this.start = start;
        this.amount = amount;
        this.shiftLeft = shiftLeft;
    }

    @Override
    protected void compute() {
        if (shiftLeft)
            MatrixUtil.shiftLeft(matrix, start, amount);
        else
            MatrixUtil.shiftUp(matrix, start, amount);
    }
}
