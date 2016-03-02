package edu.njit.math450.walker;

import edu.njit.math450.matrix.AdjMatrix;

import java.util.Random;

public class RadialWalker extends Walker {

    //Maximum radius away from the seed that the aggregate has reached
    protected int radius;

    protected Random oriRand;

    // initialize to 1 to distinguish from seed
    protected int walkNum = 1;

    // sticking probabilities
    protected double A, B, C;
    protected int L;

    // non-newtonian sticking probabilities
    protected double Cnn, alpha;

    /**
     * Parameterized constructor creates a walker that walks from a radial
     * boundary held closely off the max radius of the aggregate
     * @param walkSeed Used to seed the pseudo-random walks across the space
     * @param radius Initial max radius of the aggregate
     * @param buffer Distance from the max radius to place the radial boundary
     */
    public RadialWalker(long oriSeed, long walkSeed, int radius, int buffer,
                        double A, double B, double C, int L) {
        super(walkSeed);
        // initialize parameters
        this.A = A;
        this.B = B;
        this.C = C;
        this.L = L;
        this.oriRand = new Random(oriSeed);
        this.radius = radius;
        this.buffer = buffer;
        this.nonNewtFlag = false;
    }

    /**
     * Overloaded Constructor for non-newtonian Radial Walker!
     * @param oriSeed Random seed for reoriginating the walker
     * @param walkSeed Random seed for choosing walk direction
     * @param radius Initial radius to originate on
     * @param buffer Size of buffer for increasing radius
     * @param A Sticking probability parameter
     * @param B Sticking probability parameter
     * @param C Minimum probability of sticking
     * @param L Size of box for curvature calculation
     * @param Cnn Constant coefficient of velocity in sticking probability calculation
     * @param alpha Power of velocity in sticking probability calculation
     */
    public RadialWalker(long oriSeed, long walkSeed, int radius, int buffer,
                        double A, double B, double C, int L, double Cnn, double alpha) {
        super(walkSeed);
        // initialize parameters
        this.A = A;
        this.B = B;
        this.C = C;
        this.L = L;
        this.oriRand = new Random(oriSeed);
        this.radius = radius;
        this.buffer = buffer;
        this.nonNewtFlag = true;
        this.Cnn = Cnn;
        this.alpha = alpha;
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
        int center = space.size() / 2; // integer division
        //calculate and truncate distance from the seed (euclidean norm)
        int r = (int) distance(proj.j, proj.i, center, center);
        //check if the walker has crossed the radial boundary
        int bound = radius + buffer;
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
        //System.out.println("WalkNum: " + walkNum);
        //relocate the walker to a boundary
        reOriginate(space.size());
        //declare the projected Locale
        Locale proj;
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
                        break;
                    }
                    oldProj = new Locale(proj);
                }
                // check to see if we've formed a hole
                if (makesHole(proj, space)) {
                    reOriginate(space.size());
                    // don't stick
                    continue;
                }

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
     * @param proj represents the walker's projected locale
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return the locale that the walker should settle into
     */
    protected Locale settle(Locale proj, AdjMatrix space) {
        // instantiate the maximum neighbored open locale
        int maxNeig = 0;
        Locale settlement = new Locale(proj.i,proj.j);
        Locale prospect = new Locale(settlement.i,settlement.j);

        // number of ties
        int numTied = 1;

        // scan the block for neighbors starting at top left
        for (int i = (proj.i - 1); i <= proj.i + 1 && i < space.size(); i++) {
            for (int j = (proj.j - 1); j <= proj.j + 1 && j < space.size(); j++) {
                // check if we have found a unmarked locale within the space
                if (i >= 0 && j >= 0 && space.get(i, j) == 0) {
                    // we've found a possible settling location
                    prospect.setCoords(i,j);
                    // calculate the number of neighbors
                    int numNeig = numNeig(1, prospect, space);
                    if (numNeig > maxNeig) {
                        // clear the ties count
                        numTied = 1;
                        maxNeig = numNeig;
                        settlement.setCoords(prospect.i,prospect.j);
                    } else if (numNeig == maxNeig) {
                        // handle all ties so far
                        numTied++;
                        if (rand.nextInt(numTied) == 0) {
                            settlement.setCoords(prospect.i,prospect.j);
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
     * @param proj represents the walker's projected locale
     * @param space n x n Adjacency matrix that represents the
     *              topography of the space being walked on
     * @return the probability of sticking at the projected locale
     */
    private double stickProb(Locale proj, AdjMatrix space) {
        // calculate number of neigs in a 9x9
        int neig = L/2;
        int numNeig = numNeig(neig, proj, space);

        double prob = A * ((double)numNeig / L / L - (double)(L - 1) / (2 * L)) + B;

        if (nonNewtFlag) {
            double nonNewtCorrection=0;
//            int maxWalkNum = 0;
//            // scan the block for neighbors starting at top left
//            for (int i = (proj.i - 1); i <= proj.i + 1 && i < space.size(); i++) {
//                for (int j = (proj.j - 1); j <= proj.j + 1 && j < space.size(); j++) {
//                    // check if we have found a marked locale within the space
//                    if (i >= 0 && j >= 0 && space.get(i, j) > 0) {
//                        // get the max walk num
//                        if (space.get(i, j) > maxWalkNum) {
//                            maxWalkNum = space.get(i, j);
//                        }
//                    }
//                }
//            }
//            prob *= Math.pow(walkNum, .5) / (walkNum - maxWalkNum);

            double[] vel = new double[2]; // stores velocity as components x,y
            // check that walker has one on left, but not on right(or vice versa)
            if(proj.i-1>0 && proj.i+1<(space.size()) &&
                    ((space.get(proj.i-1,proj.j)>0)!=(space.get(proj.i+1,proj.j)>0))) {
                // find stuck walker on the left
                if (space.get(proj.i-1, proj.j) > 0) {
                    vel[0] += 1 / (double) (walkNum-space.get(proj.i - 1, proj.j));
                }
                // find stuck walker on the right
                if (space.get(proj.i+1, proj.j) > 0) {
                    vel[0] -= 1 / (double) (walkNum-space.get(proj.i + 1, proj.j));
                }
            }
            // check that walker has one on top, but not on bottom(or vice versa)
            if(proj.j-1>0 && proj.j+1<space.size()-1 &&
                    ((space.get(proj.i,proj.j-1)>0)!=(space.get(proj.i,proj.j+1)>0))) {
                // find stuck walker on the top
                if (space.get(proj.i, proj.j + 1) > 0) {
                    vel[1] -= 1 / (double) (walkNum-space.get(proj.i, proj.j + 1));
                }
                // walker stuck to the left
                if (space.get(proj.i, proj.j - 1) > 0) {
                    vel[1] += 1 / (double) (walkNum-space.get(proj.i, proj.j - 1));
                }
            }
            // apply nonNewtCorrection formula
            nonNewtCorrection=getNonNewtCorrection(vel);
            //System.out.println("Non Newtonian Correction: "+nonNewtCorrection);
            //System.out.println("Newtonian Stick Prob: " + prob);
            prob+=nonNewtCorrection;
        }
        // catch negative probs?
        if (prob < C) {
            prob = C;
        }
        return prob;
    }

    /**
     *
     * @param vel The velocity at the point
     * @return the non Newtonian probability adjustment
     */
    public double getNonNewtCorrection(double[] vel) {
        // take the squared velocity to half the power
        return this.Cnn*Math.pow(vel[0]*vel[0]+vel[1]*vel[1],this.alpha/2.0);
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

    public double getA() {
        return A;
    }

    public double getB() {
        return B;
    }


}
