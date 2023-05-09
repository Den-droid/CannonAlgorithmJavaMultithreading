package org.cannonalgorithm.algorithms.simplesequential;

import org.cannonalgorithm.algorithms.MultiplicationAlgorithm;
import org.cannonalgorithm.common.Result;

public class SequentialMultiplication implements MultiplicationAlgorithm {
    private final int[][] first;
    private final int[][] second;
    private Result result;

    public SequentialMultiplication(int[][] first, int[][] second) {
        this.first = first;
        this.second = second;
        this.result = new Result(first.length, second[0].length);
    }

	@Override
    public void multiply() {
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < second[0].length; j++) {
                int sum = 0;
                for (int k = 0; k < second.length; k++) {
                    sum += first[i][k] * second[k][j];
                }
                result.setElement(i, j, sum);
            }
        }
    }

    @Override
    public int[][] getResult() {
        return result.getResult();
    }

    @Override
    public void resetResult() {
        this.result = new Result(first.length, second[0].length);
    }
}
