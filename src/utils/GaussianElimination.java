package utils;

/*
 * Copyright © 2000–2011, Robert Sedgewick and Kevin Wayne.
 * Last updated: Sun Aug 2 18:43:37 EDT 2015. 
 */
public class GaussianElimination {

	public static double[] execute(double[][] A, double[] B) throws DegeneratedMatrixException {

		double A2[][] = A.clone();
		
		if (A.length == 0) {
			throw new RuntimeException("Empty coefficinet matrix");
		}
		if (A.length != A[0].length) {
			throw new RuntimeException("Non-square coefficient matrix");
		}

		int N = A.length;

		for (int colId = 0; colId < N; colId++) {
			
			double max = Double.MIN_VALUE;
			int maxRow = colId;
			for (int rowId = colId; rowId < N; rowId++) {
				double val = Math.abs(A[rowId][colId]);
				if(Double.compare(max, val) < 0){
					max = val;
					maxRow = rowId;
				}
			}

			if (max < utils.Geometry.EPS) {
				System.out.println("ROW: " + colId);
				printMatrix(A2);
				throw new DegeneratedMatrixException();
			}
			
			//Place max element in current column in current row
			double temp[] = A[colId].clone();
			A[colId] = A[maxRow];
			A[maxRow] = temp;
			
			double temp2 = B[colId];
			B[colId] = B[maxRow];
			B[maxRow] = temp2;

			for (int rowId = colId + 1; rowId < N; rowId++) {
				double div = A[rowId][colId] / A[colId][colId];
				for (int k = colId; k < N; k++) {
					A[rowId][k] -= div * A[colId][k];
				}
				B[rowId] -= div * B[colId];
			}
		}
		
		double[] x = new double[N];
		for (int rowId = N - 1; rowId >= 0; rowId--) {
			double sum = 0.0;
			for (int colId = rowId + 1; colId < N; colId++) {
				sum += A[rowId][colId] * x[colId];
			}
			x[rowId] = (B[rowId] - sum) / A[rowId][rowId];
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
