package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.VectorSolution;
import algorithm.rankers.NonDominationRanker;

public class NonDominatedSortTest {

	Population p;

	@Before
	public void init() {
		p = new Population();
	}

	@Test
	public void oneSolutionPopulationTest() {
		VectorSolution s1;

		double[] var1 = { 0.0, 0.0, 0.0 };
		double[] obj1 = { 1.0, 2.0, 3.0 };

		s1 = new VectorSolution(var1,obj1);
		p.addSolution(s1);
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(p);
		assertEquals(1, fronts.size());
		assertEquals(1, fronts.get(0).size());
		assertTrue(s1.equals(fronts.get(0).getSolution(0)));
	}

	// s1 dominates s2;
	@Test
	public void twoSolutionsTwoFrontsTest() {
		VectorSolution s1, s2;

		double[] var1 = { 0.0, 0.0, 0.0 };
		double[] obj1 = { 1.0, 2.0, 3.0 };
		double[] var2 = { 0.0, 0.0, 0.0 };
		double[] obj2 = { 4.0, 4.0, 4.0 };

		s1 = new VectorSolution(var1,obj1);
		s2 = new VectorSolution(var2,obj2);

		p.addSolution(s1);
		p.addSolution(s2);
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(p);
		assertEquals(2, fronts.size());
		assertEquals(1, fronts.get(0).size());
		assertEquals(1, fronts.get(1).size());
		assertTrue(s1.equals(fronts.get(0).getSolution(0)));
		assertTrue(s2.equals(fronts.get(1).getSolution(0)));
	}

	// s1 and s2 are nondominated
	@Test
	public void twoSolutionsOneFrontTest() {
		VectorSolution s1, s2;

		double[] var1 = { 0.0, 0.0, 0.0 };
		double[] obj1 = { 1.0, 2.0, 3.0 };
		double[] var2 = { 0.0, 0.0, 0.0 };
		double[] obj2 = { 2.0, 1.0, 3.0 };

		s1 = new VectorSolution(var1,obj1);
		s2 = new VectorSolution(var2,obj2);

		p.addSolution(s1);
		p.addSolution(s2);
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(p);
		assertEquals(1, fronts.size());
		assertEquals(2, fronts.get(0).size());
	}

	// Front 1: s1=(1,4) s2=(4,1) s3=(2,2) 
	// Front 2: s4=(2,5) s5=(3,3)
	// Front 3: s6=(6,6)
	// Front 4: s7=(6,7)
	@Test
	public void manyFrontsTest() {
		VectorSolution s1, s2, s3, s4, s5, s6, s7;

		double[] var1 = { 0.0, 0.0 };
		double[] obj1 = { 1.0, 4.0 };
		double[] var2 = { 0.0, 0.0 };
		double[] obj2 = { 4.0, 1.0 };
		double[] var3 = { 0.0, 0.0 };
		double[] obj3 = { 2.0, 2.0 };
		double[] var4 = { 0.0, 0.0 };
		double[] obj4 = { 2.0, 5.0 };
		double[] var5 = { 0.0, 0.0 };
		double[] obj5 = { 3.0, 3.0 };
		double[] var6 = { 0.0, 0.0 };
		double[] obj6 = { 6.0, 6.0 };
		double[] var7 = { 0.0, 0.0 };
		double[] obj7 = { 6.0, 7.0 };
		

		s1 = new VectorSolution(var1,obj1);
		s2 = new VectorSolution(var2,obj2);
		s3 = new VectorSolution(var3,obj3);
		s4 = new VectorSolution(var4,obj4);
		s5 = new VectorSolution(var5,obj5);
		s6 = new VectorSolution(var6,obj6);
		s7 = new VectorSolution(var7,obj7);
		

		p.addSolution(s1);
		p.addSolution(s2);
		p.addSolution(s3);
		p.addSolution(s4);
		p.addSolution(s5);
		p.addSolution(s6);
		p.addSolution(s7);
		
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(p);
		
		assertEquals(4, fronts.size());
		assertEquals(3, fronts.get(0).size());
		assertEquals(2, fronts.get(1).size());
		assertEquals(1, fronts.get(2).size());
		assertEquals(1, fronts.get(3).size());
	}

	
	// Front 1: s1=(1,4) s2=(4,1) s3=(2,2) 
		// Front 2: s4=(2,5) s5=(3,3)
		// Front 3: s6=(6,6)
		// Front 4: s7=(6,7)
		@Test
		public void manyFrontsTestDifferentSolutionsOrder() {
			VectorSolution s1, s2, s3, s4, s5, s6, s7;

			
			double[] var7 = { 0.0, 0.0 };
			double[] obj7 = { 6.0, 7.0 };
			double[] var1 = { 0.0, 0.0 };
			double[] obj1 = { 1.0, 4.0 };
			double[] var5 = { 0.0, 0.0 };
			double[] obj5 = { 3.0, 3.0 };
			double[] var2 = { 0.0, 0.0 };
			double[] obj2 = { 4.0, 1.0 };
			double[] var4 = { 0.0, 0.0 };
			double[] obj4 = { 2.0, 5.0 };
			double[] var6 = { 0.0, 0.0 };
			double[] obj6 = { 6.0, 6.0 };
			double[] var3 = { 0.0, 0.0 };
			double[] obj3 = { 2.0, 2.0 };
			
			

			s1 = new VectorSolution(var1,obj1);
			s2 = new VectorSolution(var2,obj2);
			s3 = new VectorSolution(var3,obj3);
			s4 = new VectorSolution(var4,obj4);
			s5 = new VectorSolution(var5,obj5);
			s6 = new VectorSolution(var6,obj6);
			s7 = new VectorSolution(var7,obj7);
			

			p.addSolution(s1);
			p.addSolution(s2);
			p.addSolution(s3);
			p.addSolution(s4);
			p.addSolution(s5);
			p.addSolution(s6);
			p.addSolution(s7);
			
			ArrayList<Population> fronts = NonDominationRanker.sortPopulation(p);
			
			assertEquals(4, fronts.size());
			assertEquals(3, fronts.get(0).size());
			assertEquals(2, fronts.get(1).size());
			assertEquals(1, fronts.get(2).size());
			assertEquals(1, fronts.get(3).size());
		}
}
