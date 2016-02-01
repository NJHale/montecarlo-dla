package edu.njit.math450.dla;

import edu.njit.math450.matrix.AdjMatrix;
import edu.njit.math450.util.DLAUtil;
import edu.njit.math450.walker.Walker;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;

/**
 * Created by nhale on 11/5/2015.
 */
public class DLASimulation {

    protected AdjMatrix space;

    protected Walker walker;

    protected Boolean complete;

    private JFrame frame;

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
        // initialize the JFrame for displaying the DLA
        frame = new JFrame("Running DLA");
        // set the space
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

//        File out = new File("run-info/dla-" + space.size() +
//                "-w" + walks + "-s" + walker.getWalkSeed() +
//                "-b" + walker.getBuffer() + ".txt");

        try {
            //PrintWriter writer = new PrintWriter(out, "UTF-8");
            int filter = 1000;
            System.out.println("Calculating fractal dimension every " + filter + " walks\n");
            for (int i = 0; i < walks; i++) {
                walker.walk(space);
                if (i % filter == 0) {
                    //double fDim = DLAUtil.fractalDim(space);
                    //System.out.println("Walks : " + i + " fractal dimension : " + fDim);
                    //writer.write("Walks : " + i + " fractal dimension : " + fDim + "\n");
                    //writer.flush();
                    BufferedImage dlaImg = space.rasterize(100);
                    //set the scaling factor
                    int scale = 1;
                    //render the space on a JFrame
                    JPanel dlaPanel = new DLAPanel(dlaImg, scale);
                    frame.setSize(space.size()*scale, space.size()*scale);
                    frame.setContentPane(dlaPanel);
                    //make the frame's content visible
                    frame.setVisible(true);
                    //set the default close op
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

}
