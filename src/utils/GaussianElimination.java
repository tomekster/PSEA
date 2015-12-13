package utils;

/*
 * Copyright © 2000–2011, Robert Sedgewick and Kevin Wayne.
 * Last updated: Sun Aug 2 18:43:37 EDT 2015. 
 */
public class GaussianElimination {

	public static double[] execute(double[][] A, double[] B) {

		double A2[][] = A.clone();
		
		if (A.length == 0) {
			throw new RuntimeException("Empty coefficinet matrix");
		}
		if (A.length != A[0].length) {
			throw new RuntimeException("Non-square coefficient matrix");
		}

		int N = A.length;

		for (int i = 0; i < N; i++) {
			
			double max = Double.MIN_VALUE;
			int maxRow = i;
			for (int j = i; j < N; j++) {
				double val = Math.abs(A[j][i]);
				if(Double.compare(max, val) < 0){
					max = val;
					maxRow = j;
				}
			}

			if (max < MyComparator.EPS) {
				throw new RuntimeException("Degenerated Matrix!");
			}
			
			//Place max element in current column in current row
			double temp[] = A[i].clone();
			A[i] = A[maxRow];
			A[maxRow] = temp;
			
			double temp2 = B[i];
			B[i] = B[maxRow];
			B[maxRow] = temp2;
//			System.out.println("ROW: " + i);
//			System.out.println("BEFORE");
//			printMatrix(M);
			for (int j = i + 1; j < N; j++) {
				double div = A[j][i] / A[i][i];
				for (int k = i; k < N; k++) {
					A[j][k] -= div * A[i][k];
				}
				B[j] -= div * B[i];
			}
//			System.out.println("AFTER");
//			printMatrix(A);
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
	
	public static void printMatrix(double M[][]){
		for(int i=0; i < M.length; i++){
			for(int j=0; j < M[0].length; j++){
				System.out.print(M[i][j] + " ");
			}
			System.out.println();
		}
	}

}
