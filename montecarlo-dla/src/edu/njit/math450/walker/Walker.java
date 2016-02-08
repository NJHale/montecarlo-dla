package edu.njit.math450.walker;

import java.util.Random;

import edu.njit.math450.matrix.*;

/**
 * Created by nhale on 11/5/2015.
 */
public abstract class Walker {

    protected static int[] deltaRow = {0, -1, -1, -1, 0, 1, 1, 1};
    protected static int[] deltaCol = {1, 1, 0, -1, -1, -1, 0, 1};

    protected class Locale {
        public int i, j;

        public Locale() {}

        public Locale(Locale locale) {
            i = locale.i;
            j = locale.j;
        }
    }

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
    protected abstract Boolean willStick(Locale projected, AdjMatrix space);

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

    /**
     * Calculates and returns the Euclidean norm of the
     * difference vector between two points (i, j) and (h, k)
     * @param i x coord of (i, j)
     * @param j y coord of (i, j)
     * @param h x coord of (h, k)
     * @param k y coord of (h, k)
     *
     * @return Euclidean norm of distance from (i, i) to (h, k)
     */
    protected double distance(int i, int j, int h, int k) {
        //calculate the distance
        double dist = Math.sqrt(Math.pow((i - h), 2) //<- assume seed is centered in the space
                + Math.pow((j - k), 2));
        return dist;
    }

    /**
     * Returns the number of neighbors in an neigh + 1 x neigh + 1
     * square centered at the given projected locale
     * @param neig Dimensions - 1 of the square neighborhood to check
     * @param proj Projected location (assumed empty)
     * @return Number of neighbors in the neighborhood of the projected locale
     */
    protected int numNeig(int neig, Locale proj, AdjMatrix space) {
        int neighbors = 0;
        // scan the block for neighbors starting at top left
        for (int i = (proj.i - neig); i <= proj.i + neig && i < space.size(); i++) {
            for (int j = (proj.j - neig); j <= proj.j + neig && j < space.size(); j++) {
                // check if we have found a marked locale within the space
                if (i >= 0 && j >= 0 && space.get(i, j) > 0) {
                    // if we have we can count as a neigbor
                    neighbors++;
                }
            }
        }
        // return the number of neighbors
        return neighbors;
    }

    /**
     * Checks if the given projection will form a hole on walker stick
     * @param proj projected locale to check
     * @param space adjacency matrix of the space
     * @return true if a walker at proj will form a hole; false otherwise
     */
    protected Boolean makesHole(Locale proj, AdjMatrix space) {
        // get the current state
        int curState = space.get(proj.i + 1, proj.j + 1);
        // initialize flip count
        int flips = 0;
        // look over each array (ALWAYS size 8)
        for (int i = 0; i < 8; i++) {
            if (curState != space.get(proj.i + deltaRow[i], proj.j + deltaCol[i])) {
                flips++;
                curState = space.get(proj.i + deltaRow[i], proj.j + deltaCol[i]);
            }
        }
        // check to see if we've flipped 4 times which indicates a forming hole
        return flips >= 4;

    }

    /**
     *
     * @return
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

}
