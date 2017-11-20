package algorithm.evolutionary.operators.impl.crossover;

import java.util.ArrayList;
import java.util.Random;

import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.evolutionary.operators.CrossoverOperator;
import problems.ContinousProblem;
import utils.math.Geometry;
import utils.math.MyRandom;

public class SBX implements CrossoverOperator <VectorSolution<Double>> {

	// SBX parameters
	private static final double DEFAULT_CROSSOVER_PROBABILITY = 1.0;
	private static final double DEFAULT_ETA_C = 30.0;
	private Random random = MyRandom.getInstance();
	private double crossoverProbability;
	private double crossover_parameter_index;
	int numVariables;
	double[] lowerBound;
	double[] upperBound;

	public SBX(ContinousProblem problem) {
		this(DEFAULT_CROSSOVER_PROBABILITY, DEFAULT_ETA_C, problem.getLowerBounds(), problem.getUpperBounds());
	}
	
	public SBX(double[] lowerBounds, double[] upperBounds) {
		this(DEFAULT_CROSSOVER_PROBABILITY, DEFAULT_ETA_C, lowerBounds, upperBounds);
	}
	
	public SBX(double crossoverProbability, double eta_c, double[] lowerBounds, double[] upperBounds) {
		this.crossoverProbability = crossoverProbability;
		this.crossover_parameter_index = eta_c;
		this.numVariables = lowerBounds.length;
		this.lowerBound = lowerBounds;
		this.upperBound = upperBounds;
	}

	private double find_bethaq(double rand, double alpha) {
		if (rand <= (1.0 / alpha)) {
			return Math.pow((rand * alpha), (1.0 / (crossover_parameter_index + 1.0)));
		} else {
			return Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossover_parameter_index + 1.0)));
		}
	}

	@Override
	public ArrayList<VectorSolution<Double>> execute(ArrayList<VectorSolution<Double>> parents) {
		ArrayList<VectorSolution <Double> > children = new ArrayList <> (2);
		children.add(new VectorSolution <Double> (parents.get(0)));
		children.add(new VectorSolution <Double> (parents.get(1)));

		double p1, p2;
		double rand;
		double lb, ub;
		double betha;
		double alpha;
		double bethaq;
		double c1, c2;

		if (random.nextDouble() > this.crossoverProbability) {
			return children;
		}

		for (int pos = 0; pos < numVariables; pos++) {
			p1 = parents.get(0).getVariable(pos);
			p2 = parents.get(1).getVariable(pos);
			if (random.nextDouble() > 0.5) {
				continue;
			}
			if (Math.abs(p1 - p2) < Geometry.EPS) {
				continue;
			}
			double y1 = Double.min(p1, p2);
			double y2 = Double.max(p1, p2);

			lb = lowerBound[pos];
			ub = upperBound[pos];

			rand = random.nextDouble();

			// C1 part
			betha = 1.0 + (2.0 * (y1 - lb) / (y2 - y1));
			alpha = 2.0 - Math.pow(betha, -(crossover_parameter_index + 1.0));
			bethaq = find_bethaq(rand, alpha);
			c1 = 0.5 * ((y1 + y2) - bethaq * (y2 - y1));

			// C2 part
			betha = 1.0 + (2.0 * (ub - y2) / (y2 - y1));
			alpha = 2.0 - Math.pow(betha, -(crossover_parameter_index + 1.0));
			bethaq = find_bethaq(rand, alpha);
			c2 = 0.5 * ((y1 + y2) + bethaq * (y2 - y1));

			c1 = Double.min(ub, Double.max(lb, c1));
			c2 = Double.min(ub, Double.max(lb, c2));

			if (random.nextDouble() < 0.5) {
				children.get(0).setVariable(pos, c1);
				children.get(1).setVariable(pos, c2);
			} else {
				children.get(0).setVariable(pos, c2);
				children.get(1).setVariable(pos, c1);
			}
		}

		return children;
	}
}
