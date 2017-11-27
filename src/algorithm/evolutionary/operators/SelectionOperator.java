package algorithm.evolutionary.operators;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;

public interface SelectionOperator{
	public Solution execute(Population <? extends Solution> population);
}