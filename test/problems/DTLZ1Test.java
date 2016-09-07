package problems;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Problem;
import core.Solution;
import problems.dtlz.DTLZ1;

public class DTLZ1Test {
	
	@Test
	public void DTLZ1initializationTest(){
		Problem p = new DTLZ1(3);
		assertEquals(7, p.getNumVariables());
		assertEquals(3, p.getNumObjectives());
		assertEquals(0, p.getNumConstraints());
		assertEquals("DTLZ1", p.getName());
		for(int i=0; i< p.getNumVariables(); i++){
			assertEquals(0.0, p.getLowerBound(i), 1E-9);
			assertEquals(1.0, p.getUpperBound(i), 1E-9);
		}
		
	}
	
	@Test
	public void createSolutionTest(){
		Problem p = new DTLZ1(7);
		Solution s = p.createSolution();
		assertEquals(s.getNumVariables(), p.getNumVariables());
		for(int i=0; i< p.getNumVariables(); i++){
			assertTrue(s.getVariable(i) <= p.getUpperBound(i));
			assertTrue(s.getVariable(i) >= p.getLowerBound(i));
		}
	}
}
