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
		assertEquals(2, GaussianElimination.execute(A, B)[0],Comparator.EPS );
		assertEquals(3, GaussianElimination.execute(A, B)[1],Comparator.EPS );
		assertEquals(-1, GaussianElimination.execute(A, B)[2],Comparator.EPS );
		
	}
}
