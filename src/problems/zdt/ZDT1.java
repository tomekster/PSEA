package problems.zdt;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import utils.enums.OptimizationType;

/** Class representing problem ZDT1 */
public class ZDT1 extends ContinousProblem {

	private static final long serialVersionUID = 6601083302864319741L;

	/**
	 * Constructor. Creates default instance of problem ZDT1 (30 decision
	 * variables)
	 */
	public ZDT1() {
		this(30);
	}

	/**
	 * Creates a new instance of problem ZDT1.
	 *
	 * @param numberOfVariables
	 *            Number of variables.
	 */
	public ZDT1(Integer numberOfVariables) {
		super(numberOfVariables, 2, 0, "ZDT1", OptimizationType.MINIMIZATION);
		setName("ZDT1");
	}

	/** Evaluate() method */
	public void evaluate(VectorSolution<Double> solution) {
		double[] f = new double[getNumObjectives()];

		f[0] = solution.getVariable(0);
		double g = this.evalG(solution);
		double h = this.evalH(f[0], g);
		f[1] = h * g;

		solution.setObjective(0, f[0]);
		solution.setObjective(1, f[1]);
	}

	/**
	 * Returns the value of the ZDT1 function G.
	 *
	 * @param solution
	 *            Solution
	 */
	private double evalG(VectorSolution<Double> solution) {
		double g = 0.0;
		for (int i = 1; i < solution.getNumVariables(); i++) {
			g += solution.getVariable(i);
		}
		double constant = 9.0 / (solution.getNumVariables() - 1);
		g = constant * g;
		g = g + 1.0;
		return g;
	}

	/**
	 * Returns the value of the ZDT1 function H.
	 *
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		double h;
		h = 1.0 - Math.sqrt(f / g);
		return h;
	}

	@Override
	public void setBoundsOnVariables() {
		for (int i = 0; i < getNumVariables(); i++) {
			setLowerBound(i, 0.0);
			setUpperBound(i, 1.0);
		}
	}

	@Override
	public Population<Solution> getReferenceFront() {
		throw new RuntimeException("Method not implemented");
	}
}