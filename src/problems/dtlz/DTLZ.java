package problems.dtlz;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.ReferencePoint;
import algorithm.nsgaiii.hyperplane.Hyperplane;
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
	public abstract void evaluate(Solution solution);

	@Override
	public abstract void evaluateConstraints(Solution solution);

	@Override
	public Population getReferenceFront(){
		Population res = new Population();
		Hyperplane h = new Hyperplane(this.getNumObjectives());
		ArrayList <ReferencePoint> referencePoints = h.getReferencePoints();
		double var[] = new double[0];
		for(ReferencePoint r : referencePoints){
			res.addSolution(new Solution(var, getTargetPoint(r.getDim())));
		}
		return res;
	}
}