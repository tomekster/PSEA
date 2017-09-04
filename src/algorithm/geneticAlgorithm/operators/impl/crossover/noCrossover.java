package algorithm.geneticAlgorithm.operators.impl.crossover;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.Solution;
import algorithm.geneticAlgorithm.operators.CrossoverOperator;

public class noCrossover implements CrossoverOperator {
	@Override
	public ArrayList<Solution> execute(ArrayList<Solution> parents) {
		return parents;
	}
}
