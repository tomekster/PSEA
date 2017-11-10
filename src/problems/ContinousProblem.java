package problems;

import algorithm.geneticAlgorithm.solutions.Solution;
import utils.math.MyRandom;

public abstract class ContinousProblem extends Problem{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8669161789552576411L;
	
	protected double[] lowerBound;
	protected double[] upperBound;
	
	public ContinousProblem(int numVariables, int numObjectives, int numConstraints, String name) {
		super(numVariables, numObjectives, numConstraints, name);
		this.lowerBound = new double[numVariables];
		this.upperBound = new double[numVariables];
		for (int i = 0; i < numVariables; i++) {
			lowerBound[i] = Double.MIN_VALUE;
			upperBound[i] = Double.MAX_VALUE;
		}
		setBoundsOnVariables();
	}

	@Override
	public Solution createSolution() {
		MyRandom random = MyRandom.getInstance();
		double var[] = new double[numVariables];
		double obj[] = new double[numObjectives];
		for(int i=0; i<numVariables; i++){
			var[i] = lowerBound[i] + (upperBound[i] - lowerBound[i]) * random.nextDouble();
		}
		return new Solution(var,obj);
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
