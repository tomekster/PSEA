package algorithm.evolutionary.operators;

import algorithm.evolutionary.solutions.Solution;

public interface MutationOperator <S extends Solution> {

	void execute(S s);

}
