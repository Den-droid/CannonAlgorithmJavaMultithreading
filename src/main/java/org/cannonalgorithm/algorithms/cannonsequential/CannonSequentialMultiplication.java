package org.cannonalgorithm.algorithms.cannonsequential;

import org.cannonalgorithm.algorithms.MultiplicationAlgorithm;
import org.cannonalgorithm.common.Result;
import org.cannonalgorithm.utils.MatrixUtil;

public class CannonSequentialMultiplication implements MultiplicationAlgorithm {
    private final int[][] first;
    private final int[][] second;
    private final Result result;
    private int step;

    public CannonSequentialMultiplication(int[][] first, int[][] second) {
        this.first = first;
        this.second = second;
        this.result = new Result(first.length, second[0].length);

        setInitialStep();
    }

    public CannonSequentialMultiplication(int[][] first, int[][] second, int step) {
        this.first = first;
        this.second = second;
        this.result = new Result(first.length, second[0].length);
        this.step = step;

        if (!isValidStep())
            setInitialStep();
    }

    private boolean isValidStep() {
        if (first[0].length < 2 || second.length < 2) {
            throw new IllegalArgumentException("Number of columns in first matrix and " +
                    "number of rows in second matrix must be at least 2!!!");
        }
        int maxDimension = Math.max(first.length, second[0].length);
        if (first[0].length < step
                || (maxDimension / step > first[0].length / step
                && !(maxDimension % step == 0 && maxDimension - first[0].length <= step))
        ) {
            return false;
        }
        return true;
    }

    private void setInitialStep() {
        if (first[0].length >= 500) {
            step = 100;
        } else if (first[0].length >= 100) {
            step = 20;
        } else if (first[0].length >= 10) {
            step = 5;
        } else {
            step = first[0].length - 1;
        }

        int maxDimension = Math.max(first.length, second[0].length);
        if (maxDimension / step > first[0].length / step
                && !(maxDimension % step == 0 && maxDimension - first[0].length <= step)) {
            int lowerStepBound, higherStepBound, newStep;
            for (int i = 0; i < maxDimension - step; i++) {
                newStep = step + i;
                lowerStepBound = (first[0].length / newStep) * newStep;
                higherStepBound = ((first[0].length / newStep) * newStep) + newStep;
                if ((first[0].length > lowerStepBound && first[0].length < higherStepBound)
                        && (first.length > lowerStepBound && first.length < higherStepBound)) {
                    step = newStep;
                    break;
                }
            }
        }
    }

    @Override
    public void resetResult() {
        this.result.reset();
    }

    @Override
    public int[][] getResult() {
        return result.getResult();
    }

    @Override
    public void multiply() {
        int leftShiftInitial = Math.min(first[0].length, first.length);
        int upShiftInitial = Math.min(second.length, second[0].length);

        for (int i = step; i < leftShiftInitial; i += step) {
            int innerStep = i + step > first.length ? first.length - i : step;
            for (int j = 0; j < innerStep; j++) {
                MatrixUtil.shiftLeft(first, i + j, i);
            }
        }

        for (int j = step; j < upShiftInitial; j += step) {
            int innerStep = j + step > second[0].length ? second[0].length - j : step;
            for (int k = 0; k < innerStep; k++) {
                MatrixUtil.shiftUp(second, j + k, j);
            }
        }

        for (int i = 0; i < first.length; i += step) {
            for (int j = 0; j < second[0].length; j += step) {
                for (int m = 0; m < first[i].length; m += step) {
                    int firstRowSize = step, firstColSize = step,
                            secondRowSize = step, secondColSize = step;

                    if (i + step > first.length) {
                        firstRowSize = first.length - i;
                    }
                    if (m + step > first[0].length) {
                        firstColSize = secondRowSize = first[0].length - m;
                    }
                    if (j + step > second[0].length) {
                        secondColSize = second[0].length - j;
                    }

                    int[][] blockFirst = MatrixUtil.copyBlock(first, i, i + firstRowSize,
                            m, m + firstColSize);
                    int[][] blockSecond = MatrixUtil.copyBlock(second, m, m + secondRowSize,
                            j, j + secondColSize);

                    int[][] resBlock = MatrixUtil.multiplyBlocks(blockFirst, blockSecond);
                    for (int blockRow = 0; blockRow < resBlock.length; blockRow++) {
                        for (int blockCol = 0; blockCol < resBlock[blockRow].length; blockCol++) {
                            result.addToElement(i + blockRow, j + blockCol,
                                    resBlock[blockRow][blockCol]);
                        }
                    }
                }

                int leftShiftStep = i + step > first.length ? first.length - i : step;
                for (int i1 = i; i1 < i + leftShiftStep; i1++) {
                    MatrixUtil.shiftLeft(first, i1, step);
                }

                int upShiftStep = j + step > second[0].length ? second[0].length - j : step;
                for (int j2 = j; j2 < j + upShiftStep; j2++) {
                    MatrixUtil.shiftUp(second, j2, step);
                }
            }
        }
    }
}
