package artificialDM;

import algorithm.geneticAlgorithm.Solution;
import utils.math.Geometry;

public class WeightedSumDM extends ArtificialDM{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2457696723033335587L;
	double weights[];
	
	public WeightedSumDM(double weights[], String name) {
		this.weights = weights;
		this.name = name;
	}
	
	public WeightedSumDM(double weights[]) {
		this(weights, "");
	}
	
	@Override
	public double eval(double[] obj) {
		return Geometry.dot(weights, obj);
	}
	
	@Override
	public int compare(Solution s1, Solution s2) {
		return Double.compare(eval(s1), eval(s2));
	}

	public double[] getWeights() {
		return weights;
	}

	@Override
	public DMType getType() {
		return DMType.WS;
	}
}
