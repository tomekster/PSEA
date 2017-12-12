package problems.zdt;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import utils.enums.OptimizationType;

//ZDT6.java
//
//Author:
//   Antonio J. Nebro <antonio@lcc.uma.es>
//   Juan J. Durillo <durillo@lcc.uma.es>
//
//Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo

/**
 * Class representing problem ZDT6
 */
public class ZDT6 extends ContinousProblem {

	private static final long serialVersionUID = 368627115987141328L;

	/**
	 * Constructor. Creates a default instance of problem ZDT6 (10 decision
	 * variables)
	 */
	public ZDT6() {
		this(10);
	}

	/**
	 * Creates a instance of problem ZDT6
	 *
	 * @param numberOfVariables
	 *            Number of variables
	 */
	public ZDT6(Integer numberOfVariables) {
		super(numberOfVariables, 2, 0, "ZDT6", OptimizationType.MINIMIZATION);
	}

	/** Evaluate() method */
	public void evaluate(VectorSolution<Double> solution) {
		double[] f = new double[numObjectives];

		double x1 = solution.getVariable(0);
		f[0] = 1.0 - Math.exp((-4.0) * x1) * Math.pow(Math.sin(6.0 * Math.PI * x1), 6.0);
		double g = this.evalG(solution);
		double h = this.evalH(f[0], g);
		f[1] = h * g;

		solution.setObjective(0, f[0]);
		solution.setObjective(1, f[1]);
	}

	/**
	 * Returns the value of the ZDT6 function G.
	 *
	 * @param solution
	 *            Solution
	 */
	public double evalG(VectorSolution<Double> solution) {
		double g = 0.0;
		for (int var = 1; var < solution.getNumVariables(); var++) {
			g += solution.getVariable(var);
		}
		g = g / (solution.getNumVariables() - 1);
		g = Math.pow(g, 0.25);
		g = 9.0 * g;
		g = 1.0 + g;
		return g;
	}

	/**
	 * Returns the value of the ZDT6 function H.
	 *
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		return 1.0 - Math.pow((f / g), 2.0);
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
