package problems;

import java.util.ArrayList;
import java.util.Collections;

import algorithm.evolutionary.solutions.VectorSolution;
import utils.enums.OptimizationType;

public abstract class PermutationProblem extends Problem <VectorSolution <Integer>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8832150494745318938L;

	public PermutationProblem(int numVariables, int numObjectives, int numConstraints, String name, OptimizationType ot) {
		super(numVariables, numObjectives, numConstraints, name, ot);
	}

	@Override
	public VectorSolution <Integer> createSolution() {
		ArrayList <Integer> permutation = new ArrayList<>();
		for(int i=0; i<numVariables;i++){
			permutation.add(i);
		}
		Collections.shuffle(permutation);
		Integer var[] = new Integer[numVariables];
		for(int i=0; i<numVariables;i++){
			var[i] = permutation.get(i);
		}
		double obj[] = new double[numObjectives];
		return new VectorSolution <Integer>(var, obj);
	}
}
