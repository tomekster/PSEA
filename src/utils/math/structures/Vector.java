package utils.math.structures;

import javax.management.RuntimeErrorException;

import utils.math.Geometry;

public class Vector{
	private double [] vals;
	
	public Vector(double [] vals){
		this.vals = vals.clone();
	}
	
	public double getLen(){
		return Geometry.vectorLength(vals);
	}

	public int getNumDim() {
		return vals.length;
	}

	public double getDim(int i) {
		return vals[i];
	}
	
	public double[] getDim() {
		return vals;
	}
	
	public Vector scale(double multiplier){
		double[] res = new double[getNumDim()];
		for(int i=0; i<res.length; i++){
			res[i] = getDim(i) * multiplier;
		}
		return new Vector(res);
	}

	public Vector subtract(Point p) {
		if(p.getNumDim() != this.getNumDim()){
			throw new RuntimeErrorException(new Error(), "Cannot subtract point from vector when they have different dimensionality! Vector: " + this.getNumDim() + ", Point: " + p.getNumDim());
		}
		
		for(int i=0; i<getNumDim(); i++){
			this.vals[i] -= p.getDim(i);
		}
		return this;
	}
}
