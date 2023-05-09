package org.cannonalgorithm.algorithms.cannonparallel;

import org.cannonalgorithm.common.Result;
import org.cannonalgorithm.utils.MatrixUtil;

import java.util.concurrent.RecursiveAction;

public class BlockMultiplicationTask extends RecursiveAction {
    private int[][] first;
    private int[][] second;
    private int rowOffset;
    private int colOffset;
    private int multiplicationOffset;
    private int step;
    private final Result result;

    public BlockMultiplicationTask(int[][] first, int[][] second, int rowOffset,
                                   int colOffset, int multiplicationOffset, int step,
                                   Result result) {
        this.first = first;
        this.second = second;
        this.rowOffset = rowOffset;
        this.colOffset = colOffset;
        this.multiplicationOffset = multiplicationOffset;
        this.step = step;
        this.result = result;
    }

    @Override
    protected void compute() {
        int firstRowSize = step, firstColSize = step,
                secondRowSize = step, secondColSize = step;

        if (rowOffset + step > first.length) {
            firstRowSize = first.length - rowOffset;
        }
        if (multiplicationOffset + step > first[0].length) {
            firstColSize = secondRowSize = first[0].length - multiplicationOffset;
        }
        if (colOffset + step > second[0].length) {
            secondColSize = second[0].length - colOffset;
        }

        int[][] blockFirst = MatrixUtil.copyBlock(first, rowOffset,
                rowOffset + firstRowSize, multiplicationOffset,
                multiplicationOffset + firstColSize);
        int[][] blockSecond = MatrixUtil.copyBlock(second, multiplicationOffset,
                multiplicationOffset + secondRowSize, colOffset,
                colOffset + secondColSize);

        int[][] resBlock = MatrixUtil.multiplyBlocks(blockFirst, blockSecond);
        for (int i = 0; i < resBlock.length; i++) {
            for (int j = 0; j < resBlock[0].length; j++) {
                synchronized (result.getResult()[i + rowOffset]) {
                    result.addToElement(i + rowOffset, j + colOffset, resBlock[i][j]);
                }
            }
        }
    }
}
