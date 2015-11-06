package edu.njit.math450.walker;

import java.util.Random;

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

    /**
     * Randomly place the walker on one of the boundary
     * edges of the n x n walking space
     * @param n size of the n x n walking space
     */
    @Override
    public void reOriginate(int n) {
        //pick an edge of the rectangular n x n space
        Random rand = new Random(walkSeed);
        //o -> top, 1 -> bottom, 2 -> left, 3 -> right
        int edge = (int) Math.floor(rand.nextDouble() * 4);
        //free index
        int free = (int) Math.floor(rand.nextDouble() * n);
        //state machine to re-originate the walker
        switch (edge) {
            //top
            case 0:
                locale.i = 0;
                locale.j = free;
            //bottom
            case 1:
                locale.i = n - 1;
                locale.j = free;
            //left
            case 2:
                locale.i = free;
                locale.j = 0;
            //right
            case 3:
                locale.i = free;
                locale.j = n - 1;
        }

    }
}
