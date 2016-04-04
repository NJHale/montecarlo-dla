package edu.njit.math450.walker;

import java.util.Random;

import edu.njit.math450.matrix.*;

/**
 * Created by nhale on 11/5/2015.
 */
public abstract class Walker {

    protected Boolean nonNewtFlag;

    protected int buffer;

    protected Random rand;

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
     * @param walkSeed walkSeed of the random walk
     */
    protected Walker(long walkSeed) {
        locale = new Locale();
        this.walkSeed = walkSeed;
        rand = new Random(walkSeed);
    }

    /**
     * Generates the next projected locale for the walker
     * to occupy. 'Steps' through the space.
     * @param space Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return the next projected locale for the walker
     *              to occupy.
     */
    protected abstract Locale step(Space space);

    /**
     * Walks until the walker sticks to the aggregate and
     * reflects the change in the given space
     * @param space Square Adjacency matrix that represents the
     *              topography of the space being walked on
     */
    public abstract void walk(Space space);

    /**
     * Determines whether the walker will stick
     * to the projected locale
     * @param projected represents the walker's projected locale
     * @param space Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return true if the walker is to stick in the projected locale,
     *  false otherwise
     */
    protected abstract Boolean willStick(Locale projected, Space space);

    /**
     * Sets the Walker's locale to some origin position
     * along the boundary of the n x n walking space
     * @param n size of the n x n walking space
     */
    public abstract void reOriginate(int n);

    /**
     * Sets the pseudo-random number generator's seed
     * to determine the walk direction at each step
     * @param walkSeed walkSeed of the random walk
     */
    public void setWalkSeed(long walkSeed) {
        this.walkSeed = walkSeed;
    }

    /**
     *
     * @return walkSeed of the random walk
     */
    public long getWalkSeed() {
        return walkSeed;
    }

    /**
     *
     * @return
     */
    public long getBuffer() {
        return buffer;
    }

    public boolean getNonNewtFlag() {
        return nonNewtFlag;
    }

}
