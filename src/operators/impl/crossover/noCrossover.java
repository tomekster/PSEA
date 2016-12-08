package operators.impl.crossover;

import java.util.ArrayList;
import java.util.Random;

import core.points.Solution;
import operators.CrossoverOperator;
import utils.Geometry;
import utils.NSGAIIIRandom;

public class noCrossover implements CrossoverOperator {

	// SBX parameters
	private Random random = NSGAIIIRandom.getInstance();
	private double crossoverProbability;
	private double crossover_parameter_index;
	int numVariables;
	double[] lowerBound;
	double[] upperBound;

	public noCrossover(double crossoverProbability, double eta_c, double[] lowerBound, double[] upperBound) {
		this.crossoverProbability = crossoverProbability;
		this.crossover_parameter_index = eta_c;
		this.numVariables = lowerBound.length;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public ArrayList<Solution> execute(ArrayList<Solution> parents) {
		return parents;
	}

	private double find_bethaq(double rand, double alpha) {
		if (rand <= (1.0 / alpha)) {
			return Math.pow((rand * alpha), (1.0 / (crossover_parameter_index + 1.0)));
		} else {
			return Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossover_parameter_index + 1.0)));
		}
	}
}
