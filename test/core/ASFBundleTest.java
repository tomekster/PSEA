package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.implementations.psea.AsfPreferenceModel;
import algorithm.implementations.psea.preferences.PreferenceModel;
import utils.math.Geometry;

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
		VectorSolution s1 = new VectorSolution(var, obj1);
		VectorSolution s2 = new VectorSolution(var, obj2);
		VectorSolution s3 = new VectorSolution(var, obj3);

		pop.addSolution(s1);
		pop.addSolution(s2);
		pop.addSolution(s3);

	}

	@Test
	public void buildSolutionRankingTest1() {
		AsfPreferenceModel lambda = new AsfPreferenceModel(dim1);
		ArrayList<VectorSolution> res = PreferenceModel.buildSolutionsRanking(lambda, pop);

		Geometry.assertEqualDoubleArrays(obj3, res.get(0).getObjectives());
		Geometry.assertEqualDoubleArrays(obj1, res.get(1).getObjectives());
		Geometry.assertEqualDoubleArrays(obj2, res.get(2).getObjectives());
	}

	@Test
	public void buildSolutionRankingTest2() {
		AsfPreferenceModel lambda = new AsfPreferenceModel(dim2);
		ArrayList<VectorSolution> res = PreferenceModel.buildSolutionsRanking(lambda, pop);

		Geometry.assertEqualDoubleArrays(obj1, res.get(0).getObjectives());
		Geometry.assertEqualDoubleArrays(obj2, res.get(1).getObjectives());
		Geometry.assertEqualDoubleArrays(obj3, res.get(2).getObjectives());
	}
	
	@Test
	public void bestWorseCVTest(){
		ArrayList<AsfPreferenceModel> newLambdas = new ArrayList<>();
		double dim[] = {0};
		AsfPreferenceModel l1 = new AsfPreferenceModel(dim);
		AsfPreferenceModel l2 = new AsfPreferenceModel(dim);
		l1.setNumViolations(1);
		l2.setNumViolations(2);
		newLambdas.add(l1);
		newLambdas.add(l2);
		assertEquals(1, newLambdas.stream().mapToInt(AsfPreferenceModel::getNumViolations).min().getAsInt());
		assertEquals(2, newLambdas.stream().mapToInt(AsfPreferenceModel::getNumViolations).max().getAsInt());
	}
}
