package utils;

import core.hyperplane.ReferencePoint;

public class NormalizationTransform {
	private double[] originVector;
	private double[] dimesionScales;
	
	public NormalizationTransform(double[] originVector, double[] dimensionScales){
		this.originVector = originVector;
		this.dimesionScales = dimensionScales;
	}
	
	public void normalize(ReferencePoint rp){
		for(int i=0; i < rp.getNumDimensions(); i++){
			rp.setNormDim(i, (rp.getDim(i) - originVector[i]) * dimesionScales[i]); 
		}
	}
	
	public void denormalize(ReferencePoint rp){
		for(int i=0; i < rp.getNumDimensions(); i++){
			rp.setDim(i, (rp.getNormDim(i) / dimesionScales[i]) + originVector[i]); 
		}
	}
}
