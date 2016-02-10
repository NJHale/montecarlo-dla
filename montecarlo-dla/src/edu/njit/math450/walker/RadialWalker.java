package edu.njit.math450.walker;

import edu.njit.math450.matrix.AdjMatrix;

import java.util.Random;

/**
 * Created by nhale on 11/5/2015.
 */
public class RadialWalker extends Walker {

    //Maximum radius away from the seed that the aggregate has reached
    protected int radius;

    protected Random oriRand;

    protected int walkNum = 0;

    // sticking probabilities
    protected double A, B, C, L;

    protected Boolean nonNewtFlag;

    /**
     * Parameterized constructor creates a walker that walks from a radial
     * boundary held closely off the max radius of the aggregate
     * @param walkSeed Used to seed the pseudo-random walks across the space
     * @param radius Initial max radius of the aggregate
     * @param buffer Distance from the max radius to place the radial boundary
     */
    public RadialWalker(long oriSeed, long walkSeed, int radius, int buffer,
                        double A, double B, double C, double L, Boolean nonNewtFlag) {
        super(walkSeed);
        // initialize stick probs
        this.A = A;
        this.B = B;
        this.C = C;
        this.L = L;
        this.oriRand = new Random(oriSeed);
        this.radius = radius;
        this.buffer = buffer;
        this.nonNewtFlag = nonNewtFlag;
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
        //System.out.println("step...");
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
        int r = (int) distance(proj.j, proj.i, center, center);
        //System.out.println("r: " + r);
        //check if the walker has crossed the radial boundary
        int bound = radius + buffer;
        //System.out.println("bound: " + bound);
        //System.out.println("Raw attempted : " + proj.i + " : " + proj.j);
        if (r > bound || space.get(proj.i, proj.j) > 0) {
            //reset locale
            proj.i = locale.i;//set y
            proj.j = locale.j;//set x
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
        // increment the walk number
        walkNum++;
        System.out.println("WalkNum: " + walkNum);
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
                // update the space to reflect the walk number
                Locale oldProj = new Locale(proj);
                // settle until two consecutive projections are
                while (true) {
                    proj = settle(proj, space);
                    if (proj.i == oldProj.i && proj.j == oldProj.j) {
                        //System.out.println("We've found a consecutive settlement!");
                        break;
                    }
                    oldProj = new Locale(proj);
                }
                // check to see if we've formed a hole
                if (makesHole(proj, space)) {
                    reOriginate(space.size());
                    // don't stick
                    //System.out.println("Continued!!!");
                    continue;
                }
                //System.out.println("MADE IT PAST HOLE PREVENT!!!");

                space.set(proj.i, proj.j, walkNum);
                //calculate and truncate distance from the seed (euclidean norm)
                int r = (int) distance(proj.j, proj.i, space.size()/2, space.size()/2);
                //update the max radius of the aggregate if there is still room to expand
                int bound = radius + buffer;
                    if ((bound < ((space.size() / 2) - 1) &&
                            (r > radius))){
                    radius = r;
                }
                //end walk
                walking = false;
            }
            //set the Walker's locale to projected
            locale = proj;
        }
    }

    /**
     *
     * @param proj
     * @param space
     * @return
     */
    protected Locale settle(Locale proj, AdjMatrix space) {
        // instantiate the maximum neighbored open locale
        int maxNeig = 0;
        Locale settlement = new Locale();
        Locale prospect = new Locale();
        settlement.i = proj.i;
        settlement.j = proj.j;
        prospect.i = settlement.i;
        prospect.j = settlement.j;

        // number of ties
        int numTied = 1;

        // scan the block for neighbors starting at top left
        for (int i = (proj.i - 1); i <= proj.i + 1 && i < space.size(); i++) {
            for (int j = (proj.j - 1); j <= proj.j + 1 && j < space.size(); j++) {
                // check if we have found a unmarked locale within the space
                if (i >= 0 && j >= 0 && space.get(i, j) == 0) {
                    // we've found a possible settling location
                    prospect.i = i;
                    prospect.j = j;
                    // calculate the number of neighbors
                    int numNeig = numNeig(1, prospect, space);
                    if (numNeig > maxNeig) {
                        // clear the ties count
                        numTied = 1;
                        maxNeig = numNeig;
                        settlement.i = prospect.i;
                        settlement.j = prospect.j;
                    } else if (numNeig == maxNeig) {
                        // handle all ties so far
                        numTied++;
                        if (rand.nextInt(numTied) == 0) {
                            settlement.i = prospect.i;
                            settlement.j = prospect.j;
                        }
                    }

                }
            }
        }
        return settlement;
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

        if (up >= 0  && space.get(up, proj.j) > 0) return rand.nextDouble() < stickProb(proj, space);
        if (down < n && space.get(down, proj.j) > 0) return rand.nextDouble() < stickProb(proj, space);//rand.nextBoolean();
        if (left >= 0 && space.get(proj.i, left) > 0) return rand.nextDouble() < stickProb(proj, space);//rand.nextBoolean();
        if (right < n && space.get(proj.i, right) > 0) return rand.nextDouble() < stickProb(proj, space);//rand.nextBoolean();

        //otherwise the walker is still wandering in space
        return false;
    }

    /**
     *
     * @param space
     * @param proj
     * @return
     */
    private double stickProb(Locale proj, AdjMatrix space) {
        // calculate number of neigs in a 9x9
        int neig = 4;
        int numNeig = numNeig(neig, proj, space);

        double prob = A * (numNeig / L / L - (L - 1) / (2 * L)) + B;

        if (nonNewtFlag) {
            int maxWalkNum = 0;
            // scan the block for neighbors starting at top left
            for (int i = (proj.i - 1); i <= proj.i + 1 && i < space.size(); i++) {
                for (int j = (proj.j - 1); j <= proj.j + 1 && j < space.size(); j++) {
                    // check if we have found a marked locale within the space
                    if (i >= 0 && j >= 0 && space.get(i, j) > 0) {
                        // get the max walk num
                        if (space.get(i, j) > maxWalkNum) {
                            maxWalkNum = space.get(i, j);
                        }
                    }
                }
            }
            prob *= Math.pow(walkNum, .5) / (walkNum - maxWalkNum);
        }
        // catch negative probs?
        if (prob < C) {
            prob = C;
        }
        return prob;
    }

    /**
     * Randomly place the walker on one of the boundary
     * edges of the n x n walking space
     * @param n size of the n x n walking space
     */
    @Override
    public void reOriginate(int n) {
        //create a random angle of inclination [0, 2*pi] (in radians)
        double rads = 2 * Math.PI * oriRand.nextDouble();
        //double angle = (360 * rand.nextDouble()) * Math.PI / 180;
        //System.out.println(rads);
        //calculate the radial bound of the allowed walking area
        int bound = radius + buffer;
        //find the rectangular equivalent of the polar coordinate
        locale.i = (int) (n/2 + (bound * Math.sin(rads)));
        locale.j = (int) (n/2 + (bound * Math.cos(rads)));
        //System.out.println("reOriginate i: " + locale.i + " j: " + locale.j + " bound: " + bound);
    }

}
