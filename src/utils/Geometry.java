package utils;

public class Geometry {

	private static double EPS = MyComparator.EPS;

	/**
	 * 
	 * @param A
	 *            point that we are computing distance for
	 * @param B
	 *            point on line (0,0) <-> B
	 * @return distance between point A and line (0,0) <-> B
	 */
	public static double pointLineDist(double[] A, double[] B) {

		if (A.length != B.length) {
			throw new RuntimeException("Point and line do not have same dimensionality");
		}

		int numDim = A.length;
		
		if(numDim < 2){
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

		// Find vector orthogonal to B by swapping nonnegative dimension with
		// any other dimension and changing it's sign
		
		double orthogonalB[] = new double[numDim];
		for(int i=0; i<numDim; i++){
			orthogonalB[i] = 0;
		}
		if (B[0] < EPS){
			orthogonalB[0] = 1;
		}else{
			orthogonalB[0] = -B[1];
			orthogonalB[1] = B[0];
		}
		
		double sum = 0;
		for (int i = 0; i < A.length; i++) {
			sum += A[i] * orthogonalB[i];
			zero[i] = 0.0;
		}
		sum /= euclideanDistance(zero, B);
		return Math.abs(sum);
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
}
