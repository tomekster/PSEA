package problems.dtlz;

import java.util.Arrays;

import algorithm.geneticAlgorithm.Solution;
import utils.math.Geometry;

public class DTLZ2 extends DTLZ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7633040782649679192L;
	// Parameter used in Deb's original paper
	// k = numVariables - numObjectives + 1
	private final static int k = 10;

	public DTLZ2(Integer numObjectives) {
		this(numObjectives + k - 1, numObjectives);
	}

	public DTLZ2(Integer numVariables, int numObjectives) {
		super(numVariables, numObjectives, 0, "DTLZ2");
	}

	/** Evaluate() method */
	public void evaluate(Solution solution) {
		int numberOfVariables = getNumVariables();
		int numberOfObjectives = getNumObjectives();

		double[] f = new double[numberOfObjectives];
		double[] x = new double[numberOfVariables];

		int k = numberOfVariables - numberOfObjectives + 1;

		for (int i = 0; i < numberOfVariables; i++) {
			x[i] = solution.getVariable(i);
		}

		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += (x[i] - 0.5) * (x[i] - 0.5);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= Math.cos(x[j] * 0.5 * Math.PI);
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= Math.sin(x[aux] * 0.5 * Math.PI);
			}
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, f[i]);
		}
		assert Geometry.getLen(solution.getObjectives()) >= 1;
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
