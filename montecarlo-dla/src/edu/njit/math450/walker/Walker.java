package edu.njit.math450.walker;

import edu.njit.math450.matrix.*;

/**
 * Created by nhale on 11/5/2015.
 */
public abstract class Walker {

    protected class Locale {
        public int i, j;
    }

    protected Locale locale;

    protected long walkSeed;

    /**
     * Default constructor that instantiates
     * the initial locale to [0, 0]
     * and the walkSeed to 0
     */
    protected Walker() {
        locale = new Locale();
        locale.i = 0;
        locale.j = 0;
        walkSeed = 0;
    }

    /**
     * Parameterized constructor that sets
     * the starting locale in the adjacency matrix
     * @param n dimension of the n x n
     */
    protected Walker(int n, long walkSeed) {
        locale = new Locale();
        reOriginate(n);
        this.walkSeed = walkSeed;
    }

    /**
     * Generates the next projected locale for the walker
     * to occupy. 'Steps' through the space.
     * @param space
     * @return
     */
    protected abstract Locale step(AdjMatrix space);

    /**
     * Walks until the walker sticks to the aggregate and
     * reflects the change in the given space
     * @param space Square Adjacency matrix that represents the
     *              topography of the space being walked on
     */
    public abstract void walk(AdjMatrix space);

    /**
     * Determines whether the walker will stick
     * to the projected locale
     * @param projected represents the walker's projected locale
     * @param space Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return true if the walker is to stick in the projected locale,
     *  false otherwise
     */
    protected abstract Boolean stick(Locale projected, AdjMatrix space);

    /**
     * Sets the Walker's locale to some origin position
     * along the boundary of the n x n walking space
     * @param n size of the n x n walking space
     */
    public abstract void reOriginate(int n);

    /**
     * Sets the pseudo-random number generator's seed
     * to determine the walk direction at each step
     * @param walkSeed
     */
    public void setWalkSeed(long walkSeed) {
        this.walkSeed = walkSeed;
    }
}
