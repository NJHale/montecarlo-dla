package edu.njit.math450.dla;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by nhale on 11/6/2015.
 */
public class DLAPanel extends JPanel {

    private BufferedImage dlaImg;

    private int scale;

    private int size;

    public DLAPanel(BufferedImage dlaImg, int scale) {
        this.scale = scale;
        this.dlaImg = dlaImg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //draw and scale image
        g.drawImage(dlaImg, 0, 0, dlaImg.getHeight()*scale,
                dlaImg.getWidth()*scale, null);
    }
}
