package utils;

import core.Population;
import core.Solution;

public class Geometry {

	private static double EPS = MyComparator.EPS;

	/**
	 * 
	 * @param P
	 *            point that we are computing distance for
	 * @param B
	 *            vector representing line
	 * @return distance between point A and line (0,0) <-> B
	 */
	public static double pointLineDist(double[] P, double[] B) {

		if (P.length != B.length) {
			throw new RuntimeException("Point and line do not have same dimensionality");
		}

		int numDim = P.length;

		if (numDim < 2) {
			throw new RuntimeException("Space needs to be at least two dimensional");
		}

		double max = Double.MIN_VALUE;
		for (double d : B) {
			max = Math.max(max, Math.abs(d));
		}
		if (max < EPS) {
			throw new RuntimeException("Line represented by degenerated vector (B = 0)");
		}

		// Define zero point
		double zero[] = new double[numDim];

		double t = dot(P, B) / dot(B, B);

		double resVector[] = new double[numDim];
		for (int i = 0; i < numDim; i++) {
			resVector[i] = P[i] - t * B[i];
			zero[i] = 0.0;
		}
		return euclideanDistance(zero, resVector);
	}

	public static double euclideanDistance(double[] P1, double[] P2) {
		if (P1.length != P2.length) {
			throw new RuntimeException("Points have different dimensionality");
		}

		double sum = 0.0;
		double dx;
		for (int i = 0; i < P1.length; i++) {
			dx = P1[i] - P2[i];
			sum += dx * dx;
		}

		return Math.sqrt(sum);
	}

	public static double dot(double[] A, double[] B) {
		if (A.length != B.length) {
			throw new RuntimeException("Vectors have different dimensionality");
		}
		int numDim = A.length;
		double res = 0;
		for (int i = 0; i < numDim; i++) {
			res += A[i] * B[i];
		}
		return res;
	}

	public static double populationVarMinDist(Population pop){
		double res = Double.MAX_VALUE;
		Solution closest1 = null;
		Solution closest2 = null;
		for(int i=0; i < pop.size(); i++){
			for(int j=i+1; j < pop.size(); j++){
				Solution s1 = pop.getSolution(i);
				Solution s2 = pop.getSolution(j);
				double dist = euclideanDistance(s1.getVariables(), s2.getVariables());
				if(Double.compare(dist, res ) < res){
					res = dist;
					closest1 = s1;
					closest2 = s2;
				}
			}
		}
//		System.out.println("Closest points: ");
//		System.out.println(closest1.toString());
//		System.out.println(closest2.toString());
		return res;
	}
	
	public static double populationObjMinDist(Population pop){
		double res = Double.MAX_VALUE;
		Solution closest1 = null;
		Solution closest2 = null;
		for(int i=0; i < pop.size(); i++){
			for(int j=i+1; j < pop.size(); j++){
				Solution s1 = pop.getSolution(i);
				Solution s2 = pop.getSolution(j);
				double dist = euclideanDistance(s1.getObjectives(), s2.getObjectives());
				if(Double.compare(dist, res ) < res){
					res = dist;
					closest1 = s1;
					closest2 = s2;
				}
			}
		}
//		System.out.println("Closest points: ");
//		System.out.println(closest1.toString());
//		System.out.println(closest2.toString());
		return res;
	}
}
