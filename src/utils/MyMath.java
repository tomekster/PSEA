package utils;

import core.Population;
import core.points.Solution;

public class MyMath {
	/**
	 * 
	 * @param point
	 * @param pop
	 * @return Return distance between point and closest solution from pop (in objectives space)
	 */
	public static double getMinDist(double point[], Population pop){
		double min = Double.MAX_VALUE;
		for(Solution s : pop.getSolutions()){
			min = Double.min(min, Geometry.euclideanDistance(point, s.getObjectives()));
		}
		return min;
	}
	
	/**
	 * 
	 * @param point
	 * @param pop
	 * @return Computes average distance between point and solutions from pop (in objectives space)
	 */
	public static double getAvgDist(double point[], Population pop){
		double avg = 0;
		for(Solution s : pop.getSolutions()){
			avg += Geometry.euclideanDistance(point, s.getObjectives());
		}
		return avg / pop.size();
	}
	
	
}
