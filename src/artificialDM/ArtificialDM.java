package artificialDM;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Stream;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;

public abstract class ArtificialDM implements Serializable, Comparator<Solution>{
	/**
	 * 
	 */
	protected String name;
	
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
	
	public Solution getBestSolutionVal(Population pop){
		Stream<Solution> solutionStream = pop.getSolutions().stream();
		Solution best = solutionStream.reduce((a,b)-> 
		    eval(a) < eval(b) ? a:b
		).get();
		return best;
	}
	
	@Override
	public int compare(Solution s1, Solution s2) {
		return Double.compare(eval(s1), eval(s2));
	}
}
