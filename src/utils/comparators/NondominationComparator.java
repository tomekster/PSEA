package utils.comparators;

import java.util.Comparator;

import algorithm.evolutionary.solutions.Solution;
import utils.enums.OptimizationType;

public class NondominationComparator <S extends Solution> implements Comparator<S>{

	private OptimizationType optimizationType;
	
	public NondominationComparator(OptimizationType ot) {
		this.optimizationType = ot;
	}
	
	@Override
	public int compare(S s1, S s2) {
		boolean s1Dominates = false;
		boolean s2Dominates = false;
		
		switch(optimizationType){
			case MINIMIZATION:
				for(int i=0; i < s1.getNumObjectives(); i++){
					if(s1.getObjective(i) < s2.getObjective(i)) s1Dominates = true;
					if(s1.getObjective(i) > s2.getObjective(i)) s2Dominates = true;
				}
			case MAXIMIZATION:
				for(int i=0; i < s1.getNumObjectives(); i++){
					if(s1.getObjective(i) > s2.getObjective(i)) s1Dominates = true;
					if(s1.getObjective(i) < s2.getObjective(i)) s2Dominates = true;
				}
		}
		
		if(s1Dominates == s2Dominates) return 0;
		else if(s1Dominates == true) return -1;
		else return 1;
	}

}
