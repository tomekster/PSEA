package problems;

import core.Problem;
import core.Solution;

public class DTLZ7 extends Problem {
	// Parameter used in Deb's original paper
	// k = numVariables - numObjectives + 1
	private final static int k = 10;

	public DTLZ7(Integer numObjectives) {
		this(numObjectives + k - 1, numObjectives);
	}

	public DTLZ7(int numVariables, int numObjectives) {
		super(numVariables, numObjectives, 0, "DTLZ7");
		setBoundsOnVariables();
	}

	/** Evaluate() method */
	public void evaluate(Solution solution) {
		int numberOfVariables = getNumVariables();
		int numberOfObjectives = getNumObjectives();
		double[] theta = new double[numberOfObjectives - 1];

		double[] f = new double[numberOfObjectives];
		double[] x = new double[numberOfVariables];

		int k = getNumVariables() - getNumObjectives() + 1;

		for (int i = 0; i < numberOfVariables; i++) {
			x[i] = solution.getVariable(i);
		}

		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += x[i];
		}

		g = 1 + (9.0 * g) / k;

		System.arraycopy(x, 0, f, 0, numberOfObjectives - 1);

		double h = 0.0;
		for (int i = 0; i < numberOfObjectives - 1; i++) {
			h += (f[i] / (1.0 + g)) * (1 + Math.sin(3.0 * Math.PI * f[i]));
		}

		h = numberOfObjectives - h;

		f[numberOfObjectives - 1] = (1 + g) * h;

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
