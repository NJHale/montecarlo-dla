package edu.njit.math450;

import edu.njit.math450.dla.DLAPanel;
import edu.njit.math450.dla.DLASimulation;
import edu.njit.math450.matrix.*;
import edu.njit.math450.walker.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Simulation!");
        //hard code initial values for now
        long walkSeed = 8009832;
        long oriSeed = 9008932;
        int n = 1000;
        int walks = 100000;
        int buffer = 3;
        double A = 2;
        double B = .15;
        double C = .01;
        double L = 9;
	    //Attempt walker simulation
        Walker walker = new RadialWalker(oriSeed, walkSeed, 3,
                buffer, A, B, C, L, true);//new DumbWalker(walkSeed, stickSeed);
        //Walker walker = new DumbWalker(walkSeed, walkSeed);
        //instantiate the walk space
        AdjMatrix space = new ArrayAdjMatrix(n);
        //create the simulation
        DLASimulation dla = new DLASimulation(space, walker);
        long start = System.currentTimeMillis();
        space = dla.simulate(walks);
        System.out.println(walks + " random walks completed in " +
                (System.currentTimeMillis() - start) + " ms");
        //rasterize the dla and store the image
        int hue = 0xffffff;
        BufferedImage dlaImg = space.rasterize(hue);
        //attempt write the image to a file
        try {
            File out = new File("images/dla-" + n +
                    "-" + walks + "-" + walkSeed + ".png");
            ImageIO.write(dlaImg, "png", out);
        } catch (Exception e) {
            System.err.println("An error occurred while attempting to store the " +
                    "DLA in a png");
        }

        JFrame frame = new JFrame("DLA");
        //set the scaling factor
        int scale = 1;
        //render the space on a JFrame
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
