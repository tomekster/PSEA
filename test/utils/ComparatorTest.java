package utils;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Solution;

public class ComparatorTest {

	private MyComparator cp = new MyComparator();
	
	@Test
	public void compareDominanceTest(){

		double[] varS1 = {0.0, 0.0, 0.0, 0.0};
		double[] objS1 = {1.0, 2.0, 3.0, 4.0};
		double[] varS2 = {0.0, 0.0, 0.0, 0.0};
		double[] objS2 = {1.0, 2.0, 3.0, 4.0};
		
		Solution s1, s2;
		
		s1 = new Solution(varS1,objS1);
		s2 = new Solution(varS2,objS2);
		
		assertEquals(varS1.length, s1.getNumVariables());
		assertEquals(varS2.length, s2.getNumVariables());
		assertEquals(0, cp.compareDominance(s1, s2));
		
		s1.setVariable(0, 0.5);
		assertEquals(0, cp.compareDominance(s1, s2));
		
		s1.setObjective(0, 0.5);
		assertEquals(-1, cp.compareDominance(s1, s2));
		
		s1.setObjective(0, 2.0);
		assertEquals(1, cp.compareDominance(s1, s2));
	}
}
