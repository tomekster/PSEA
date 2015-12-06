package operators.impl.crossover;

import java.util.ArrayList;
import java.util.Random;

import core.Solution;
import operators.CrossoverOperator;
import utils.Compare;
import utils.NSGAIIIRandom;

public class SBX implements CrossoverOperator {

	// SBX parameters
	private Random random = NSGAIIIRandom.getInstance();
	private double crossoverProbability;
	private double crossover_parameter_index;

	public SBX(double crossoverProbability, double eta_c) {
		this.crossoverProbability = crossoverProbability;
		this.crossover_parameter_index = eta_c;
	}

	@Override
	public ArrayList<Solution> execute(ArrayList<Solution> parents) {

		int numVariables = parents.get(0).getNumVariables();
		double[] lowerBound = parents.get(0).getLowerBound();
		double[] upperBound = parents.get(0).getUpperBound();

		ArrayList<Solution> children = new ArrayList<Solution>(2);
		children.add(new Solution(parents.get(0)));
		children.add(new Solution(parents.get(1)));

		double p1, p2;
		double rand;
		double p_min, p_max;
		double betha;
		double alpha;
		double bethaq;
		double c1, c2;

		if (random.nextDouble() < this.crossoverProbability) {
			for (int pos = 0; pos < numVariables; pos++) {
				p1 = parents.get(0).getVariable(pos);
				p2 = parents.get(1).getVariable(pos);
				if (random.nextDouble() <= 0.5) {
					if (Compare.compareDouble(p1, p2) == 0) {
						children.get(0).setVariable(pos, p1);
						children.get(1).setVariable(pos, p2);
					} else {
						if (p1 > p2) {
							double temp = p1;
							p1 = p2;
							p2 = temp;
						}

						rand = random.nextDouble();
						p_min = lowerBound[pos];
						p_max = upperBound[pos];

						// C1 part
						betha = 1.0 + (2.0 * (p1 - p_min) / (p2 - p1));
						alpha = 2.0 - Math.pow(betha, -(crossover_parameter_index + 1.0));
						if (rand <= (1.0 / alpha)) {
							bethaq = Math.pow((rand * alpha), (1.0 / (crossover_parameter_index + 1.0)));
						} else {
							bethaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossover_parameter_index + 1.0)));
						}
						c1 = 0.5 * ((p1 + p2) - bethaq * (p2 - p1));

						// C2 part
						betha = 1.0 + (2.0 * (p_max - p2) / (p2 - p1));
						alpha = 2.0 - Math.pow(betha, -(crossover_parameter_index + 1.0));
						if (rand <= (1.0 / alpha)) {
							bethaq = Math.pow((rand * alpha), (1.0 / (crossover_parameter_index + 1.0)));
						} else {
							bethaq = Math.pow((1.0 / (2.0 - rand * alpha)), (1.0 / (crossover_parameter_index + 1.0)));
						}
						c2 = 0.5 * ((p1 + p2) + bethaq * (p2 - p1));

						// Fix boundaries
						if (c1 < p_min)
							c1 = p_min;
						if (c2 < p_min)
							c2 = p_min;
						if (c1 > p_max)
							c1 = p_max;
						if (c2 > p_max)
							c2 = p_max;

						if (random.nextDouble() < 0.5) {
							children.get(0).setVariable(pos, c1);
							children.get(1).setVariable(pos, c2);
						} else {
							children.get(0).setVariable(pos, c2);
							children.get(1).setVariable(pos, c1);
						}
					}
				}
			}
		}

		return children;
	}
}
