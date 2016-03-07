package edu.njit.math450.util;

import edu.njit.math450.matrix.Space;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

//import org.apache.commons.math3.fitting;

/**
 * Created by nhale on 11/20/15.
 */
public class DLAUtil {

    /**
     * Calculates the fractal dimension of Space A
     * @param A Adjacency Matrix representing the fractal
     * @return The fractal dimension of Space A
     */
    public static double fractalDim(Space A) {
        //int boxes = (int) (Math.log(A.size()) / Math.log(2));

        List<Double> cnts = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        int start = (int) Math.pow(2, Math.round(Math.log(A.size()) / Math.log(2)) / 2);
        for (int i = start; i <= A.size(); i *= 2) {
            // System.out.println("boxNumber: " + i);
            x.add(Math.log(i));
            cnts.add(Math.log(boxCount(A, i)));
        }
        //calculate the slope of the linear regression
        double dim = linReg(x, cnts);
        return  dim;//default return
    }

    /**
     * Counts the number of boxes in the given adjacency matrix
     * that contain values > 0
     * @param A Adjacency Matrix
     * @param numBoxes Number of boxes to count
     * @return Number of boxes that contain values > 0
     */
    private static int boxCount(Space A, int numBoxes) {
        int n = A.size();//get the size of n x n Space A
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
                        if (A.get(i, j) > 0) {//check to see if we've found a hit
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


}
