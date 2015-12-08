package utils;

public class Geometry {
	/**
	 * 
	 * @param A point that we are computing distance for
	 * @param B point on line (0,0) <-> B 
	 * @return distance between point A and line (0,0) <-> B
	 */
	public static double pointLineDist(double[] A, double[] B) {
		
		if(A.length != B.length){
			throw new RuntimeException("Point and line do not have same dimensionality");
		}

		double zero[] = new double[B.length];
		double sum = 0;
		for(int i=0; i<A.length;i++ ){
			sum += A[i] * B[i];
			zero[i] = 0.0;
		}
		
		sum /= euclideanDistance(zero,B);
		return sum;
	}

	public static double euclideanDistance(double[] P1, double[] P2) {
		if(P1.length != P2.length ){
			throw new RuntimeException("Points have different dimensionality");
		}
		
		double sum = 0.0;
		double dx;
		for(int i=0; i<P1.length; i++){
			dx = P1[i] - P2[i];
			sum += dx*dx;
		}
		
		return Math.sqrt(sum);
	}
}
