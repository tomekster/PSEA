package utils.comparators;

import java.util.Comparator;

import algorithm.evolutionary.solutions.Solution;
import utils.enums.OptimizationType;

public class SingleObjectiveComparator implements Comparator<Solution>{

	private int objective;
	private OptimizationType ot;
	
	public SingleObjectiveComparator(int objective, OptimizationType ot) {
		this.objective = objective;
		this.ot = ot;
	}
	
	@Override
	public int compare(Solution s1, Solution s2) {
		if(ot == OptimizationType.MINIMIZATION) {
			return Double.compare(s1.getObjective(objective), s2.getObjective(objective));
		}
		else { //MAXIMIZATION
			return - Double.compare(s1.getObjective(objective), s2.getObjective(objective));
		}
	}
	
}
