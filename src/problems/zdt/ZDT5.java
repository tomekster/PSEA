package problems.zdt;

//ZDT5.java
//
//Author:
//   Antonio J. Nebro <antonio@lcc.uma.es>
//   Juan J. Durillo <durillo@lcc.uma.es>
//
//Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo

import java.util.BitSet;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import utils.enums.OptimizationType;

/**
 * Class representing problem ZDT5
 */
public class ZDT5 extends ContinousProblem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5843985804357206677L;
	private int[] bitsPerVariable;

	/** Creates a default instance of problem ZDT5 (11 decision variables) */
	public ZDT5() {
		this(11);
	}

	/**
	 * Creates a instance of problem ZDT5
	 *
	 * @param numberOfVariables
	 *            Number of variables.
	 */
	public ZDT5(Integer numberOfVariables) {
		super(numberOfVariables, 2, 0, "ZDT5", OptimizationType.MINIMIZATION);

		bitsPerVariable = new int[numberOfVariables];

		bitsPerVariable[0] = 30;
		for (int var = 1; var < numberOfVariables; var++) {
			bitsPerVariable[var] = 5;
		}
	}

	protected int getBitsPerVariable(int index) {
		if ((index < 0) || (index >= this.getNumVariables())) {
			throw new RuntimeException("Index value is incorrect: " + index);
		}
		return bitsPerVariable[index];
	}

	/** Evaluate() method */
	public void evaluate(BinarySolution solution) {
		double[] f = new double[solution.getNumberOfObjectives()];
		f[0] = 1 + u(solution.getVariableValue(0));
		double g = evalG(solution);
		double h = evalH(f[0], g);
		f[1] = h * g;

		solution.setObjective(0, f[0]);
		solution.setObjective(1, f[1]);
	}

	/**
	 * Returns the value of the ZDT5 function G.
	 *
	 * @param solution
	 *            The solution.
	 */
	public double evalG(BinarySolution solution) {
		double res = 0.0;
		for (int i = 1; i < solution.getNumberOfVariables(); i++) {
			res += evalV(u(solution.getVariableValue(i)));
		}

		return res;
	}

	/**
	 * Returns the value of the ZDT5 function V.
	 *
	 * @param value
	 *            The parameter of V function.
	 */
	public double evalV(double value) {
		if (value < 5.0) {
			return 2.0 + value;
		} else {
			return 1.0;
		}
	}

	/**
	 * Returns the value of the ZDT5 function H.
	 *
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		return 1 / f;
	}

	/**
	 * Returns the u value defined in ZDT5 for a solution.
	 *
	 * @param bitset
	 *            A bitset variable
	 */
	private double u(BitSet bitset) {
		return bitset.cardinality();
	}

	@Override
	public void setBoundsOnVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluate(VectorSolution<Double> solution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Population<Solution> getReferenceFront() {
		// TODO Auto-generated method stub
		return null;
	}
}
