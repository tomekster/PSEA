package algorithm.geneticAlgorithm.operators;

import java.util.Arrays;

import org.junit.Test;

import algorithm.geneticAlgorithm.operators.impl.mutation.PermutationMutation;
import algorithm.geneticAlgorithm.solutions.VectorSolution;

public class PermutationMutationTest {

	@Test
	public void compareTest() {
		double var[] = {1,2,3,4,5,6};
		double obj[] = new double[0];
		VectorSolution s = new VectorSolution(var, obj);
		PermutationMutation pm = new PermutationMutation();
		pm.execute(s);
		System.out.println(Arrays.toString(s.getVariables()));
	}
}
