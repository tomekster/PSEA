package utils;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Solution;

public class ComparatorTest {

	private Comparator cp = new Comparator();
	
	@Test
	public void compareDoubleTest(){

		double a, b;
		
		a = 1.0;
		b = 2.0;
		assertEquals(1, cp.compareDouble(a, b));
		
		a = 2.0;
		b = 1.0;
		assertEquals(-1, cp.compareDouble(a, b));
		
		a = 2.0;
		b = 2.0;
		assertEquals(0, cp.compareDouble(a, b));
		
		a = 2.00000000009;
		b = 2.0;
		assertEquals(0, cp.compareDouble(a, b));
		
		a = 2.0000000001;
		b = 2.0;
		assertEquals(-1, cp.compareDouble(a, b));
	}
	
	@Test
	public void compareDominanceTest(){

		double[] varS1 = {1.0, 2.0, 3.0, 4.0};
		double[] varS2 = {1.0, 2.0, 3.0, 4.0};
		
		Solution s1, s2;
		
		s1 = new Solution(varS1);
		s2 = new Solution(varS2);
		
		assertEquals(varS1.length, s1.getNumVariables());
		assertEquals(varS2.length, s2.getNumVariables());
		assertEquals(0, cp.compareDominance(s1, s2));
		
		s1.setVariable(0, 0.5);
		assertEquals(-1, cp.compareDominance(s1, s2));
		
		s1.setVariable(0, 2.0);
		assertEquals(1, cp.compareDominance(s1, s2));
	}
}
