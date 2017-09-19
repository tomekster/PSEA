package problems.dtlz;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solution.DoubleSolution;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import problems.Problem;

public abstract class DTLZ extends Problem{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2633453132552504662L;

	public DTLZ(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name);
	}

	@Override
	public abstract void evaluate(DoubleSolution solution);

	@Override
	public abstract void evaluateConstraints(DoubleSolution solution);

	@Override
	public Population getReferenceFront(){
		Population res = new Population();
		Hyperplane h = new Hyperplane(getNumObjectives());
		ArrayList <ReferencePoint> rp = h.getReferencePoints();
		for(ReferencePoint r : rp){
			res.addSolution(new DoubleSolution(r.getDim(), getTargetAsfPoint(r.getDim())));
		}
		return res;
	}
}