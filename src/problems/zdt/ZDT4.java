package problems.zdt;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import utils.enums.OptimizationType;

//ZDT4.java
//
//Author:
//   Antonio J. Nebro <antonio@lcc.uma.es>
//   Juan J. Durillo <durillo@lcc.uma.es>
//
//Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillos

/**
 * Class representing problem ZDT4
 */
public class ZDT4 extends ContinousProblem {

	/**
	 * Constructor. Creates a default instance of problem ZDT4 (10 decision
	 * variables
	 */
	public ZDT4() {
		this(10);
	}

	/**
	 * Creates a instance of problem ZDT4.
	 *
	 * @param numberOfVariables
	 *            Number of variables.
	 */
	public ZDT4(Integer numberOfVariables) {
		super(numberOfVariables, 2, 0, "ZDT4", OptimizationType.MINIMIZATION);
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
	 * Returns the value of the ZDT4 function G.
	 *
	 * @param solution
	 *            Solution
	 */
	public double evalG(VectorSolution<Double> solution) {
		double g = 0.0;
		for (int var = 1; var < solution.getNumVariables(); var++) {
			g += Math.pow(solution.getVariable(var), 2.0)
					+ -10.0 * Math.cos(4.0 * Math.PI * solution.getVariable(var));
		}

		double constant = 1.0 + 10.0 * (solution.getNumVariables() - 1);
		return g + constant;
	}

	/**
	 * Returns the value of the ZDT4 function H.
	 *
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		return 1.0 - Math.sqrt(f / g);
	}

	@Override
	public void setBoundsOnVariables() {
		setLowerBound(0, 0.0);
		setUpperBound(0, 1.0);
		for (int i = 1; i < getNumVariables(); i++) {
			setLowerBound(i, -5);
			setUpperBound(i, 5);
		}
	}

	@Override
	public Population<Solution> getReferenceFront() {
		throw new RuntimeException("Method not implemented");
	}
}