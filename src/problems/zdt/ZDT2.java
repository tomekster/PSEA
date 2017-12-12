package problems.zdt;
//ZDT2.java
//
//Author:
//   Antonio J. Nebro <antonio@lcc.uma.es>
//   Juan J. Durillo <durillo@lcc.uma.es>
//
//Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import utils.enums.OptimizationType;

/** Class representing problem ZDT2 */
public class ZDT2 extends ContinousProblem {

	private static final long serialVersionUID = 6104333614256756571L;

	/**
	 * Constructor. Creates default instance of problem ZDT2 (30 decision
	 * variables)
	 */
	public ZDT2() {
		this(30);
	}

	/**
	 * Constructor. Creates a new ZDT2 problem instance.
	 *
	 * @param numberOfVariables
	 *            Number of variables
	 */
	public ZDT2(Integer numberOfVariables) {
		super(numberOfVariables, 2, 0, "ZDT2", OptimizationType.MINIMIZATION);
		setName("ZDT2");
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
	 * Returns the value of the ZDT2 function G.
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
	 * Returns the value of the ZDT2 function H.
	 *
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		double h;
		h = 1.0 - Math.pow(f / g, 2.0);
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
