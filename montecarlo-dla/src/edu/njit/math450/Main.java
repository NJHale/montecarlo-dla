package edu.njit.math450;

import edu.njit.math450.dla.DLAPanel;
import edu.njit.math450.dla.DLASimulation;
import edu.njit.math450.matrix.*;
import edu.njit.math450.walker.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Simulation!");
        //hard code initial values for now
        long walkSeed = 1209387;
        long stickSeed = 1209388;
        int n = 1000;
        int walks = 100000;
	    //Attempt generic DumbWalker simulation
        Walker walker = new DumbWalker(walkSeed, stickSeed);
        //instantiate the walk space
        AdjMatrix space = new ArrayAdjMatrix(n);
        //create the simulation
        DLASimulation dla = new DLASimulation(space, walker);
        long start = System.currentTimeMillis();
        space = dla.simulate(walks);
        System.out.println(walks + " random walks completed in " +
                (System.currentTimeMillis() - start) + " ms");
        //rasterize the dla and store the image
        int hue = 100;
        BufferedImage dlaImg = space.rasterize(hue);
        //attempt write the image to a file
        try {
            File out = new File("images/dla-" + n +
                    "-" + walks + "-" + walkSeed + ".bmp");
            ImageIO.write(dlaImg, "bmp", out);
        } catch (Exception e) {
            System.err.println("An error occurred while attempting to store the " +
                    "DLA in a bitmap");
        }

        JFrame frame = new JFrame("DumbWalker DLA");
        //set the scaling factor
        int scale = 1;
        //render the space as a bitmap on a JFrame
        JPanel dlaPanel = new DLAPanel(dlaImg, scale);
        frame.setSize(n*scale, n*scale);
        frame.setContentPane(dlaPanel);
        //make the frame's content visible
        frame.setVisible(true);
        //set the default close op
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * Returns value a as is
     * @param a value to return
     * @return the same input param you passed
     */
    public int returnValue(int a) {
        return a;
    }
}
