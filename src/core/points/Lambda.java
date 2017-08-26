package core.points;

import java.io.Serializable;

public class Lambda extends Solution implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1962134424334714602L;
	private double reward;
	private double penalty;
	private int numViolations;

	public Lambda(int numDim) {
		super(new double[1], new double [numDim]);
	}

	public Lambda(Lambda rp) {
		this(rp.getNumDimensions());
		this.obj = rp.getDim().clone();
	}
	
	public Lambda(double []dimensions) {
		this(dimensions.length);
		this.obj = dimensions.clone();
	}

	public int getNumDimensions() {
		return obj.length;
	}

	public double[] getDim() {
		return obj;
	}
	
	public double getDim(int i){
		return getObjective(i);
	}
	
	public void setDim(int i, double val){
		setObjective(i, val);
	}
	
	public boolean isCoherent() {
		return numViolations == 0;
	}

	public Lambda copy() {
		Lambda rp = new Lambda(this);
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
