package utils;

/*
 * Copyright © 2000–2011, Robert Sedgewick and Kevin Wayne.
 * Last updated: Sun Aug 2 18:43:37 EDT 2015. 
 */
public class GaussianElimination {

	public static double[] execute(double[][] A, double[] B) {

		if (A.length == 0) {
			throw new RuntimeException("Empty coefficinet matrix");
		}
		if (A.length != A[0].length) {
			throw new RuntimeException("Non-square coefficient matrix");
		}

		int N = A.length;

		for (int i = 0; i < N; i++) {
			double max = Double.MIN_VALUE;
			for (int j = i; j < N; j++) {
				max = Double.max(max, Math.abs(A[j][i]));
			}

			if (max < Comparator.EPS) {
				throw new RuntimeException("Degenerated Matrix!");
			}

			for (int j = i + 1; j < N; j++) {
				double div = A[j][i] / A[i][i];
				for (int k = i; k < N; k++) {
					A[j][k] -= div * A[i][k];
				}
				B[j] -= div * B[i];
			}
		}
		double[] x = new double[N];
		for (int i = N - 1; i >= 0; i--) {
			double sum = 0.0;
			for (int j = i + 1; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			x[i] = (B[i] - sum) / A[i][i];
		}
		return x;
	}

}
