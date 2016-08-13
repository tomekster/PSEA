package utils;

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
			String s = "";
			for(int i=0; i<B.length; i++){
				s += B[i] + " ";
			}
			throw new RuntimeException("Line represented by degenerated vector (B = 0)\n B: [" + s + "]\n");
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
		double res = 0;
		for (int i = 0; i < A.length; i++) {
			res += A[i] * B[i];
		}
		return res;
	}
	
	public static Solution cast3dPointToPlane(double p[]){
		double tmp[] = {0,0};
		Solution res = new Solution(tmp, tmp);
		double a[] = p.clone();
		double sum = 0;
		for(double d : a){
			sum += d;
		}
		for(int i=0; i<p.length; i++){
			a[i] /= sum;
		}
		a[0] -= 1;
		double b[] = {-0.5, -0.5, 1};
		res.setObjective(0, pointLineDist(a, b));
		
		b = new double[]{-1,1,0};
		res.setObjective(1, pointLineDist(a, b));
		return res;
	}
	
	public static double[] randomPointOnSphere(int dim, double radius){
		NSGAIIIRandom rand = NSGAIIIRandom.getInstance();
		double res[] = new double[dim];
		double sum = 0;
		
		for(int i=0; i<dim; i++){
			res[i] = rand.nextGaussian();
			sum += res[i] * res[i];
		}
		sum = Math.sqrt(sum);
		for(int i=0; i<dim; i++){
			res[i] *= radius/sum;
		}
		return res;
	}
	
	public static double[] normalize(double[] a){
		double res[] = new double[a.length];
		double sum = 0;
		for(int i=0; i<a.length; i++){
			res[i] = a[i];
			sum += a[i];
		}
		
		for(int i=0; i<a.length; i++){
			res[i] /= sum;
		}
		return res;
	}

	public static double[] invert(double[] dimensions) {
		int len = dimensions.length;
		double lambda[] = new double[len];
		for (int i = 0; i < len; i++) {
			lambda[i] = 1 / dimensions[i];
		}
		return lambda;
	}
}
