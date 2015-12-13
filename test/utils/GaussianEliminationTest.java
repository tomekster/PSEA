package utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class GaussianEliminationTest {

	@Test
	public void wikipediaExample(){
		double A[][] = {
						{2,1,-1},
						{-3,-1,2},
						{-2,1,2} };
		double B[] = {8, -11, -3};
		
		double res[] = {2,3,-1};
		assertArrayEquals(res, GaussianElimination.execute(A, B),MyComparator.EPS );
		
	}
}
