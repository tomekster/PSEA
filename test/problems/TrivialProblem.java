package problems;

import core.Problem;
import core.points.Solution;

public class TrivialProblem extends Problem {

	public TrivialProblem(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name);
	}
	
	public TrivialProblem(){
		this(2,2,0,"TRIVIAL_PROBLEM");
	}

	@Override
	public void evaluate(Solution solution) {
		return;
	}

	@Override
	public void evaluateConstraints(Solution solution) {
		return;
	}
	
}
