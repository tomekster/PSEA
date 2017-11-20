package problems.dtlz;

import algorithm.evolutionary.solutions.VectorSolution;
import utils.math.Geometry;

public class DTLZ3 extends DTLZHypersphereParetoFront{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5215525665727008668L;
	
	private static final double HYPERSPHERE_CONST = 0.5; //In DTLZ 2-4 problems Pareto Frontier is given by hypersphere: 0.5 = sqrt(x_1^2 + x_2^2 + ... + x_n^2)

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
	public void evaluate(VectorSolution <Double> solution) {
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
			g += (x[i] - HYPERSPHERE_CONST) * (x[i] - HYPERSPHERE_CONST) - Math.cos(20.0 * Math.PI * (x[i] - HYPERSPHERE_CONST));
		}

		g = 100.0 * (k + g);
		for (int i = 0; i < numberOfObjectives; i++) {
			f[i] = 1.0 + g;
		}

		for (int i = 0; i < numberOfObjectives; i++) {
			for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
				f[i] *= java.lang.Math.cos(x[j] * HYPERSPHERE_CONST * java.lang.Math.PI);
			}
			if (i != 0) {
				int aux = numberOfObjectives - (i + 1);
				f[i] *= java.lang.Math.sin(x[aux] * HYPERSPHERE_CONST * java.lang.Math.PI);
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
