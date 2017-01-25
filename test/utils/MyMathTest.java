package utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MyMathTest {

	private double EPS = Geometry.EPS;

	@Test
	public void smoothMaxGradTest(){
		double a[] = {1,2};
		double b[] = {2,1};
		double lambda[] = {0.5,0.5};
		assertEquals(-0.0004085602, MyMath.smoothMaxGrad(a, lambda, 0), EPS);
	}

}
