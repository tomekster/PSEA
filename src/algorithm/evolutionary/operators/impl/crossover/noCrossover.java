package algorithm.evolutionary.operators.impl.crossover;

import java.util.ArrayList;

import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.operators.CrossoverOperator;

public class noCrossover <S extends Solution> implements CrossoverOperator <S>{

	@Override
	public ArrayList<S> execute(ArrayList<S> parents) {
		return parents;
	}
}
