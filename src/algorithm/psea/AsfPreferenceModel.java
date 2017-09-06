package algorithm.psea;

import java.io.Serializable;

import algorithm.geneticAlgorithm.Solution;

public class AsfPreferenceModel extends Solution implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1962134424334714602L;
	private double reward;
	private double penalty;
	private int numViolations;

	public AsfPreferenceModel(int numDim) {
		super(new double[1], new double [numDim]);
	}

	public AsfPreferenceModel(AsfPreferenceModel rp) {
		this(rp.getNumDimensions());
		this.obj = rp.getLambda().clone();
	}
	
	public AsfPreferenceModel(double []dimensions) {
		this(dimensions.length);
		this.obj = dimensions.clone();
	}

	public int getNumDimensions() {
		return obj.length;
	}

	public double[] getLambda() {
		return obj;
	}
	
	public double getLambda(int i){
		return getObjective(i);
	}
	
	public void setDim(int i, double val){
		setObjective(i, val);
	}
	
	public boolean isCoherent() {
		return numViolations == 0;
	}

	public AsfPreferenceModel copy() {
		AsfPreferenceModel rp = new AsfPreferenceModel(this);
		return rp;
	}

	public double getReward(){
		return this.reward;
	}
	
	public void setReward(double reward) {
		this.reward = reward;
	}

	public double getPenalty(){
		return this.penalty;
	}
	
	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	public int getNumViolations(){
		return this.numViolations;
	}
	
	public void setNumViolations(int numViolations) {
		this.numViolations = numViolations;
	}

	public void setDim(double[] q) {
		obj = q.clone();
	}

	public void incrDim(int pos, double d) {
		obj[pos] += d; 
	}
}
