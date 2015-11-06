package edu.njit.math450.matrix;

/**
 * Created by nhale on 11/5/2015.
 */
public class ArrayAdjMatrix extends AdjMatrix {

    private int[][] matrix;

    /**
     * Parameterized constructor instantiates
     * the m x n Adjacency Matrix as an array of arrays filled with zeros.
     * @param m
     * @param n
     */
    public ArrayAdjMatrix(int m, int n) {
        matrix = new int[m][n];
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    @Override
    public int get(int i, int j) {
        return matrix[i][j];
    }

    @Override
    public void set(int i, int j, int v) {
        matrix[i][j] = v;
    }
}
