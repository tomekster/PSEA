package algorithm.geneticAlgorithm.operators;

import algorithm.geneticAlgorithm.solution.DoubleSolution;
import utils.math.structures.Pair;

public interface CrossoverOperator {
	Pair<DoubleSolution, DoubleSolution> execute(Pair<DoubleSolution, DoubleSolution> parents);
}
