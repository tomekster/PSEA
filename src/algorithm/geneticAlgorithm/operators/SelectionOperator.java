package algorithm.geneticAlgorithm.operators;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.Solution;

public interface SelectionOperator {
	public Solution execute(Population <? extends Solution> population);
}