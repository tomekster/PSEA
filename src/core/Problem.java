package core;

import utils.NSGAIIIRandom;

public abstract class Problem {
	private int numVariables = 0;
	private int numObjectives = 0;
	private int numConstraints = 0;
	private String name = "Abstract Problem";
	private double[] lowerBound;
	private double[] upperBound;

	public Problem(int numVariables, int numObjectives, int numConstraints, String name) {
		this.numVariables = numVariables;
		this.numObjectives = numObjectives;
		this.numConstraints = numConstraints;
		this.name = name;
		this.lowerBound = new double[numVariables];
		this.upperBound = new double[numVariables];
		for (int i = 0; i < numVariables; i++) {
			lowerBound[i] = Double.MIN_VALUE;
			upperBound[i] = Double.MAX_VALUE;
		}
	}

	public Solution createSolution() {
		NSGAIIIRandom random = NSGAIIIRandom.getInstance();
		double var[] = new double[numVariables];
		double obj[] = new double[numObjectives];
		for(int i=0; i<numVariables; i++){
			var[i] = lowerBound[i] + (upperBound[i] - lowerBound[i]) * random.nextDouble();
		}
		Solution s = new Solution(var,obj);
		return s;
	}

	public abstract void evaluate(Solution solution);
	public abstract void evaluate(Population pop);

	// TODO
	public abstract void evaluateConstraints(Solution solution);

	public abstract void setBoundsOnVariables();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getLowerBound() {
		return this.lowerBound;
	}

	public double[] getUpperBound() {
		return this.upperBound;
	}

	public void setLowerBound(int pos, double val) {
		lowerBound[pos] = val;
	}

	public void setUpperBound(int pos, double val) {
		upperBound[pos] = val;
	}

	public int getNumVariables() {
		return numVariables;
	}

	public void setNumVariables(int numVariables) {
		this.numVariables = numVariables;
	}

	public int getNumObjectives() {
		return numObjectives;
	}

	public void setNumObjectives(int numObjectives) {
		this.numObjectives = numObjectives;
	}

	public int getNumConstraints() {
		return numConstraints;
	}

	public void setNumConstraints(int numConstraints) {
		this.numConstraints = numConstraints;
	}

	public double getLowerBound(int pos) {
		return lowerBound[pos];
	}

	public double getUpperBound(int pos) {
		return upperBound[pos];
	}
}
