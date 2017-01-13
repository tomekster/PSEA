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
		
		System.out.print("Nearest: ");
		for(double x : nearest.getObjectives()){
			System.out.print(x + ", ");
		}
		System.out.println("");
		
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

	
	/**
	 * Implements derivative defined here: https://en.wikipedia.org/wiki/Smooth_maximum  
	 * @param a - solution
	 * @param lambda - Chebyshev function direction
	 * @param i - id of variable with regard to which derivative is computed
	 * @return
	 */
	
	public static double smoothMaxGrad(double a[], double lambda[], int i){
		double alpha = 20, nominator = 0, denominator = 0;
		for(int j=0; j<a.length; j++){
			nominator += a[j] * lambda[j] * Math.exp(a[j] * lambda[j] * alpha);
			denominator += Math.exp(a[j] * lambda[j] * alpha);
		}
		return a[i] * Math.exp(a[i] * lambda[i] * alpha) / denominator * (1 + alpha * (a[i] * lambda[i] - nominator/denominator));
	}
}
