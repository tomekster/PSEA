package algorithm.geneticAlgorithm.operators.impl.crossover;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.solutions.Solution;

public class noCrossover <S extends Solution> implements CrossoverOperator <S>{

	@Override
	public ArrayList<S> execute(ArrayList<S> parents) {
		return parents;
	}
}
