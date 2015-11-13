package org.javaDLA;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;


public class SALTFunctionsNewtonian {
	int xo,yo;
	Random rand;
	
	public static void main(String args[]) {
		SALTFunctionsNewtonian test=new SALTFunctionsNewtonian();
		test.run(1000,100000,1.0,0.5,9);
		//test.run(1000,100000,1.0,0.5,9);
	}
	
	public void run(int n, int num_part, double A, double B, double L) {
		/*
		 * //data = SALTFunctionsNewtonianJULIA(n,num_part,A,B,l)
		//n = the size of the rectangle that holds the circle
		//num_part= the amount of walkers you want, normally 10000
		//A = variable for probability, normally 1
		//B = variable for probability, normally 0.5
		//L = variable used to find local curvature, normally 9
		 */
		
		rand=new Random(231);
		//rand=new Random();
		
		xo=n/2; //center of circle
		//finds the center of the square determined by the n value given to the function
		yo=n/2; //center of circle
		
		byte[][] data=new byte[n][n];
		data[xo][yo]=1;data[xo+1][yo]=1;
		data[xo][yo+1]=1;data[xo+1][yo+1]=1;
		//2x2 seed to prevent diagonal sticking initially
		double R=n/50.0; //get Radius where random walkers spawn to 1/10th of matrix dimension n 
		//dimension=[];
	    int num_walk = 0;
	    //double[][] dimension=new double[num_part/500][2];
	    
	    int moveon;double theta;
	    int col;int row;
	    boolean columnflag;
	    int move;int neigh;
	    
	    for(int k=0;k<num_part;k++) { //runs the function for the amount of walkers initially specified 
	        moveon=0;
	    	if(k%5000==0) {System.out.println(k);}
	    	theta=2*Math.PI*rand.nextDouble(); 
	    	//modeled as a circle in first quadrant centered at (xo,yo)
	    	//rand randomly generates a number so each walker in theory will start from a different spot
	    	col=(int)Math.floor(xo+(R-1)*Math.cos(theta)); 
	    	//start the walker at a random position along the circle of radius R 
	    	//using the randomly generated theta value and the R value
	    	row=(int)Math.floor(yo+(R-1)*Math.sin(theta));
	    	
	    	columnflag=false; //used to allow for a new walker to start walking
	    	while(!columnflag) {
	    		move=rand.nextInt(4);
	    		/*
	    		 * 0 north 1 south 2 east 3 west, begin moving the walker randomly
	             * returns a one-by-one matrix with a value of 0,1,2, or 3 which is later put into the g or f 
	             * function above to determine which direction the walker is told to move
	    		 */
	    		if(move>=2) { //if walker is to be moved east or west (IE move= 2 or 3)
	    			//check whether that "move" is ok to make (not equal to 1)
	    			//west and east: g=col+1 for east g=col-1 for west
	    			//when a number is randomly generated, this changes it into the correct movement: W or E
	    			int newcol=-2*move+5+col;
	    			if((rsq(row,newcol)<R*R) && (data[row][newcol]!=1)){ 
	    				//make sure that move is not going to cross over our boundary determined 
	    				//by our R (Radius) value
                    	col = newcol; //make move if the walker will not move out of the circle
	    			}//if move not okay, then another randomly generated move is found
	    		} else { //if the move is south or north (0 or 1) check whether the move will be occupied
	    			//%north and south: f=row+1 for south, f=row-1 for north 
	    			//when a number is randomly generated, this changes it into the correct movement: N or S
	    			int newrow=-2*move+1+row;
	    			if((rsq(newrow,col)<R*R)&&(data[newrow][col]!= 1)){
	    				//make sure the move is not going to cross the circle determined by the Radius
	                    row = newrow; //make move
	    			}//if move not okay, then another randomly generated move is found
	    		}
	    		neigh=0;
                
                if(col<data[0].length-1) {neigh+=data[row][col+1];}
                if(col>0)                {neigh+=data[row][col-1];}
                if(row<data.length-1)    {neigh+=data[row+1][col];}
                if(row>0)                {neigh+=data[row-1][col];}
                
                //if the walker is next to a stuck walker, then the walker has a chance of sticking there
	    		if(neigh>=1){//we have reached the seed-- need to check the local curvature near the seed               
		            double p=FindProb(data,row,col,A,B,L);
		            //function used to determine the probability for the walker to stick where it is
		            //takes in data, row, col, A, B, and L, outputs p
	                if(rand.nextDouble()<=p||p>=1){ 
	                	// roll a dice (rand) and determine if walker sticks
	                	// OR if the proability is > 1 (high curvature) stick
	                    // if rand is greater than p, then the loop is broken,
	                	// this walker will not stick and a new walker is begun
	                    int row0=row;
	                    int col0=col;
	                    while(true){
	                        int[] pos=NewHome(row,col,data);
	                        //function using a lxl box to count the number of 1s inside
	                        //kadanoff paper (http://m.njit.edu/~kondic/capstone/2014/vicsek_prl_84.pdf)
	                        //takes in row, col, and data
	                        //returns col and row which might or might not have been updated
	                        row=pos[0];col=pos[1];
	                        if(row==row0 && col==col0){
	                            break;
	                        }
	                        row0 = row;
	                        col0 = col;
	                    }
	                    //function made to prevent holes using many conditionals
	                    //inputs row, col, and data
	                    //outputs a value co, which is used below to determine whether the walker
	                    //is allowed to stick or not              
	                    if(!HolePrevent(row,col,data)) {//the walker is finally allowed to stick
	                        num_walk=num_walk+1;
	                        columnflag = true; 
	                        //to break you out of the while loop so a new walker can start
	                        //after this 'if' loop is finished    
	                        data[row][col]=1; //walker has stuck 
	                        //all spots where a walker has stuck have a value of '1', all other spots are '0'
	                        if(rsq(row,col)>(R-5)*(R-5)) {//adjust R based on last stick
	                        	//make sure that the last sticking particle is within 2 of R
	                            R = R+n/100.0; //makes R bigger ck
	                        }
	                    } else { 
	                        moveon=moveon+1;
	                    }
	                    
	                    if(moveon >= 10) {
	                        columnflag = true;//does not allow that walker to stick because it got stuck
	                    }
	                }
	    		}
	    	}
	    	
	    	//if(k%500==0) {
	            //dimension[k/500][1]=k; 
	            //dimension[k/500][2]=hausDim(data);
	    	//}
	    }
		
	    display(data);
		/*	    
	    figure();
	    plot(dimension(:,1),dimension(:,2),'-o');
	    title('Fractal Dimension as a Function of Time SEED=8');
	    xlabel('Time (# of Walkers)'); ylabel('Fractal Dimension');
	    figure();
	    plot(dimension(2:end,2)./dimension(1:(end-1),2),'-o');
	    title('Density Correlation as a Function of Time');
	 */
	}
	
