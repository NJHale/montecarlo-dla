package edu.njit.math450.walker;

import edu.njit.math450.matrix.AdjMatrix;

/**
 * Created by nhale on 11/5/2015.
 */
public class RadialWalker extends Walker {

    //Maximum radius away from the seed that the aggregate has reached
    protected int radius;

    //Extra space given for the walkers to move in
    protected int buffer;

    /**
     * Parameterized constructor creates a walker that walks from a radial
     * boundary held closely off the max radius of the aggregate
     * @param walkSeed Used to seed the pseudo-random walks across the space
     * @param radius Initial max radius of the aggregate
     * @param buffer Distance from the max radius to place the radial boundary
     */
    public RadialWalker(long walkSeed, int radius, int buffer) {
        super(walkSeed);
        this.radius = radius;
        this.buffer = buffer;
    }


    /**
     * Generates the next projected locale for the walker
     * to occupy. 'Steps' through the space.
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return next projected Locale based on a random seed
     */
    @Override
    protected Locale step(AdjMatrix space) {
        //decide axis of movement
        //0 -> vertical, 1 -> horizontal
        int axis = (int) (rand.nextDouble() * 2);
        int shift = (int) (rand.nextDouble() * 2);
        //instantiate the projected locale
        Locale proj = new Locale();
        //apply the step
        proj.i = locale.i + (int) Math.pow(-1, shift) * (1 - axis);
        proj.j = locale.j + (int) Math.pow(-1, shift) * axis;
        //System.out.println("Raw attempted : " + proj.i + " : " + proj.j);
        int center = (int) (space.size() / 2);
        //calculate and truncate distance from the seed (euclidean norm)
        int r = (int) Math.sqrt(Math.pow((center - proj.i), 2) //<- assume seed is centered in the space
                + Math.pow((center - proj.j), 2));
        //System.out.println("r: " + r);
        //check if the walker has crossed the radial boundary
        int bound = radius + buffer;
        if (r >= bound) {
            //reflect the projected Locale
            //corrected reflection radius taking into account the step
            int cor = -(r - Math.abs(r - bound));
            //System.out.println("cor: " + cor);
            //calculate the angle of reflection (in radians)
            double rads = Math.asin((proj.i - space.size()/2) / cor);
            proj.i = (int) (center + (cor * Math.sin(rads)));
            proj.j = (int) (center + (cor * Math.cos(rads)));
        }
        //System.out.println("Corrected attempted step: " + proj.i + " : " + proj.j);
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
    public void walk(AdjMatrix space) {
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
            //check sticking condition
            if (willStick(proj, space)) {
                //update the space
                space.set(proj.i, proj.j, space.get(proj.i, proj.j) + 1);
                //update the max radius of the aggregate if there is still room to expand
                if (radius + buffer < space.size())
                    radius += 1;
                //end walk
                walking = false;
            }
            //set the Walker's locale to projected
            locale = proj;
        }
        //System.out.println("Walk complete!");
    }

    /**
     *
     * @param proj represents the walker's projected locale
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return true if the walker will stick to the projected locale
     */
    @Override
    protected Boolean willStick(Locale proj, AdjMatrix space) {
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
        //create a random angle of inclination [0, 2*pi] (in radians)
        double rads = 2 * Math.PI * rand.nextDouble();
        //calculate the radial bound of the allowed walking area
        int bound = radius + buffer;
        //find the rectangular equivalent of the polar coordinate
        locale.i = (int) (n/2 + (bound * Math.sin(rads)));
        locale.j = (int) (n/2 + (bound * Math.cos(rads)));
    }
}
