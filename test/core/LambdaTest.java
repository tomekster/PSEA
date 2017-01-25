package core;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import core.points.ReferencePoint;
import core.points.Solution;
import utils.Geometry;
import utils.TestingUtils;

import static org.junit.Assert.*;

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
		ReferencePoint lambda = new ReferencePoint(dim1);
		ArrayList<Solution> res = Lambda.buildSolutionsRanking(lambda, pop);

		TestingUtils.assertDoubleArrayEquals(obj3, res.get(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj1, res.get(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.get(2).getObjectives());
	}

	@Test
	public void buildSolutionRankingTest2() {
		ReferencePoint lambda = new ReferencePoint(dim2);
		ArrayList<Solution> res = Lambda.buildSolutionsRanking(lambda, pop);

		TestingUtils.assertDoubleArrayEquals(obj1, res.get(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.get(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj3, res.get(2).getObjectives());
	}
	
//	@Test
//	public void selectKSolutionsByChebyshevBordaRankingTest(){
//		Population lambdas = new Population();
//		lambdas.addSolution(new ReferencePoint(dim1));
//		lambdas.addSolution(new ReferencePoint(dim2));
//		Lambda LAMBDA = new Lambda(2, 10);
//		LAMBDA.setPopulation(lambdas);
//		Population res;
//		res = LAMBDA.selectKSolutionsByChebyshevBordaRanking(pop, 1);
//		assertEquals(1, res.size());
//		TestingUtils.assertDoubleArrayEquals(obj1, res.getSolution(0).getObjectives());
//		
//		res = LAMBDA.selectKSolutionsByChebyshevBordaRanking(pop, 2);
//		assertEquals(2, res.size());
//		TestingUtils.assertDoubleArrayEquals(obj1, res.getSolution(0).getObjectives());
//		TestingUtils.assertDoubleArrayEquals(obj3, res.getSolution(1).getObjectives());
//		
//		res = LAMBDA.selectKSolutionsByChebyshevBordaRanking(pop, 3);
//		assertEquals(3, res.size());
//		TestingUtils.assertDoubleArrayEquals(obj1, res.getSolution(0).getObjectives());
//		TestingUtils.assertDoubleArrayEquals(obj3, res.getSolution(1).getObjectives());
//		TestingUtils.assertDoubleArrayEquals(obj2, res.getSolution(2).getObjectives());
//	}
	
	public void selectKSolutionsByChebyshevBordaRankingTest(){
		ArrayList<ReferencePoint> lambdas = new ArrayList<ReferencePoint>();
		lambdas.add(new ReferencePoint(dim1));
		lambdas.add(new ReferencePoint(dim2));
		Lambda LAMBDA = new Lambda(2, 10);
		LAMBDA.setLambdas(lambdas);
		Population res;
		res = LAMBDA.selectKSolutionsByChebyshevBordaRanking(pop, 1);
		assertEquals(1, res.size());
		TestingUtils.assertDoubleArrayEquals(obj1, res.getSolution(0).getObjectives());
		
		res = LAMBDA.selectKSolutionsByChebyshevBordaRanking(pop, 2);
		assertEquals(2, res.size());
		TestingUtils.assertDoubleArrayEquals(obj1, res.getSolution(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj3, res.getSolution(1).getObjectives());
		
		res = LAMBDA.selectKSolutionsByChebyshevBordaRanking(pop, 3);
		assertEquals(3, res.size());
		TestingUtils.assertDoubleArrayEquals(obj1, res.getSolution(0).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj3, res.getSolution(1).getObjectives());
		TestingUtils.assertDoubleArrayEquals(obj2, res.getSolution(2).getObjectives());
	}
	
	public void getTotalPCGradientTest(){
		Lambda L = new Lambda(2, 1);
		double vars[] = {0};
		double obj1[] = {1,2};
		double obj2[] = {2,1};
		L.getPreferenceCollector().addComparison(new Solution(vars, obj1), new Solution(vars, obj2));
		double rp[] = {0.5, 0.5};
		ReferencePoint lambda = new ReferencePoint(rp);
		double grad[] = L.getTotalPCGradient(lambda);
		assertEquals(2.00122568062, grad[0], Geometry.EPS);
		assertEquals(-2.00122568062, grad[1], Geometry.EPS);
	}
}
