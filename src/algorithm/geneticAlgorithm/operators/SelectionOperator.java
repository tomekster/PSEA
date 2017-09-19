package algorithm.geneticAlgorithm.operators;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solution.DoubleSolution;

public interface SelectionOperator {
	public DoubleSolution execute(Population population);
}
