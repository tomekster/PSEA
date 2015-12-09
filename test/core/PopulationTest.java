package core;

import static org.junit.Assert.*;

import org.junit.Test;

public class PopulationTest {
	@Test
	public void populationTest(){
			Population pop = new Population();
			Solution s1 = new Solution(3,3);
			s1.setVariable(0, 1);
			s1.setVariable(1, 2);
			s1.setVariable(2, 3);
			pop.addSolution(s1);
			Solution s2 = new Solution(3,3);
			s2.setVariable(0, 4);
			s2.setVariable(1, 5);
			s2.setVariable(2, 6);
			pop.addSolution(s2);
			Solution s3 = new Solution(3,3);
			s3.setVariable(0, 7);
			s3.setVariable(1, 8);
			s3.setVariable(2, 9);
			pop.addSolution(s3);
			Solution s4 = new Solution(3,3);
			s4.setVariable(0, 10);
			s4.setVariable(1, 11);
			s4.setVariable(2, 12);
			pop.addSolution(s4);
			assertEquals(4, pop.size());
			assertEquals(7, pop.getSolution(2).getVariable(0), 1E-9);
			assertEquals(11, pop.getSolution(3).getVariable(1), 1E-9);
			
			Population pop2 = new Population(pop);
			assertEquals(4, pop2.size());
	}
}
