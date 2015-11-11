package edu.njit.math450.dla;

import edu.njit.math450.matrix.AdjMatrix;
import edu.njit.math450.walker.Walker;

/**
 * Created by nhale on 11/5/2015.
 */
public class DLASimulation {

    protected AdjMatrix space;

    protected Walker walker;

    protected Boolean complete;

    /**
     * Parameterized constructor defines the default
     * behavior of the DLA - (single seed at center, all
     * walkers from infinity)
     * @param space n x n adjacency matrix that represents the
     *              space the DLA will form in
     * @param walker Implementation of Walker that will build
     *               the DLA
     */
    public DLASimulation(AdjMatrix space, Walker walker) {
        int n = space.size();
        //place the seed (should be around (1/2)*(n, n)th entry)
        int seed = (int) n / 2;
        space.set(seed, seed, 1);
        //traverse the space adding zeros to non-seed area
        /*
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != seed && j != seed)
                    space.set(i, j, 0);
            }
        }*/
        //set the space
        this.space = space;
        this.walker = walker;
        complete = false;
    }

    /**
     * Walks the walker walks times to generate the DLA
     * @param walks number of walks to complete before simulation completion
     */
    public AdjMatrix simulate(int walks) {
        //walk walks times to generate the DLA
        for (int i = 0; i < walks; i++) {
            System.out.println("walk: " + i);
            walker.walk(space);
        }
        //set completion flag
        complete = true;
        //return the space
        return space;
    }

    /**
     * Returns the status of the simulation
     * @return true if complete, false if incomplete
     */
    public Boolean isComplete() {
        return complete;
    }

}
