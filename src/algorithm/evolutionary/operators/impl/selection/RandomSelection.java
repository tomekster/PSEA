package algorithm.evolutionary.operators.impl.selection;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.operators.SelectionOperator;
import utils.math.MyRandom;

public class RandomSelection implements SelectionOperator {
	public Solution execute(Population<? extends Solution> population) {
		return population.getSolution(MyRandom.getInstance().nextInt(population.size()));
	}
}