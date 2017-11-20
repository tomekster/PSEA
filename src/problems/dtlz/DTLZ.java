package problems.dtlz;

import algorithm.evolutionary.solutions.VectorSolution;
import problems.AsfDmProblem;
import problems.ContinousProblem;
import utils.enums.OptimizationType;

public abstract class DTLZ extends ContinousProblem implements AsfDmProblem{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2633453132552504662L;

	public DTLZ(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name, OptimizationType.MINIMIZATION);
	}

	@Override
	public abstract void evaluate(VectorSolution <Double> solution);
}