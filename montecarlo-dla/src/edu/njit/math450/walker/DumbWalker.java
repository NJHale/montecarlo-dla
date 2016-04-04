package edu.njit.math450.walker;

import edu.njit.math450.matrix.Locale;
import edu.njit.math450.matrix.Space;

/**
 * Created by nhale on 11/5/2015.
 */
public class DumbWalker extends Walker {

    //seed for the random chance to stick
    private long stickSeed;

    /**
     * Parameterized constructor
     *
     * @param walkSeed seed used for randomness in walking
     * @param stickSeed seed used for randomness in sticking
     */
    public DumbWalker(long walkSeed, long stickSeed) {
        super(walkSeed);
        this.stickSeed = stickSeed;
    }


    /**
     * Generates the next projected locale for the walker
     * to occupy. 'Steps' through the space.
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return next projected Locale based on a random seed
     */
    @Override
    protected Locale step(Space space) {
        //decide axis of movement
        //0 -> vertical, 1 -> horizontal
        int axis = (int) (rand.nextDouble() * 2);
        int shift = (int) (rand.nextDouble() * 2);
        //instantiate the projected locale
        Locale proj = new Locale();
        //apply the step
        proj.i = locale.i + (int) Math.pow(-1, shift) * (1 - axis);
        proj.j = locale.j + (int) Math.pow(-1, shift) * axis;
        //wrap the indices if they've crossed the boundary
        int n = space.size();
        if (proj.i < 0)//top boundary
            proj.i = proj.i + n;
        else if (proj.i >= n)//bottom boundary
            proj.i = n - proj.i;
        if (proj.j < 0)//left boundary
            proj.j = proj.j + n;
        else if (proj.j >= n)//right boundary
            proj.j = n - proj.j;
        //return the projected Locale
        return proj;
    }

    /**
     * Steps the Walker through the given space until a
     * sticking condition occurs and updates the space with the result
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     */
    @Override
    public void walk(Space space) {
        //relocate the walker to a boundary
        reOriginate(space.size());
        //declare the projected Locale
        Locale proj = null;
        //set the walking flag
        Boolean walking = true;
        //step over the space until a sticking condition is met
        while (walking) {
            //get the next projected step, assuming the
            proj = step(space);
            //System.out.println("projected locale row: " + proj.i + " col: " + proj.j);
            //check sticking condition
            if (willStick(proj, space)) {
                //update the space
                space.set(proj.i, proj.j, space.get(proj.i, proj.j) + 1);
                //end walk
                walking = false;
            }
            //set the Walker's locale to projected
            locale = proj;
        }
    }

    /**
     *
     * @param proj represents the walker's projected locale
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return true if the walker will stick to the projected locale
     */
    @Override
    protected Boolean willStick(Locale proj, Space space) {
        //check immediate area around the projected locale
        int up = proj.i - 1;
        int down = proj.i + 1;
        int left = proj.j - 1;
        int right = proj.j + 1;
        int n = space.size();
        //stick to the aggregate
        if (up >= 0  && space.get(up, proj.j) > 0)
            return rand.nextBoolean();
        if (down < n && space.get(down, proj.j) > 0)
            return rand.nextBoolean();
        if (left >= 0 && space.get(proj.i, left) > 0)
            return rand.nextBoolean();
        if (right < n && space.get(proj.i, right) > 0)
            return rand.nextBoolean();
        //otherwise the walker is still wandering in space
        return false;
    }

    /**
     * Randomly place the walker on one of the boundary
     * edges of the n x n walking space
     * @param n size of the n x n walking space
     */
    @Override
    public void reOriginate(int n) {
        //pick an edge of the rectangular n x n space
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
