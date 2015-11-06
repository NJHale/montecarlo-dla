package edu.njit.math450.walker;

import edu.njit.math450.matrix.AdjMatrix;

/**
 * Created by nhale on 11/5/2015.
 */
public class DumbWalker extends Walker {

    private long stickSeed;



    /**
     * Generates the next projected locale for the walker
     * to occupy. 'Steps' through the space.
     * @param space
     * @return next projected Locale based on a random seed
     */
    @Override
    protected Locale step(AdjMatrix space) {
        return null;
    }

    /**
     *
     * @param space Adjacency matrix that represents the
     *              topography of the space being walked on
     */
    @Override
    public void walk(AdjMatrix space) {

    }

    /**
     *
     * @param projected represents the walker's projected locale
     * @param space Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return true if the
     */
    @Override
    protected Boolean stick(Locale projected, AdjMatrix space) {
        return null;
    }

    @Override
    public void reOriginate(int n) {

    }
}
