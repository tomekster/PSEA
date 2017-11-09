package problems;

import java.util.ArrayList;
import java.util.Collections;

import algorithm.geneticAlgorithm.Solution;

public abstract class PermutationProblem extends Problem{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8832150494745318938L;

	public PermutationProblem(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name);
	}

	@Override
	public Solution createSolution() {
		ArrayList <Double> permutation = new ArrayList<>();
		for(int i=0; i<numVariables;i++){
			permutation.add((double) i);
		}
		Collections.shuffle(permutation);
		double var[] = new double[numVariables];
		for(int i=0; i<numVariables;i++){
			var[i] = permutation.get(i);
		}
		double obj[] = new double[numObjectives];
		return new Solution(var, obj);
	}
}
