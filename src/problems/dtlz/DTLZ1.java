package problems.dtlz;

import core.Problem;
import core.points.Solution;

public class DTLZ1 extends Problem {

	// Parameter used in Deb's original paper
	// k = numVariables - numObjectives + 1
	private final static int k = 5;

	public DTLZ1(Integer numObjectives) {
		this(numObjectives + k - 1, numObjectives);
	}

	public DTLZ1(int numVariables, int numObjectives) {
		super(numVariables, numObjectives, 0, "DTLZ1");
	}

	@Override
	public void evaluate(Solution solution) {
		int numberOfVariables = getNumVariables();
		int numberOfObjectives = getNumObjectives();

		double[] x = new double[numberOfVariables];
		double[] f = new double[numberOfObjectives];

		int k = numberOfVariables - numberOfObjectives + 1;

		for (int i = 0; i < numberOfVariables; i++) {
			x[i] = solution.getVariable(i);
		}

		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
		}

		g = 100 * (k + g);
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = (1.0 + g) * 0.5;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= x[j];
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= 1 - x[aux];
			}
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, f[i]);
		}
	}

	@Override
	public void evaluateConstraints(Solution solution) {
		return;
	}

	@Override
	public void setBoundsOnVariables() {
		for (int i = 0; i < getNumVariables(); i++) {
			setLowerBound(i, 0.0);
			setUpperBound(i, 1.0);
		}
	}
}
