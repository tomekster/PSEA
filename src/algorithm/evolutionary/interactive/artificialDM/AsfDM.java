package algorithm.evolutionary.interactive.artificialDM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;

import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import utils.math.AsfFunction;

public class AsfDM implements Comparator<Solution>{
	
	private AsfFunction asf;
	private String name;
	private int numViolations;
	private double reward;
	private double penalty;
	
	public AsfDM(AsfFunction asf, String name){
		this.asf= asf;
		this.name = name;
	}
	
	public AsfDM(AsfFunction asf){
		this(asf, "Anonymous");
	}

	public AsfDM copy() {
		return new AsfDM(this.asf.copy(), this.name);
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

	public Solution getBestSolutionVal(Population <? extends Solution> pop){
		Stream<? extends Solution> solutionStream = pop.getSolutions().stream();
		Solution best = solutionStream.reduce((a,b)-> 
		    eval(a) < eval(b) ? a:b
		).get();
		return best;
	}
	
	public double eval(Solution a) {
		return this.asf.eval(a);
	}

	@Override
	public int compare(Solution s1, Solution s2) {
		return Double.compare(eval(s1), eval(s2));
	}
	
	public void sort(ArrayList<? extends Solution> solutions){
		HashMap <Solution, Double> asfValue = new HashMap <Solution, Double>();

		for (Solution s : solutions) {
			asfValue.put(s, eval(s));
		}

		Collections.sort(solutions, new Comparator<Solution>() {
			@Override
			public int compare(final Solution s1, final Solution s2) {
				return Double.compare(asfValue.get(s1), asfValue.get(s2)); // Sort ASCENDING by ASF value
			}
		});
	}
	
	public AsfDM newAdmWithUpdatedLambda(double lambda[]){
		AsfFunction newAsf = this.asf.copy();
		newAsf.setLambda(lambda.clone());
		return new AsfDM(newAsf);
	}
	
	/**
	 * Checks if given DMmodel reproduces correctly all pairwise comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public int verifyModel(ArrayList <Comparison> pc) {
		int numViolations = 0;
		for(Comparison c : pc){
			if( this.compare(c.getBetter(), c.getWorse()) < 0 ){
				numViolations++;
			}
		}
		return numViolations;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}