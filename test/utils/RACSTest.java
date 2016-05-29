package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import core.Population;
import core.Solution;
import preferences.Comparison;
import preferences.PreferenceCollector;
public class RACSTest {

	Population p;

	@Before
	public void init() {
		p = new Population();
	}

	@Test
	public void RATSLPTest() {
		Solution s1, s2, s3, s4;

		double[] var = { 0, 0};
		double[] obj1 = { 3, 7};
		double[] obj2 = { 5, 5};
		double[] obj3 = { 7, 6};
		double[] obj4 = { 10, 2};

		s1 = new Solution(var,obj1);
		s2 = new Solution(var,obj2);
		s3 = new Solution(var,obj3);
		s4 = new Solution(var,obj4);
		p.addSolution(s1);
		p.addSolution(s2);
		p.addSolution(s3);
		p.addSolution(s4);
		
		double lambda[] = {0.2,0.2}; 
		
		PreferenceCollector pc = new PreferenceCollector();
		pc.addComparison(s1, s3);
		
		double EPS = RACS.RATSLP(lambda, pc);
		System.out.println("EPS = " + EPS);
		assertTrue(EPS > 1E-9);
	}
	
	@Test
	public void RATSLP2Test() {
		Solution s1, s2, s3, s4;

		double[] var = { 0, 0};
		double[] obj1 = { 3, 7};
		double[] obj2 = { 5, 5};
		double[] obj3 = { 7, 6};
		double[] obj4 = { 10, 2};

		s1 = new Solution(var,obj1);
		s2 = new Solution(var,obj2);
		s3 = new Solution(var,obj3);
		s4 = new Solution(var,obj4);
		p.addSolution(s1);
		p.addSolution(s2);
		p.addSolution(s3);
		p.addSolution(s4);
		
		double lambda[] = {0.2,0.2}; 
		
		PreferenceCollector pc = new PreferenceCollector();
		pc.addComparison(s3, s2);
		
		double EPS = RACS.RATSDominationLP(lambda, pc, p, -1);
		System.out.println("EPS = " + EPS);
		assertTrue(EPS < 0);
	}
	
	@Test
	public void RATSLP3Test() {
		Solution s1, s2, s3, s4;

		double[] var = { 0, 0};
		double[] obj1 = { 3, 7};
		double[] obj2 = { 5, 5};
		double[] obj3 = { 7, 6};
		double[] obj4 = { 10, 2};

		s1 = new Solution(var,obj1);
		s2 = new Solution(var,obj2);
		s3 = new Solution(var,obj3);
		s4 = new Solution(var,obj4);
		p.addSolution(s1);
		p.addSolution(s2);
		p.addSolution(s3);
		p.addSolution(s4);
		
		double lambda[] = {0.2,0.2}; 
		
		PreferenceCollector pc = new PreferenceCollector();
		pc.addComparison(s1, s2);
		pc.addComparison(s2, s4);
		pc.addComparison(s4, s1);
		
		double EPS = RACS.RATSLP(lambda, pc);
		System.out.println("EPS = " + EPS);
		assertTrue(EPS < 0);
	}
}
