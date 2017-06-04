package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import core.points.ReferencePoint;
import core.points.Solution;
import solutionRankers.SolutionsBordaRanker;
import utils.Geometry;
import utils.MyMath;
import utils.TestingUtils;

public class LambdaTest {

	double var[] = new double[0];
	double obj1[] = { 3, 6 };
	double obj2[] = { 6, 6 };
	double obj3[] = { 7, 3 };
	double dim1[] = { 3, 2 };
	double dim2[] = { 2, 4 };

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
		ReferencePoint lambda = new ReferencePoint(Geometry.dir2point(dim1));
		ArrayList<Solution> res = SolutionsBordaRanker.buildSolutionsRanking(lambda, pop);

		TestingUtils.assertDoubleArrayEquals(obj3, res.get(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj1, res.get(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.get(2).getObjectives());
	}

	@Test
	public void buildSolutionRankingTest2() {
		ReferencePoint lambda = new ReferencePoint(Geometry.dir2point(dim2));
		ArrayList<Solution> res = SolutionsBordaRanker.buildSolutionsRanking(lambda, pop);

		TestingUtils.assertDoubleArrayEquals(obj1, res.get(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.get(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj3, res.get(2).getObjectives());
	}
	
	@Test
	public void bestWorseCVTest(){
		ArrayList<ReferencePoint> newLambdas = new ArrayList<>();
		double dim[] = {0};
		ReferencePoint rp1 = new ReferencePoint(dim);
		ReferencePoint rp2 =new ReferencePoint(dim);
		rp1.setNumViolations(1);
		rp2.setNumViolations(2);
		newLambdas.add(rp1);
		newLambdas.add(rp2);
		assertEquals(1, newLambdas.stream().mapToInt(ReferencePoint::getNumViolations).min().getAsInt());
		assertEquals(2, newLambdas.stream().mapToInt(ReferencePoint::getNumViolations).max().getAsInt());
	}
}
