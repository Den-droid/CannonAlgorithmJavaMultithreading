package org.cannonalgorithm.algorithms.cannonparallel;

import org.cannonalgorithm.algorithms.MultiplicationAlgorithm;
import org.cannonalgorithm.common.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class CannonParallelMultiplication extends RecursiveAction implements MultiplicationAlgorithm {
    private final int[][] first;
    private final int[][] second;
    private final Result result;
    private int step;
    private int threadsCount;

    public CannonParallelMultiplication(int[][] first, int[][] second) {
        this.first = first;
        this.second = second;
        this.result = new Result(first.length, second[0].length);
        this.threadsCount = Runtime.getRuntime().availableProcessors();

        setInitialStep();
    }

    public CannonParallelMultiplication(int[][] first, int[][] second, int step) {
        this.first = first;
        this.second = second;
        this.result = new Result(first.length, second[0].length);
        this.step = step;
        this.threadsCount = Runtime.getRuntime().availableProcessors();

        if (!isValidStep())
            setInitialStep();
    }

    public CannonParallelMultiplication(int[][] first, int[][] second, int step, int threadsCount) {
        this.first = first;
        this.second = second;
        this.step = step;
        this.threadsCount = threadsCount;
        this.result = new Result(first.length, second[0].length);

        if (!isValidStep())
            setInitialStep();
    }

    private CannonParallelMultiplication(int[][] first, int[][] second, Result result, int step) {
        this.first = first;
        this.second = second;
        this.result = result;
        this.step = step;
    }

    private boolean isValidStep() {
        if (first[0].length != second.length || first[0].length < 2) {
            throw new IllegalArgumentException("Number of columns in first matrix and " +
                    "number of rows in second matrix must be equal and at least 2!!!");
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
    public void multiply() {
        ForkJoinPool pool = new ForkJoinPool(threadsCount);
        pool.invoke(new CannonParallelMultiplication(first, second, result, step));
    }

    @Override
    public int[][] getResult() {
        return result.getResult();
    }

    @Override
    protected void compute() {
        List<ShiftTask> shiftTaskList = new ArrayList<>();
        int leftShiftInitial = Math.min(first[0].length, first.length);
        int upShiftInitial = Math.min(second.length, second[0].length);

        for (int i = step; i < leftShiftInitial; i += step) {
            int innerStep = i + step > first.length ? first.length - i : step;
            for (int j = 0; j < innerStep; j++) {
                ShiftTask task = new ShiftTask(first, i + j, i, true);
                task.fork();
                shiftTaskList.add(task);
            }
        }

        for (int j = step; j < upShiftInitial; j += step) {
            int innerStep = j + step > second[0].length ? second[0].length - j : step;
            for (int k = 0; k < innerStep; k++) {
                ShiftTask task = new ShiftTask(second, j + k, j, false);
                task.fork();
                shiftTaskList.add(task);
            }
        }

        for (ShiftTask shiftTask : shiftTaskList) {
            shiftTask.join();
        }
        shiftTaskList.clear();

        List<BlockMultiplicationTask> cmtList = new ArrayList<>();
        for (int i = 0; i < first.length; i += step) {
            for (int j = 0; j < second[0].length; j += step) {
                for (int m = 0; m < first[0].length; m += step) {
                    BlockMultiplicationTask cmt = new BlockMultiplicationTask(first, second, i, j,
                            m, step, result);

                    cmt.fork();
                    cmtList.add(cmt);
                }

                for (BlockMultiplicationTask task : cmtList) {
                    task.join();
                }

                int leftShift = i + step > first.length ? first.length - i : step;
                for (int i1 = i; i1 < i + leftShift; i1++) {
                    ShiftTask task = new ShiftTask(first, i1, step, true);
                    task.fork();
                    shiftTaskList.add(task);
                }

                int upShift = j + step > second[0].length ? second[0].length - j : step;
                for (int j2 = j; j2 < j + upShift; j2++) {
                    ShiftTask task = new ShiftTask(second, j2, step, false);
                    task.fork();
                    shiftTaskList.add(task);
                }

                for (ShiftTask shiftTask : shiftTaskList) {
                    shiftTask.join();
                }
                shiftTaskList.clear();
            }
        }
    }
}
