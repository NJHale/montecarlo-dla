package edu.njit.math450.util;

import edu.njit.math450.matrix.AdjMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhale on 11/20/15.
 */
public class DLAUtil {

    /**
     * Calculates the fractal dimension of AdjMatrix A
     * @param A Adjacency Matrix representing the fractal
     * @return The fractal dimension of AdjMatrix A
     */
    public static double fractalDim(AdjMatrix A) {
        int boxes = (int) (Math.log(A.size()) / Math.log(2));

        List<Double> boxCnts = new ArrayList<Double>();
        for (int i = 0; i < boxes; i ++) {
            boxCnts.set(i, Math.log(boxCount(A, i)));

        }
        return -1;//default return
    }

    private static int boxCount(AdjMatrix A, int size) {
        int n = A.size();//get the size of n x n AdjMatrix A
        //instantiate the box count
        int count = 0;
        //calculate the scaled size of each box
        int boxSize = (int) (n / size);
        for (int ibox = 0; ibox < size; ibox++) {//traverse each box
            int si = ibox * boxSize;//get the starting i
            for(int jbox = 0; jbox < size; jbox++) {
                int sj = jbox * boxSize;//get the starting j
                for (int i = si; i < ibox * n + ibox; i++) {
                    for (int j = sj; j < jbox * n + jbox; j++) {
                        if (A.get(i, j) > 0) {//check to see if we've found a hit
                            //add the hit
                            count++;
                            //move to the next box
                            i = ibox * n + ibox;
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }


}
