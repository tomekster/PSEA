package algorithm.geneticAlgorithm.operators;

import algorithm.geneticAlgorithm.solutions.Solution;

public interface MutationOperator <T extends Number> {

	void execute(Solution <T> s);

}
