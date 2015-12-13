package utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeometryTest {

	private double EPS = MyComparator.EPS;

	@Test
	public void testEuclideanDistance() {

		double P1[] = { 1, 1, 1 };
		double P2[] = { 0, 0, 0 };
		assertEquals(Math.pow(3, 0.5), Geometry.euclideanDistance(P1, P2), EPS);

		double P3[] = { 0, 0, 0, 0 };
		double P4[] = { 1, 2, 3, 4 };
		assertEquals(Math.pow(1 + 4 + 9 + 16, 0.5), Geometry.euclideanDistance(P3, P4), EPS);
	}
	
	@Test
	public void testPointLineDist() {
		double P1[] = { 1, 1, 1 };
		double P2[] = { 2, 2, 2 };
		assertEquals(0, Geometry.pointLineDist(P1, P2), EPS);
		
		double P3[] = { 0, 1, 1 };
		double P4[] = { 0, 0, 1 };
		assertEquals(1, Geometry.pointLineDist(P3, P4), EPS);
		
		double P5[] = { 1, 1, 1 };
		double P6[] = { 0, 0, 1 };
		assertEquals(Math.sqrt(2), Geometry.pointLineDist(P5, P6), EPS);
		
		double P7[] = { 0, 0, 1 };
		double P8[] = { 0, 1, 1 };
		assertEquals(Math.sqrt(2)/2, Geometry.pointLineDist(P7, P8), EPS);
		
		double P9[] = { 0, 0, 1 };
		double P10[] = { 1, 1, 1 };
		assertEquals(Math.sqrt(2.0/3), Geometry.pointLineDist(P9, P10), EPS);
	}
}
