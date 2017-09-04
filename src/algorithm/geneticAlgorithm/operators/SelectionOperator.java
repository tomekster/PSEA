package algorithm.geneticAlgorithm.operators;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;

public interface SelectionOperator {
	public Solution execute(Population population);
}
