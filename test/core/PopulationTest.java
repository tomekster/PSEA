package core;

import static org.junit.Assert.*;

import org.junit.Test;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.Solution;

public class PopulationTest {
	@Test
	public void populationTest(){
			double var1[] = {1,2,3};
			double var2[] = {4,5,6};
			double var3[] = {7,8,9};
			double var4[] = {10,11,12};
			double obj[] = {0};
			Population pop = new Population();
			pop.addSolution(new Solution(var1,obj));
			pop.addSolution(new Solution(var2,obj));
			pop.addSolution(new Solution(var3,obj));
			pop.addSolution(new Solution(var4,obj));
			assertEquals(4, pop.size());
			assertEquals(7, pop.getSolution(2).getVariable(0), 1E-9);
			assertEquals(11, pop.getSolution(3).getVariable(1), 1E-9);
			
			Population pop2 = new Population(pop);
			assertEquals(4, pop2.size());
	}
}
