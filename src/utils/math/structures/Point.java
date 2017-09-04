package utils.math.structures;

import java.util.ArrayList;

public class Point {
	private int numDim;
	private double[] dimensions;
	
	public Point(ArrayList <Double> dimensions){
		numDim = dimensions.size();
		for(int i=0; i< numDim; i++){
			this.dimensions[i] = dimensions.get(i);
		}
	}
	public Point(double[] dimensions){
		numDim = dimensions.length;
		this.dimensions = dimensions.clone();
	}
	
	public int getNumDIm(){
		return numDim;
	}
	
	public double getDim(int i){
		return dimensions[i];
	}
	public double[] getDimensions() {
		return dimensions;
	}
}
