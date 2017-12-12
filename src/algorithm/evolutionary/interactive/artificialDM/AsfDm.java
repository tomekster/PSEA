package algorithm.evolutionary.interactive.artificialDM;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Solution;
import utils.math.AsfFunction;
import utils.math.structures.Point;

public class AsfDm extends RferencePointDm{
	
	private AsfFunction asf;
	private int numViolations;
	
	private double reward;
	private double penalty;
	
	public AsfDm(AsfFunction asf, String name){
		this.asf= asf;
		this.name = name;
	}
	
	public AsfDm(AsfFunction asf){
		this(asf, "Anonymous");
	}

	public AsfDm copy() {
		return new AsfDm(this.asf.copy(), this.name);
	}

	public Point getRefPoint(){
		return asf.getRefPoint();
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
	
	public AsfDm newAsfDmWithUpdatedLambda(double lambda[]){
		AsfFunction newAsf = this.asf.copy();
		newAsf.setLambda(lambda.clone());
		return new AsfDm(newAsf);
	}
	
	/**
	 * Checks if given DMmodel reproduces correctly all pairwise comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public int verifyModel(ArrayList <Comparison> pc) {
		numViolations = 0;
		for(Comparison c : pc){
			if( this.compare(c.getBetter(), c.getWorse()) >= 0 ){
				numViolations++;
			}
		}
		return numViolations;
	}
	
	public int getNumViolations(){
		return this.numViolations;
	}
	
	public void setNumViolations(int numViolations) {
		this.numViolations = numViolations;
	}
	
	public boolean isViolating() {
		return numViolations == 0;
	}
	
	public AsfFunction getAsfFunction(){
		return this.asf;
	}

	@Override
	public double eval(Solution a) {
		return asf.eval(a.getObjectives());
	}

	@Override
	public Point getReferencePoint() {
		return this.getAsfFunction().getRefPoint();
	}
}