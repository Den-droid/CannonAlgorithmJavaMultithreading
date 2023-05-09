package org.cannonalgorithm.utils;

import java.util.Random;

public class MatrixUtil {
    public static int[][] generateMatrix(int row, int column, int min, int max) {
        Random random = new Random();
        int[][] matrix = new int[row][column];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = random.nextInt(max - min + 1) + min;
            }
        }
        return matrix;
    }

    public static void shiftLeft(int[][] matrix, int i, int step) {
        int[] tmpArr = new int[step];

        System.arraycopy(matrix[i], 0, tmpArr, 0, step);
        for (int j = step; j < matrix[i].length; j++) {
            matrix[i][j - step] = matrix[i][j];
        }
        System.arraycopy(tmpArr, 0, matrix[i], matrix[i].length - step, step);
    }

    public static void shiftUp(int[][] matrix, int j, int step) {
        int[] tmpArr = new int[step];

        for (int i = 0; i < step; i++) {
            tmpArr[i] = matrix[i][j];
        }

        for (int i = step; i < matrix.length; i++) {
            matrix[i - step][j] = matrix[i][j];
        }

        for (int i = 0; i < step; i++) {
            matrix[i + matrix.length - step][j] = tmpArr[i];
        }
    }

    public static void showMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int elem : row) {
                System.out.print(elem + " ");
            }
            System.out.println();
        }
    }

    public static int[][] multiplyBlocks(int[][] blockFirst, int[][] blockSecond) {
        int[][] resBlock = new int[blockFirst.length][blockSecond[0].length];
        for (int i = 0; i < blockFirst.length; i++) {
            for (int j = 0; j < blockSecond[0].length; j++) {
                int sum = 0;
                for (int k = 0; k < blockSecond.length; k++) {
                    sum += blockFirst[i][k] * blockSecond[k][j];
                }
                resBlock[i][j] = sum;
            }
        }
        return resBlock;
    }

    public static int[][] copyBlock(int[][] src, int rowStart, int rowFinish,
                                    int colStart, int colFinish) {
        int[][] copy = new int[rowFinish - rowStart][colFinish - colStart];
        for (int i = 0; i < rowFinish - rowStart; i++) {
            System.arraycopy(src[i + rowStart], colStart, copy[i], 0, colFinish - colStart);
        }
        return copy;
    }

    public static boolean areEqual(int[][] first, int[][] second) {
        if (first.length != second.length || first[0].length != second[0].length)
            return false;
        for (int i = 0; i < first.length; i++) {
            for (int j = 0; j < first[0].length; j++) {
                if (first[i][j] != second[i][j])
                    return false;
            }
        }
        return true;
    }

}
