package problems.dtlz;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.ReferencePointDm;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.implementations.nsgaiii.hyperplane.Hyperplane;
import algorithm.implementations.nsgaiii.hyperplane.ReferencePoint;
import utils.math.Geometry;

public abstract class DTLZHypersphereParetoFront extends DTLZ{

	/**
	 * 
	 */
	private static final long serialVersionUID = 697121136018051048L;
	protected static final double HYPERSPHERE_PARAM = 0.5; //In DTLZ 2-4 problems Pareto Frontier is given by hypersphere: 1 = sqrt(x_1^2 + x_2^2 + ... + x_n^2)
	
	public DTLZHypersphereParetoFront(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name);
	}
	
	@Override
	public final Solution getOptimalSolution(ReferencePointDm dm) {
		return new Solution(Geometry.lineCrossSpherePoint(dm.getDirection(), HYPERSPHERE_PARAM * 2).getDim());
	}
	
	@Override
	public Population <Solution> getReferenceFront(){
		Population <Solution> res = new Population <> ();
		Hyperplane h = new Hyperplane(getNumObjectives());
		ArrayList <ReferencePoint> rp = h.getReferencePoints();
		for(ReferencePoint r : rp){
			res.addSolution(new VectorSolution<Double>(null, Geometry.vectorNormalize(r.getDim(), HYPERSPHERE_PARAM * 2)));
		}
		return res;
	}
}
