package operators.impl.mutation;

import java.util.Random;

import core.points.Solution;

/**
 * This class implements a polynomial mutation operator
 *
 * The implementation is based on the NSGA-II code available in
 * http://www.iitk.ac.in/kangal/codes.shtml
 *
 * If the lower and upper bounds of a variable are the same, no mutation is carried out and the
 * bound value is returned.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */

import operators.MutationOperator;
import utils.NSGAIIIRandom;

public class PolynomialMutation implements MutationOperator {

	private double probability;
	private double distributionIndex;
	private double[] lowerBound;
	private double[] upperBound;

	public PolynomialMutation(double probability, double distributionIndex, double[] lowerBound, double[] upperBound) {
		this.probability = probability;
		this.distributionIndex = distributionIndex;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public void execute(Solution solution) {
		double rnd, delta1, delta2, mutPow, deltaq;
		double y, yl, yu, val, xy;

		Random random = NSGAIIIRandom.getInstance();

		for (int i = 0; i < solution.getNumVariables(); i++) {
			if (random.nextDouble() <= probability) {
				y = solution.getVariable(i);
				yl = lowerBound[i];
				yu = upperBound[i];
				
				delta1 = (y - yl) / (yu - yl);
				delta2 = (yu - y) / (yu - yl);
				
				rnd = random.nextDouble();
				
				mutPow = 1.0 / (distributionIndex + 1.0);
				if (rnd <= 0.5) {
					xy = 1.0 - delta1;
					val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
					deltaq = Math.pow(val, mutPow) - 1.0;
				} else {
					xy = 1.0 - delta2;
					val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
					deltaq = 1.0 - Math.pow(val, mutPow);
				}
				y = y + deltaq * (yu - yl);
				y = Double.max(y, lowerBound[i]);
				y = Double.min(y, upperBound[i]);
				solution.setVariable(i, y);
			}
		}
	}
}
