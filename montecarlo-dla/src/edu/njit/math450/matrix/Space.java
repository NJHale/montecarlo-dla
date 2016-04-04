package edu.njit.math450.matrix;

import java.awt.image.BufferedImage;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class Space {

    /**
     * Gets the value of the Space at (i, j)
     * @param i row
     * @param j column
     * @return the value of the Space at (i, j)
     */
    public abstract int get(int i, int j);

    /**
     * Sets the value of the Space at (i, j)
     * @param i row
     * @param j column
     * @param v value to set as the (i, j)th entry
     */
    public abstract void set(int i, int j, int v);

    /**
     * Returns the size n of the n x n adjacency matrix
     * @return size n of the n x n adjacency matrix
     */
    public abstract int size();

    /**
     * Rasterizes the Space into a
     * BufferedImage, pixel by pixel and
     * returns the resulting image
     * @param hue integer value of hue to rasterize matrix with
     * @return Rasterization of the Space
     */
    public BufferedImage rasterize(int hue) {
        BufferedImage img = new BufferedImage(size(), size(), BufferedImage.TYPE_INT_RGB);
        //set the color of the (i, j)th pixel
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                //calculate distance from center
                int r = (int) Math.sqrt(Math.pow((size()/2 - i), 2) +
                        Math.pow((size()/2 - j), 2));
                //calculate color palette based on value at (i, j)
                int palette = (get(i, j) > 0) ? get(i, j) : 0xffffff;//get(i, j);//r * get(i, j);//(int) ((Math.exp(r) + hue) % 16777215) * get(i, j);
                img.setRGB(i, j, palette);
            }
        }
        return img;
    }

    /**
     * Calculates the fractal dimension of Space A
     * @return The fractal dimension of Space A
     */
    public double fractalDim() {
        //int boxes = (int) (Math.log(A.size()) / Math.log(2));

        List<Double> cnts = new ArrayList<Double>();
        List<Double> x = new ArrayList<Double>();
        int start = 1;//(int) Math.pow(2, Math.round(Math.log(A.size()) / Math.log(2)) - 5);
        for (int i = start; i <= this.size(); i *= 2) {
            // System.out.println("boxNumber: " + i);
            x.add(Math.log(i));
            cnts.add(Math.log(boxCount(i)));
        }
        //calculate the slope of the linear regression
        double dim = linReg(x, cnts);
        return  dim;//default return
    }

    /**
     * Counts the number of boxes in the given adjacency matrix
     * that contain values > 0
     * @param numBoxes Number of boxes to count
     * @return Number of boxes that contain values > 0
     */
    private int boxCount(int numBoxes) {
        int n = this.size();//get the size of n x n Space A
        //instantiate the box count
        int count = 0;
        //calculate the scaled size of each box
        int boxSize = (n / numBoxes);
        for (int ibox = 0; ibox < numBoxes; ibox++) {//traverse each box
            int si = ibox * boxSize;//get the starting i
            for(int jbox = 0; jbox < numBoxes; jbox++) {
                int sj = jbox * boxSize;//get the starting j
                for (int i = si; i < si + boxSize; i++) {
                    for (int j = sj; j < sj + boxSize; j++) {
                        if (this.get(i, j) > 0) {//check to see if we've found a hit
                            //add the hit
                            count++;
                            //move to the next box
                            i = si + boxSize;
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Calculates the slope of the linear regression resulting
     * from the given x, y sets
     * @param x
     * @param y
     * @return
     * @throws InvalidParameterException if
     */
    public static double linReg(List<Double> x, List<Double> y) {
        //check if x and y are the same length
        if (x.size() != y.size())
            throw new InvalidParameterException("Sets x and y must be the same length!");
        double xSum = 0;
        double ySum = 0;
        double xySum = 0;
        double x2Sum = 0;
        double y2Sum = 0;
        //get the length of the x and y sets
        int n = x.size();

        for (int i = 0; i < n; i++) {
            xSum += x.get(i);
            ySum += y.get(i);
            xySum += x.get(i) * y.get(i);
            x2Sum += x.get(i) * x.get(i);
            y2Sum += y.get(i) * y.get(i);
        }

        //calculate the slope of the linear regression
        double m = ((n * xySum) - (xSum * ySum)) / ((n * x2Sum) - (xSum * xSum));

        return m;
    }

    /**
     * Calculates the average speed of the aggregate at the given (current) walk number
     * @param walkNum
     */
    public double calcAvgSpd(int walkNum) {
        // calculate the average speed over the whole boundary
        // this is resource intensive, don't do this in the final simulation
        double averageSpd=0;
        int numSpdMeasured=0;

        for(int i1=0;i1<this.size();i1++) {
            for(int j1=0;j1<this.size();j1++) {
                if(this.get(i1,j1)>0) {
                    double age = (walkNum + 3) - this.get(i1,j1); // implied cast to double
                    double[] vel = new double[2]; // stores velocity as components x,y
                    // check left and right
                    // if one is occupied and the other isn't
                    if(this.get(i1-1,j1)>0 && !(this.get(i1+1,j1)>0)) {
                        vel[0]=1/age;
                    } else if (this.get(i1+1,j1)>0 && !(this.get(i1-1,j1)>0)) {
                        vel[0]=-1/age;
                    }
                    // check top and bottom
                    if(this.get(i1,j1-1)>0 && !(this.get(i1,j1+1)>0)) {
                        vel[1]=1/age;
                    } else if (this.get(i1,j1+1)>0 && !(this.get(i1,j1-1)>0)) {
                        vel[1]=-1/age;
                    }

                    if(vel[0]!=0 || vel[1]!=0) {
                        averageSpd = (averageSpd*numSpdMeasured + Math.pow(vel[0]*vel[0]+vel[1]*vel[1],0.5)) / (++numSpdMeasured);
                    }
                }
            }
        }
        return averageSpd;
        //System.out.printf("%.9f \t %d  \t %d\n",averageSpd,numSpdMeasured,walkNum);
    }


    public static int[] deltaRow = {0, -1, -1, -1, 0, 1, 1, 1};
    public static int[] deltaCol = {1, 1, 0, -1, -1, -1, 0, 1};

    /**
     * Checks if the given projection will form a hole on walker stick
     * @param proj projected locale to check
     * @return true if a walker at proj will form a hole; false otherwise
     */
    public Boolean makesHole(Locale proj) {
        // get the current state
        int curState = this.get(proj.i + 1, proj.j + 1);
        // initialize flip count
        int flips = 0;
        // look over each array (ALWAYS size 8)
        for (int i = 0; i < 8; i++) {
            if ((curState > 0) != (this.get(proj.i + deltaRow[i], proj.j + deltaCol[i]) > 0)) {
                flips++;
                curState = this.get(proj.i + deltaRow[i], proj.j + deltaCol[i]);
            }
        }
        // check to see if we've flipped 4 times which indicates a forming hole
        return flips >= 4;

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
    public static double distance(int i, int j, int h, int k) {
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
    public int numNeig(int neig, Locale proj) {
        int neighbors = 0;
        // scan the block for neighbors starting at top left
        for (int i = (proj.i - neig); i <= proj.i + neig && i < this.size(); i++) {
            for (int j = (proj.j - neig); j <= proj.j + neig && j < this.size(); j++) {
                // check if we have found a marked locale within the space
                if (i >= 0 && j >= 0 && this.get(i, j) > 0) {
                    // if we have we can count as a neighbor
                    neighbors++;
                }
            }
        }
        // return the number of neighbors
        return neighbors;
    }
}
