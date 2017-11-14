package problems.dtlz;

import algorithm.geneticAlgorithm.solutions.VectorSolution;
import utils.math.Geometry;

public class DTLZ4 extends DTLZHypersphereParetoFront{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7691950353740847808L;
	
	private static final double HYPERSPHERE_CONST = 0.5; //In DTLZ 2-4 problems Pareto Frontier is given by hypersphere: 0.5 = sqrt(x_1^2 + x_2^2 + ... + x_n^2)

	// Parameter used in Deb's original paper
	// k = numVariables - numObjectives + 1
	private final static int k = 10;

	public DTLZ4(Integer numObjectives) {
		this(numObjectives + k - 1, numObjectives);
	}

	public DTLZ4(int numVariables, int numObjectives) {
		super(numVariables, numObjectives, 0, "DTLZ4");
	}

	/** Evaluate() method */
	public void evaluate(VectorSolution <Double> solution) {
		int numberOfVariables = getNumVariables();
		int numberOfObjectives = getNumObjectives();
		double[] f = new double[numberOfObjectives];
		double[] x = new double[numberOfVariables];

		double alpha = 100.0;

		for (int i = 0; i < numberOfVariables; i++) {
			x[i] = solution.getVariable(i);
		}

		int k = getNumVariables() - getNumObjectives() + 1;

		double g = 0.0;
		for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
			g += (x[i] - HYPERSPHERE_CONST) * (x[i] - HYPERSPHERE_CONST);
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= java.lang.Math.cos(java.lang.Math.pow(x[j], alpha) * (java.lang.Math.PI / 2.0));
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= java.lang.Math.sin(java.lang.Math.pow(x[aux], alpha) * (java.lang.Math.PI / 2.0));
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
