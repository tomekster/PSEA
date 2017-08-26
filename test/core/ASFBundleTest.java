package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import core.points.Lambda;
import core.points.ReferencePoint;
import core.points.Solution;
import solutionRankers.SolutionsBordaRanker;
import utils.Geometry;
import utils.TestingUtils;

public class ASFBundleTest {

	double var[] = new double[0];
	double obj1[] = { 1/0.3, 1/0.6 };
	double obj2[] = { 1/0.6, 1/0.6 };
	double obj3[] = { 1/0.7, 1/0.3 };
	double dim1[] = { 1/0.3, 1/0.2 };
	double dim2[] = { 1/0.2, 1/0.4 };

	Population pop = new Population();

	@Before
	public void init() {
		Solution s1 = new Solution(var, obj1);
		Solution s2 = new Solution(var, obj2);
		Solution s3 = new Solution(var, obj3);

		pop.addSolution(s1);
		pop.addSolution(s2);
		pop.addSolution(s3);

	}

	@Test
	public void buildSolutionRankingTest1() {
		Lambda lambda = new Lambda(dim1);
		ArrayList<Solution> res = SolutionsBordaRanker.buildSolutionsRanking(lambda, pop);

		TestingUtils.assertDoubleArrayEquals(obj3, res.get(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj1, res.get(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.get(2).getObjectives());
	}

	@Test
	public void buildSolutionRankingTest2() {
		Lambda lambda = new Lambda(dim2);
		ArrayList<Solution> res = SolutionsBordaRanker.buildSolutionsRanking(lambda, pop);

		TestingUtils.assertDoubleArrayEquals(obj1, res.get(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.get(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj3, res.get(2).getObjectives());
	}
	
	@Test
	public void bestWorseCVTest(){
		ArrayList<Lambda> newLambdas = new ArrayList<>();
		double dim[] = {0};
		Lambda l1 = new Lambda(dim);
		Lambda l2 = new Lambda(dim);
		l1.setNumViolations(1);
		l2.setNumViolations(2);
		newLambdas.add(l1);
		newLambdas.add(l2);
		assertEquals(1, newLambdas.stream().mapToInt(Lambda::getNumViolations).min().getAsInt());
		assertEquals(2, newLambdas.stream().mapToInt(Lambda::getNumViolations).max().getAsInt());
	}
}
