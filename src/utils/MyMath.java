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
		Solution nearest = null;
		for(Solution s : pop.getSolutions()){
			double d = Double.min(min, Geometry.euclideanDistance(point, s.getObjectives()));
			if(d < min){
				min = d;
				nearest = s;
			}
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
	
	public static double variance(double a[]){
		double mean = 0, res = 0;
		for(double v : a) mean += v;
		mean /= a.length;
		for(double v : a) res += (v-mean) * (v-mean);
		return res;
	}
}
