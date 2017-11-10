package algorithm.geneticAlgorithm.operators.impl.crossover;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.solutions.Solution;

public class noCrossover <N extends Number> implements CrossoverOperator <N>{

	@Override
	public ArrayList<Solution<N>> execute(ArrayList<Solution<N>> parents) {
		return parents;
	}
}
