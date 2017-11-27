package problems.dtlz;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.AsfDM;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.implementations.nsgaiii.hyperplane.Hyperplane;
import algorithm.implementations.nsgaiii.hyperplane.ReferencePoint;
import utils.math.Geometry;

public abstract class DTLZHyperplaneParetoFront extends DTLZ {

	/**
	 * 
	 */
	private static final long serialVersionUID = -135378121240836609L;
	protected static final double HYPERPLANE_CONST = 0.5; //In DTLZ1 problem Pareto Frontier is given by hyperplane: 0.5 = x_1 + x_2 + ... + x_n
	
	public DTLZHyperplaneParetoFront(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name);
	}
	@Override
	public final VectorSolution <Double> getOptimalAsfDmSolution(AsfDM dm) {
		return new VectorSolution <Double> (null, Geometry.lineCrossHyperplanePoint(dm.getAsfFunction().getAsfLine(), HYPERPLANE_CONST).getDim());
	}
	
	@Override
	public Population <Solution> getReferenceFront(){
		Population <Solution> res = new Population<>();
		Hyperplane h = new Hyperplane(getNumObjectives());
		ArrayList <ReferencePoint> rp = h.getReferencePoints();
		for(ReferencePoint r : rp){
			res.addSolution(new VectorSolution <Double>(null, Geometry.normalizeSum(r.getDim(), 0.5)));
		}
		return res;
	}
}
