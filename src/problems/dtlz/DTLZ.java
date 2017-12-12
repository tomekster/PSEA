package problems.dtlz;

import algorithm.evolutionary.solutions.VectorSolution;
import problems.KnowsOptimalAsfSolution;
import problems.ContinousProblem;
import utils.enums.OptimizationType;
import utils.math.structures.Point;

public abstract class DTLZ extends ContinousProblem implements KnowsOptimalAsfSolution{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2633453132552504662L;

	public DTLZ(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name, OptimizationType.MINIMIZATION);
	}

	@Override
	public abstract void evaluate(VectorSolution <Double> solution);
	
	@Override
	public Point getTrueIdealPoint() {
		return new Point(numObjectives);
	}
}