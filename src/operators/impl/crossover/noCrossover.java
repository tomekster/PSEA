package operators.impl.crossover;

import java.util.ArrayList;

import core.points.Solution;
import operators.CrossoverOperator;

public class noCrossover implements CrossoverOperator {
	@Override
	public ArrayList<Solution> execute(ArrayList<Solution> parents) {
		return parents;
	}
}
