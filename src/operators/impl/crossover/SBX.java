package operators.impl.crossover;

import java.util.ArrayList;
import java.util.Random;

import core.points.Solution;
import operators.CrossoverOperator;
import utils.Geometry;
import utils.NSGAIIIRandom;

public class SBX implements CrossoverOperator {

	// SBX parameters
	private Random random = NSGAIIIRandom.getInstance();
	private double crossoverProbability;
	private double crossover_parameter_index;
	int numVariables;
	double[] lowerBound;
	double[] upperBound;

	public SBX(double crossoverProbability, double eta_c, double[] lowerBound, double[] upperBound) {
		this.crossoverProbability = crossoverProbability;
		this.crossover_parameter_index = eta_c;
		this.numVariables = lowerBound.length;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public ArrayList<Solution> execute(ArrayList<Solution> parents) {

		ArrayList<Solution> children = new ArrayList<Solution>(2);
		children.add(new Solution(parents.get(0)));
		children.add(new Solution(parents.get(1)));

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

	private double find_bethaq(double rand, double alpha) {
		if (rand <= (1.0 / alpha)) {
			return Math.pow((rand * alpha), (1.0 / (crossover_parameter_index + 1.0)));
		} else {
			return Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossover_parameter_index + 1.0)));
		}
	}
}
