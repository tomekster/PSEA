package algorithm.geneticAlgorithm.operators.impl.crossover;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.solution.DoubleSolution;
import algorithm.geneticAlgorithm.solution.Solution;
import utils.math.structures.Pair;

public class noCrossover implements CrossoverOperator {
	@Override
	public Pair<DoubleSolution, DoubleSolution> execute(Pair<DoubleSolution, DoubleSolution> parents) {
		return parents;
	}
}
