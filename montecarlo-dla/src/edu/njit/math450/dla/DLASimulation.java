package edu.njit.math450.dla;

import edu.njit.math450.matrix.Space;
import edu.njit.math450.util.DLAUtil;
import edu.njit.math450.walker.RadialWalker;
import edu.njit.math450.walker.Walker;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DLASimulation {

    protected Space space;

    protected Walker walker;

    protected Boolean complete;

    private JFrame frame;

    private boolean displayOn;

    /**
     * Parameterized constructor defines the default
     * behavior of the DLA - (single seed at center, all
     * walkers from infinity)
     * @param space n x n adjacency matrix that represents the
     *              space the DLA will form in
     * @param walker Implementation of Walker that will build
     *               the DLA
     */
    public DLASimulation(Space space, Walker walker, boolean displayOn) {
        int n = space.size();
        //place the seed (should be around (1/2)*(n, n)th entry)
        int seed = n / 2; // integer division
        int seedSize = 2;
        for (int i = seed - seedSize / 2; i <= seed + seedSize / 2; i++) {
            for (int j = seed - seedSize / 2; j <= seed + seedSize / 2; j++) {
                space.set(i, j, 1);
            }
        }
        //traverse the space adding zeros to non-seed area
        /*
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != seed && j != seed)
                    space.set(i, j, 0);
            }
        }*/
        // initialize the JFrame for displaying the DLA
        if (displayOn) frame = new JFrame("Running DLA");
        // set the space
        this.space = space;
        this.walker = walker;
        this.displayOn = displayOn;
        complete = false;
    }

    /**
     * Walks the walker walks times to generate the DLA
     * @param num_walks number of walks to complete before simulation completion
     */
    public Space simulate(int num_walks) {
        //walk num_walks times to generate the DLA

//        //Create the output file
//        File out = new File("run-info/dla-" + space.size() +
//                "-w" + walks + "-s" + walker.getWalkSeed() +
//                "-b" + walker.getBuffer() + ".txt");

        try {
            //PrintWriter writer = new PrintWriter(out, "UTF-8");
            int filter = 1000;
            System.out.println("Calculating fractal dimension every " + filter + " walks\n");

            // attempt to create image directory structure
            String dirPath = "images/DLA_";
            dirPath += (walker.getNonNewtFlag()) ? "nonNewt" : "newt";
            dirPath += "_Size" + space.size() + "_Seed" + walker.getWalkSeed() +
                "_A" + ((RadialWalker)walker).getA() + "_B" + ((RadialWalker)walker).getB();
            if (walker.getNonNewtFlag()) dirPath += "_Cnn" + ((RadialWalker)walker).getCnn() + "_α" + ((RadialWalker)walker).getAlpha();
            File dir = new File(dirPath);
            boolean success = dir.mkdir() || dir.isDirectory();

            System.out.println("Walk #, Fractal Dimension, Average Speed");

            for (int i = 0; i < num_walks; i++) {
                walker.walk(space);
                if (i % filter == 0) {
                    // calculate and print out the average speed over the boundary
                    calcAvgSpd(i);


                    double fDim = DLAUtil.fractalDim(space);
                    System.out.println(i + "," + fDim + "," + calcAvgSpd(i));
                    //writer.write("Walks : " + i + " fractal dimension : " + fDim + "\n");
                    //writer.flush();
                    BufferedImage dlaImg = space.rasterize(100);
                    //set the scaling factor
                    int scale = 1;
                    if(displayOn) {
                        //render the space on a JFrame
                        JPanel dlaPanel = new DLAPanel(dlaImg, scale);
                        frame.setSize(space.size() * scale, space.size() * scale);
                        frame.setContentPane(dlaPanel);
                        //make the frame's content visible
                        frame.setVisible(true);
                        //set the default close op
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    }
                    //attempt write the image to a file
                    try {
                        if (success) {
                            File out = new File(dirPath + "/Walk" + i + ".png");
                            ImageIO.write(dlaImg, "png", out);
                        }
                    } catch (Exception e) {
                        System.err.println("An error occurred while attempting to store the " +
                                "DLA in a png");
                    }
                }
            }
            // close the PrintWriter
            //writer.close();
            //set completion flag
            complete = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Something went wrong while attempting to write the run-info file");
        }
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

    /**
     * Calculates the average speed of the aggregate at the given (current) walk number
     * @param walkNum
     */
    public double calcAvgSpd(int walkNum) {
        // calculate the average speed over the whole boundary
        // this is resource intensive, don't do this in the final simulation
        double averageSpd=0;
        int numSpdMeasured=0;

        for(int i1=0;i1<space.size();i1++) {
            for(int j1=0;j1<space.size();j1++) {
                if(space.get(i1,j1)>0) {
                    double age = (walkNum + 3) - space.get(i1,j1); // implied cast to double
                    double[] vel = new double[2]; // stores velocity as components x,y
                    // check left and right
                    // if one is occupied and the other isn't
                    if(space.get(i1-1,j1)>0 && !(space.get(i1+1,j1)>0)) {
                        vel[0]=1/age;
                    } else if (space.get(i1+1,j1)>0 && !(space.get(i1-1,j1)>0)) {
                        vel[0]=-1/age;
                    }
                    // check top and bottom
                    if(space.get(i1,j1-1)>0 && !(space.get(i1,j1+1)>0)) {
                        vel[1]=1/age;
                    } else if (space.get(i1,j1+1)>0 && !(space.get(i1,j1-1)>0)) {
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
}
