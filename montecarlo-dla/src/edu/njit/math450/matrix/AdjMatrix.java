package edu.njit.math450.matrix;

/**
 * Created by nhale on 11/5/2015.
 */
public abstract class AdjMatrix {
    /**
     * Gets the value of the AdjMatrix at (i, j)
     * @param i row
     * @param j column
     * @return the value of the AdjMatrix at (i, j)
     */
    public abstract int get(int i, int j);

    /**
     * Sets the value of the AdjMatrix at (i, j)
     * @param i row
     * @param j column
     * @param v value to set as the (i, j)th entry
     */
    public abstract void set(int i, int j, int v);


}
