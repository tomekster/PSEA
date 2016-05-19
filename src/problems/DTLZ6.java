package problems;

import core.Problem;
import core.Solution;

public class DTLZ6 extends Problem {
	// Parameter used in Deb's original paper
	// k = numVariables - numObjectives + 1
	private final static int k = 10;

	public DTLZ6(Integer numObjectives) {
		this(numObjectives + k - 1, numObjectives);
	}

	public DTLZ6(int numVariables, int numObjectives) {
		super(numVariables, numObjectives, 0, "DTLZ6");
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
			g += java.lang.Math.pow(x[i], 0.1);
		}

		double t = java.lang.Math.PI / (4.0 * (1.0 + g));
		theta[0] = x[0] * java.lang.Math.PI / 2;
		for (int i = 1; i < (numberOfObjectives - 1); i++) {
			theta[i] = t * (1.0 + 2.0 * g * x[i]);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= java.lang.Math.cos(theta[j]);
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= java.lang.Math.sin(theta[aux]);
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
