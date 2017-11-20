package problems;

import algorithm.evolutionary.solutions.VectorSolution;
import utils.enums.OptimizationType;
import utils.math.MyRandom;

public abstract class ContinousProblem extends Problem <VectorSolution<Double>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8669161789552576411L;
	
	protected double[] lowerBound;
	protected double[] upperBound;
	
	public ContinousProblem(int numVariables, int numObjectives, int numConstraints, String name, OptimizationType ot) {
		super(numVariables, numObjectives, numConstraints, name, ot);
		this.lowerBound = new double[numVariables];
		this.upperBound = new double[numVariables];
		for (int i = 0; i < numVariables; i++) {
			lowerBound[i] = Double.MIN_VALUE;
			upperBound[i] = Double.MAX_VALUE;
		}
		setBoundsOnVariables();
	}

	@Override
	public VectorSolution <Double> createSolution() {
		MyRandom random = MyRandom.getInstance();
		Double var[] = new Double[numVariables];
		double obj[] = new double[numObjectives];
		for(int i=0; i<numVariables; i++){
			var[i] = lowerBound[i] + (upperBound[i] - lowerBound[i]) * random.nextDouble();
		}
		return new VectorSolution <Double> (var,obj);
	}

	public abstract void setBoundsOnVariables();
	
	public double[] getLowerBounds() {
		return this.lowerBound;
	}

	public double[] getUpperBounds() {
		return this.upperBound;
	}

	public void setLowerBound(int pos, double val) {
		lowerBound[pos] = val;
	}

	public void setUpperBound(int pos, double val) {
		upperBound[pos] = val;
	}
	
	public double getLowerBound(int pos) {
		return lowerBound[pos];
	}

	public double getUpperBound(int pos) {
		return upperBound[pos];
	}
}
