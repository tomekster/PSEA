package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import core.Population;
import core.Solution;

public class NonDominatedSortTest {

	Population p;

	@Before
	public void init() {
		p = new Population();
	}

	@Test
	public void oneSolutionPopulationTest() {
		Solution s1;

		double[] var1 = { 1.0, 2.0, 3.0 };

		s1 = new Solution(var1);
		p.addSolution(s1);
		ArrayList<Population> fronts = NonDominatedSort.execute(p);
		assertEquals(1, fronts.size());
		assertEquals(1, fronts.get(0).size());
		assertTrue(s1.sameAs(fronts.get(0).getSolution(0)));
	}

	// s1 dominates s2;
	@Test
	public void twoSolutionsTwoFrontsTest() {
		Solution s1, s2;

		double[] var1 = { 1.0, 2.0, 3.0 };
		double[] var2 = { 4.0, 4.0, 4.0 };

		s1 = new Solution(var1);
		s2 = new Solution(var2);

		p.addSolution(s1);
		p.addSolution(s2);
		ArrayList<Population> fronts = NonDominatedSort.execute(p);
		assertEquals(2, fronts.size());
		assertEquals(1, fronts.get(0).size());
		assertEquals(1, fronts.get(1).size());
		assertTrue(s1.sameAs(fronts.get(0).getSolution(0)));
		assertTrue(s2.sameAs(fronts.get(1).getSolution(0)));
	}

	// s1 and s2 are nondominated
	@Test
	public void twoSolutionsOneFrontTest() {
		Solution s1, s2;

		double[] var1 = { 1.0, 2.0, 3.0 };
		double[] var2 = { 2.0, 1.0, 3.0 };

		s1 = new Solution(var1);
		s2 = new Solution(var2);

		p.addSolution(s1);
		p.addSolution(s2);
		ArrayList<Population> fronts = NonDominatedSort.execute(p);
		assertEquals(1, fronts.size());
		assertEquals(2, fronts.get(0).size());
	}

	// Front 1: s1=(1,4) s2=(4,1) s3=(2,2) 
	// Front 2: s4=(2,5) s5=(3,3)
	// Front 3: s6=(6,6)
	// Front 4: s7=(6,7)
	@Test
	public void manyFrontsTest() {
		Solution s1, s2, s3, s4, s5, s6, s7;

		double[] var1 = { 1.0, 4.0 };
		double[] var2 = { 4.0, 1.0 };
		double[] var3 = { 2.0, 2.0 };
		double[] var4 = { 2.0, 5.0 };
		double[] var5 = { 3.0, 3.0 };
		double[] var6 = { 6.0, 6.0 };
		double[] var7 = { 6.0, 7.0 };
		

		s1 = new Solution(var1);
		s2 = new Solution(var2);
		s3 = new Solution(var3);
		s4 = new Solution(var4);
		s5 = new Solution(var5);
		s6 = new Solution(var6);
		s7 = new Solution(var7);
		

		p.addSolution(s1);
		p.addSolution(s2);
		p.addSolution(s3);
		p.addSolution(s4);
		p.addSolution(s5);
		p.addSolution(s6);
		p.addSolution(s7);
		
		ArrayList<Population> fronts = NonDominatedSort.execute(p);
		
		assertEquals(4, fronts.size());
		assertEquals(3, fronts.get(0).size());
		assertEquals(2, fronts.get(1).size());
		assertEquals(1, fronts.get(2).size());
		assertEquals(1, fronts.get(3).size());
	}

}
