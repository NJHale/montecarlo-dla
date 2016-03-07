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
        System.out.println("Parsing cmd line arguments");
        long walkSeed = 0;
        long oriSeed = 0;//8009832;
        int spaceSize = 0;
        int walks = 0;
        int buffer = 0;
        int L = 0;
        double A = 0;
        double B = 0;
        double C = 0;
        double Cnn = 0;
        double alpha = 0;
        boolean nonNewtFlag = false;


        int idx = 0;

        try {
            // fill variables
            walkSeed = Long.parseLong(args[idx++]);//8009832;
            oriSeed = Long.parseLong(args[idx++]);//9008932;
            spaceSize = Integer.parseInt(args[idx++]);
            walks = Integer.parseInt(args[idx++]);
            buffer = Integer.parseInt(args[idx++]);
            A = Double.parseDouble(args[idx++]);
            B = Double.parseDouble(args[idx++]);
            C = Double.parseDouble(args[idx++]);
            L = Integer.parseInt(args[idx++]);
            // check for non-newtonian goodness
            if (args.length > idx) {
                Cnn = Double.parseDouble(args[idx++]);
                alpha = Double.parseDouble(args[idx++]);
                nonNewtFlag = true;
            }
        } catch (Exception e) {
            System.err.println("cmd line arguments invalid or missing");
            System.err.println("Expected Format:\n walkSeed oriSeed spaceSize walks buffer A B C L [Cnn alpha]");
            System.exit(1);
        }

        // recalibrate spaceSize to a power of 2
        spaceSize = (int) Math.pow(2, (int)(Math.log(spaceSize - 1) / Math.log(2)) + 1);

        boolean displayOn = false;

        Walker walker;

        // check whether we are attempting a newtonian or non-newtonian

        if (nonNewtFlag) {
            walker = new RadialWalker(oriSeed, walkSeed, 3,
                    buffer, A, B, C, L, Cnn, alpha);
        } else {
            walker = new RadialWalker(oriSeed, walkSeed, 3,
                    buffer, A, B, C, L);
        }
	    //Attempt walker simulation

//        Walker walker = new RadialWalker(oriSeed, walkSeed, 3,
//                buffer, A, B, C, L, .85, .233);
        //Walker walker = new DumbWalker(walkSeed, walkSeed);
        //instantiate the walk space
        Space space = new ArraySpace(spaceSize);
        //create the simulation
        DLASimulation dla = new DLASimulation(space, walker, displayOn);
        long start = System.currentTimeMillis();
        space = dla.simulate(walks);
        System.out.println(walks + " random walks completed in " +
                (System.currentTimeMillis() - start) + " ms");
        //rasterize the dla and store the image
        int hue = 0xffffff;
        BufferedImage dlaImg = space.rasterize(hue);
        //attempt write the image to a file
        try {
            File out = new File("images/dla-" + spaceSize +
                    "-" + walks + "-" + walkSeed + ".png");
            ImageIO.write(dlaImg, "png", out);
        } catch (Exception e) {
            System.err.println("An error occurred while attempting to store the " +
                    "DLA in a png");
        }
        if(displayOn) {
            JFrame frame = new JFrame("DLA");
            //set the scaling factor
            int scale = 1;
            //render the space on a JFrame
            JPanel dlaPanel = new DLAPanel(dlaImg, scale);
            frame.setSize(spaceSize * scale, spaceSize * scale);
            frame.setContentPane(dlaPanel);
            //make the frame's content visible
            frame.setVisible(true);
            //set the default close op
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}
