package org.cannonalgorithm;

import org.cannonalgorithm.algorithms.MultiplicationAlgorithm;
import org.cannonalgorithm.algorithms.cannonparallel.CannonParallelMultiplication;
import org.cannonalgorithm.algorithms.cannonsequential.CannonSequentialMultiplication;
import org.cannonalgorithm.algorithms.simplesequential.SequentialMultiplication;
import org.cannonalgorithm.utils.MatrixUtil;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String answer;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println("1. Test Matrices Multiplication");
            System.out.println("2. Test Threads Count");
            System.out.println("3. Test Step Size");
            System.out.println("4. Test Speed Up");
            System.out.println("5. Exit");

            answer = scanner.nextLine();

            switch (answer) {
                case "1" -> {
                    try {
                        System.out.println("Enter first matrix row count: ");
                        int rowFirst = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter first matrix column and second matrix row count: ");
                        int colFirst = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter second matrix column count: ");
                        int colSecond = Integer.parseInt(scanner.nextLine());
                        testResult(rowFirst, colFirst, colSecond);
                    } catch (Exception e) {
                        System.out.println("You've entered wrong number. Try again!!!");
                    }
                }
                case "2" -> testThreadsCount();
                case "3" -> testStepSize();
                case "4" -> testMatrixSize();
                case "5" -> {
                    return;
                }
                default -> System.out.println("Enter valid option!!!");
            }
        } while (!Objects.equals(answer, "5"));
    }

    public static void testResult(int rowFirst, int colFirst, int colSecond) {
        int[][] first = MatrixUtil.generateMatrix(rowFirst, colFirst, 10, 99);
        int[][] second = MatrixUtil.generateMatrix(colFirst, colSecond, 10, 99);
        int[][] cannonParallelFirst =
                MatrixUtil.copyBlock(first, 0, first.length, 0, first[0].length);
        int[][] cannonParallelSecond =
                MatrixUtil.copyBlock(second, 0, second.length, 0, second[0].length);
        int[][] cannonSequentialFirst =
                MatrixUtil.copyBlock(first, 0, first.length, 0, first[0].length);
        int[][] cannonSequentialSecond =
                MatrixUtil.copyBlock(second, 0, second.length, 0, second[0].length);

        CannonParallelMultiplication cpm =
                new CannonParallelMultiplication(cannonParallelFirst, cannonParallelSecond, 100, 100);
        cpm.multiply();

        CannonSequentialMultiplication csm =
                new CannonSequentialMultiplication(cannonSequentialFirst, cannonSequentialSecond, 100);
        csm.multiply();

        SequentialMultiplication sm =
                new SequentialMultiplication(first, second);
        sm.multiply();

        boolean areEqualCpmAndSm = MatrixUtil.areEqual(cpm.getResult(), sm.getResult());
        boolean areEqualCsmAndSm = MatrixUtil.areEqual(csm.getResult(), sm.getResult());
        boolean areEqualCpmAndCsm = MatrixUtil.areEqual(cpm.getResult(), csm.getResult());

        System.out.println("\nEquality of Cannon Parallel and Cannon Sequential Result: "
                + areEqualCpmAndCsm);
        System.out.println("\nEquality of Cannon Parallel and Sequential Result: "
                + areEqualCpmAndSm);
        System.out.println("\nEquality of Cannon Sequential and Sequential Result: "
                + areEqualCsmAndSm);
    }

    public static void testThreadsCount() {
        int size = 1500;
        int min = 10;
        int max = 99;

        int[] threadsCounts = {4, 8, 16, 25, 50, 100, 150};
        int[][] first = MatrixUtil.generateMatrix(size, size, min, max);
        int[][] second = MatrixUtil.generateMatrix(size, size, min, max);
        int experimentsCount = 4;

        for (int threadsCount : threadsCounts) {
            int[][] firstCopy =
                    MatrixUtil.copyBlock(first, 0, first.length, 0, first[0].length);
            int[][] secondCopy =
                    MatrixUtil.copyBlock(second, 0, second.length, 0, second[0].length);
            CannonParallelMultiplication cpm =
                    new CannonParallelMultiplication(firstCopy, secondCopy, 100, threadsCount);

            long averageParallel = getAverageTimeOfExperiments(cpm, experimentsCount);

            System.out.println("Cannon parallel (" + size + "x" + size + "): " +
                    "threads count - " + threadsCount + "; time - " + averageParallel + " ms;");
            System.out.println();
        }
    }

    public static void testStepSize() {
        int size = 1500;
        int min = 10;
        int max = 99;

        int[] steps = {25, 50, 100, 150, 200};
        int[][] first = MatrixUtil.generateMatrix(size, size, min, max);
        int[][] second = MatrixUtil.generateMatrix(size, size, min, max);
        int experimentsCount = 4;

        for (int step : steps) {
            int[][] firstCopy =
                    MatrixUtil.copyBlock(first, 0, first.length, 0, first[0].length);
            int[][] secondCopy =
                    MatrixUtil.copyBlock(second, 0, second.length, 0, second[0].length);
            CannonParallelMultiplication cpm =
                    new CannonParallelMultiplication(firstCopy, secondCopy, step, 8);

            long averageParallel = getAverageTimeOfExperiments(cpm, experimentsCount);

            System.out.println("Cannon parallel (" + size + "x" + size + "): " +
                    "step - " + step + "; time - " + averageParallel + " ms;");
            System.out.println();
        }
    }

    public static void testMatrixSize() {
        int min = 10;
        int max = 99;

        int[] sizes = {100, 300, 500, 1000, 1500, 2000, 2500, 3000};
        int experimentsCount = 4;
        int threadsCount = 8;

        for (int matrixSize : sizes) {
            int[][] first = MatrixUtil.generateMatrix(matrixSize, matrixSize, min, max);
            int[][] second = MatrixUtil.generateMatrix(matrixSize, matrixSize, min, max);

            int[][] firstCopy =
                    MatrixUtil.copyBlock(first, 0, first.length, 0, first[0].length);
            int[][] secondCopy =
                    MatrixUtil.copyBlock(second, 0, second.length, 0, second[0].length);
            CannonParallelMultiplication cpm =
                    new CannonParallelMultiplication(firstCopy, secondCopy, 100, threadsCount);

            long averageParallel = getAverageTimeOfExperiments(cpm, experimentsCount);

            System.out.println("Cannon parallel: matrix size - " + matrixSize + "x"
                    + matrixSize + "; time - " + averageParallel + " ms;");

            firstCopy =
                    MatrixUtil.copyBlock(first, 0, first.length, 0, first[0].length);
            secondCopy =
                    MatrixUtil.copyBlock(second, 0, second.length, 0, second[0].length);
            CannonSequentialMultiplication csm =
                    new CannonSequentialMultiplication(firstCopy, secondCopy, 100);

            long averageSequential = getAverageTimeOfExperiments(csm, experimentsCount);

            System.out.println("Cannon sequential: matrix size - " + matrixSize + "x"
                    + matrixSize + "; time - " + averageSequential + " ms;");
            System.out.println("Speed up: " + (1.0 * averageSequential / averageParallel));
            System.out.println();
        }
    }

    public static long getAverageTimeOfExperiments(MultiplicationAlgorithm algorithm,
                                                   int experimentsCount) {
        long experimentsTime = 0;
        for (int i = 0; i < experimentsCount; i++) {
            long experimentStart = System.currentTimeMillis();
            algorithm.multiply();
            long experimentFinish = System.currentTimeMillis();
            experimentsTime += experimentFinish - experimentStart;

            algorithm.resetResult();
        }
        return experimentsTime / experimentsCount;
    }
}