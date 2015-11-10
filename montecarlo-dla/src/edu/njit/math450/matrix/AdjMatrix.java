package edu.njit.math450.matrix;

import java.awt.image.BufferedImage;

/**
 * Created by nhale on 11/5/2015.
 */
public abstract class AdjMatrix {
    /**
     * Gets the value of the AdjMatrix at (i, j)
     * @param i row
     * @param j column
     * @return the value of the AdjMatrix at (i, j)
     */
    public abstract int get(int i, int j);

    /**
     * Sets the value of the AdjMatrix at (i, j)
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
     * Rasterizes the AdjMatrix into a
     * BufferedImage, pixel by pixel and
     * returns the result
     * @param hue integer value of hue to rasterize matrix with
     * @return Rasterization of the AdjMatrix
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
                int palette = (int) ((Math.exp(r) + hue) % 16777215) * get(i, j);
                img.setRGB(i, j, palette);
            }
        }
        return img;
    }

}