	public void display(byte[][] data) {
//		for(int i=3*data.length/8;i<5*data.length/8;i++) {
//	    	for(int j=data[0].length/4;j<3*data[0].length/4;j++) {
//	    		if(data[i][j]!=0) {
//	    			System.out.print('#');
//	    		} else {
//	    			System.out.print('.');
//	    		}
//	    	}
//	    	System.out.println();
//	    }
		int dheight=1000/data.length;
		int dwidth=1000/data[0].length;
		int height=dheight*data.length;
		int width=dwidth*data[0].length;
		
		BufferedImage img =new BufferedImage(height,width,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		for(int i=0;i<data.length;i++) {
	    	for(int j=0;j<data[0].length;j++) {
	    		if(data[i][j]!=0) {
	    			g2.setColor(Color.black);
	    			g2.fill(new Rectangle(i*dheight,j*dheight,dheight,dwidth));
	    		} else {
	    			g2.setColor(Color.white);
	    			g2.draw(new Rectangle(i*dwidth,j*dwidth,dheight,dwidth));
	    		}
	    	}
	    }
		g2.dispose();
		JFrame f=new JFrame("Java DLA");
		f.getContentPane().setLayout(new FlowLayout());
		f.getContentPane().add(new JLabel(new ImageIcon(img)));
		f.pack();
		f.setVisible(true);
	}

	
	private double rsq(int x,int y) {
		return (x-xo)*(x-xo)+(y-yo)*(y-yo); 
		//making the initial circle around the seed, using the equation for a circle
	}
	
	private int[] NewHome(int row,int col,byte[][] data) {
		/*
		 * function [row, col] = NewHome(row,col,data)
		* [row, col] = NewHome(row,col,data)
		* row is the row the walker wants to stick to
		* col is the column the walker wants to stick to
		* data is the matrix of 1's and 0's
		* move is a number[0 3] corresponding tothe last direction the walker moved
		 */
		int max=0;
		int max0=max;
		int[][] tiedForMax=new int[9][2];
		tiedForMax[0][0]=row;
		tiedForMax[0][1]=col;
		int maxIndex=0;
		for(int i=row-1;i<=row+1;i++) {
			//using a lxl box count the number of 1s inside
			//kadanoff paper (http://m.njit.edu/~kondic/capstone/2015/vicsek_prl_84.pdf)
	        for(int j=col-1;j<=col+1;j++){
	        	//move walker to place with least potential=highest number of neighbors
	            if(data[i][j]==0) {
	                //count = sum(sum(data[i-1:i+1][j-1:j+1]));
	            	int count=0;
	            	for(int i2=i-1;i2<=i+1;i2++) {
	            		for(int j2=j-1;j2<=j+1;j2++) {
	            			count+=data[i2][j2];
	            		}
	            	}
	                if(count > max) {
	                    max = count;
	                    tiedForMax =new int[9][2];
	                    tiedForMax[0][0]=i;
	            		tiedForMax[0][1]=j;
	            		maxIndex=0;
	                }else if (max == count && count > max0){
	                	maxIndex++;
	                    tiedForMax[maxIndex][0]=i;
	                    tiedForMax[maxIndex][1]=j;
	                }
	            }
	        }    
		}
		
	    int nelm = maxIndex+1;
	    if (nelm == 1) {
	        row = tiedForMax[0][0];
	        col = tiedForMax[0][1];
	    } else{
	        int i = rand.nextInt(nelm);
	        row = tiedForMax[i][0];
	        col = tiedForMax[i][1];
	    }
		
		int[] result={row,col};
		return result;
	}
	
	private double hausDim(byte[][] I) {
		// HAUSDIM Returns the Haussdorf fractal dimension of an object represented by
		// a binary image.
		int maxDim=Math.max(I.length,I[0].length);
		int log2MaxDim=(int)Math.ceil(Math.log(maxDim)/Math.log(2));
		
		//int newDimSize=(int)Math.pow(2,log2MaxDim);
		//int rowPad=newDimSize-I.length;
		//int colPad=newDimSize-I[0].length;
		//I=padarray(I,[rowPad,colPad],'post')
		// Pad the image with background pixels so that its dimensions are a power of 2.
		
		int[] boxCounts=new int[log2MaxDim];
		double[] resolutions=new double[log2MaxDim];
		
		int boxSize=I.length;
		int boxesPerDim=1;
		int idx=0;
		while(boxSize>=1) {
			int boxCount=0;
			for(int boxRow=0;boxRow<boxesPerDim;boxRow++) {
				for(int boxCol=0;boxCol<boxesPerDim;boxCol++) {
					int minRow = (boxRow - 1) * boxSize + 1;
	                int maxRow = boxRow * boxSize;
	                int minCol = (boxCol - 1) * boxSize + 1;
	                int maxCol = boxCol * boxSize;
	                boolean objFound=false;
	                for(int row=minRow;row<=maxRow;row++) {
		                for(int col=minCol;col<=maxCol;col++) {
		                    if(I[row][col]!=0) {
		                        boxCount++;
		                        objFound=true;
		                    } 
		                    if(objFound){
		                        break; // Break from nested loop.
		                    }
		                }
		                if(objFound) {
	                        break; // Break from nested loop.
	                    }
	                }
				}
			}
			idx++;
	        boxCounts[idx] = boxCount;
	        resolutions[idx] =1.0/boxSize;
	        
	        boxesPerDim*=2;
	        boxSize/=2;
		}
		PolynomialCurveFitter fitter=PolynomialCurveFitter.create(1);
		WeightedObservedPoints obs=new WeightedObservedPoints();
		
		for(int i=0;i<log2MaxDim;i++) {
			obs.add(Math.log(resolutions[i]),Math.log(boxCounts[i]));
		}
		
		//obs.add(x,y)
		double[] coeff=fitter.fit(obs.toList());
		
		return coeff[0];
		//D = polyfit(log(resolutions), log(boxCounts), 1);
	}
	
	private double FindProb(byte[][] data, int row, int col, double A, double B, double L) {
		/*
		 * function p = FindProb(data, row, col, A, B, l)
		//prob = FindProb(row, col, A, B, l)
		//data is the matrix of 0's and 1's
		//row is the row where the walker might stick
		//col is the column where the walker might stick
		//A is specified from the input, for the probability equation
		//B is specified from the input, for the probability equation
		//l is specified from the input, for the probability equation
		*/
		int Ni=0;
		for(int i=row-4;i<=row+4;i++) {//using a lxl box count the number of 1s inside
			//kadanoff paper (http://m.njit.edu/~kondic/capstone/2014/vicsek_prl_84.pdf)
			for(int j=col-4;j<=col+4;j++) {
				Ni+=data[i][j]; //if there is a walker already at that location, add one to Ni
			}
		}
		double C=.01;
		double p = A*(Ni/L/L-(L-1)/2/L) + B; //probability of sticking based on number of 1s inside box
		if (p < C) { //if the probability happens to be negative (small curvature) 
			// set p=C so the code doesnt get stuck
	        p = C; //to get negatives, you need to make A and B different than 1 and 0.5
		}
		return p;
	}
	
	private boolean HolePrevent(int row, int col, byte[][] data) {
		int[] delta_row={0,-1,-1,-1,0,1,1,1};
		int[] delta_col={1,1,0,-1,-1,-1,0,1};
		
		int curr_state=data[row+1][col+1];
	    int flips=0;
	    for(int i=0;i<8;i++) {
	        if(curr_state!=data[row+delta_row[i]][col+delta_col[i]]) {
	            flips=flips+1;
	            curr_state=data[row+delta_row[i]][col+delta_col[i]];
	        }
	    }
	    if(flips>=4) {
	        return true; //prevent the walker from sticking
		}
		return false;
	}
}
