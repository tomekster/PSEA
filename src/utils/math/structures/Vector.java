package utils.math.structures;

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
}
