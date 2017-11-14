package algorithm.geneticAlgorithm.operators;

import algorithm.geneticAlgorithm.solutions.Solution;

public interface MutationOperator <S extends Solution> {

	void execute(S s);

}
