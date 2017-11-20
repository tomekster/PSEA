package algorithm.evolutionary.interactive.artificialDM;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Stream;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;

public abstract class ArtificialDM implements Serializable, Comparator<Solution>{
	/**
	 * 
	 */
	protected String name;
	protected int numViolations;
	
	private static final long serialVersionUID = -1962640459115190637L;
	
	public double eval(Solution s){
		return eval(s.getObjectives());
	}
	public abstract double eval(double obj[]);
	
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
	
	public Solution getBestSolutionVal(Population <? extends Solution> pop){
		Stream<? extends Solution> solutionStream = pop.getSolutions().stream();
		Solution best = solutionStream.reduce((a,b)-> 
		    eval(a) < eval(b) ? a:b
		).get();
		return best;
	}
}
