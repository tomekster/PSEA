package problems.dtlz;

import algorithm.geneticAlgorithm.solutions.VectorSolution;
import utils.math.Geometry;

public class DTLZ2 extends DTLZHypersphereParetoFront {
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
	public void evaluate(VectorSolution <Double> solution) {
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
			g += (x[i] - HYPERSPHERE_RADIUS) * (x[i] - HYPERSPHERE_RADIUS);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= Math.cos(x[j] * HYPERSPHERE_RADIUS * Math.PI);
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= Math.sin(x[aux] * HYPERSPHERE_RADIUS * Math.PI);
			}
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			solution.setObjective(i, f[i]);
		}
		assert Geometry.getLen(solution.getObjectives()) >= 1;
	}

	@Override
	public void setBoundsOnVariables() {
		for (int i = 0; i < getNumVariables(); i++) {
			setLowerBound(i, 0.0);
			setUpperBound(i, 1.0);
		}
	}
}
