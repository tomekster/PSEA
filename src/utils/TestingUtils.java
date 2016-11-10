package utils;

import static org.junit.Assert.*;

public class TestingUtils {
	public static void assertDoubleArrayEquals(double a[], double b[]){
		assertTrue(a.length == b.length);
		for(int i=0; i < a.length; i++){
			assertEquals(a[i], b[i], Geometry.EPS);
		}
	}
}
