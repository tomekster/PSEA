package problems.dtlz;

import core.Problem;
import core.points.Solution;

public class DTLZ3 extends Problem {
	// Parameter used in Deb's original paper
	// k = numVariables - numObjectives + 1
	private final static int k = 10;

	public DTLZ3(Integer numObjectives) {
		this(numObjectives + k - 1, numObjectives);
	}
	
	public DTLZ3(int numVariables, int numObjectives) {
		super(numVariables, numObjectives, 0, "DTLZ3");
	}

	/** Evaluate() method */
	public void evaluate(Solution solution) {
		int numberOfVariables = getNumVariables();
		int numberOfObjectives = getNumObjectives();
		double[] f = new double[numberOfObjectives];
		double[] x = new double[numberOfVariables];

		for (int i = 0; i < numberOfVariables; i++) {
			x[i] = solution.getVariable(i);
		}

		int k = numberOfVariables - numberOfObjectives + 1;
		
		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
		}

		g = 100.0 * (k + g);
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= java.lang.Math.cos(x[j] * 0.5 * java.lang.Math.PI);
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= java.lang.Math.sin(x[aux] * 0.5 * java.lang.Math.PI);
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
