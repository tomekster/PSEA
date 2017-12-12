package algorithm.evolutionary.interactive.artificialDM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import utils.math.structures.Point;

public abstract class RferencePointDm implements Comparator<Solution>{

	protected String name;
	
	public Solution getBestSolutionVal(Population <? extends Solution> pop){
		Stream<? extends Solution> solutionStream = pop.getSolutions().stream();
		Solution best = solutionStream.reduce((a,b)-> 
		    eval(a) < eval(b) ? a:b
		).get();
		return best;
	}
	
	public abstract double eval(Solution a);
	public abstract Point getReferencePoint();
	
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
	
	@Override
	public int compare(Solution o1, Solution o2) {
		return Double.compare(eval(o1), eval(o2));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
