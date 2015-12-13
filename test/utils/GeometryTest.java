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
	}
}
