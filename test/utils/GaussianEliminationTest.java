package utils;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.DegeneratedMatrixException;

public class GaussianEliminationTest {

	@Test
	public void wikipediaExample(){
		double A[][] = {
						{2,1,-1},
						{-3,-1,2},
						{-2,1,2} };
		double B[] = {8, -11, -3};
		
		double res[] = {2,3,-1};
		try {
			assertArrayEquals(res, GaussianElimination.execute(A, B),Geometry.EPS );
		} catch (DegeneratedMatrixException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * https://math.dartmouth.edu/archive/m23s06/public_html/handouts/row_reduction_examples.pdf
	 */
	@Test
	public void otherExample(){
		double A[][] = {
						{0,2,1},
						{1,-2,-3},
						{-1,1,2} };
		double B[] = {-8, 0, 3};
		
		double res[] = {-4,-5,2};
		try {
			assertArrayEquals(res, GaussianElimination.execute(A, B),Geometry.EPS );
		} catch (DegeneratedMatrixException e) {
			e.printStackTrace();
		}
	}
	
}
