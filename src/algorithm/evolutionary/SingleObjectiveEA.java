package algorithm.evolutionary;

import java.util.Comparator;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import problems.Problem;

public class SingleObjectiveEA <S extends Solution> extends EA <S>{
	protected Comparator <Solution> comp;
	
	public SingleObjectiveEA(Problem<S> p, int popSize, EA.GeneticOperators<S> go, Comparator <Solution> comp){
		super(p, popSize, go);
		this.comp = comp;
	}
	
	/**
	 * Population is sorted using comparator (ranker) provided during class instantiation.
	 * First popSize solutions are returned as new population.
	 */
	@Override
	protected Population <S> selectNewPopulation(Population <S> pop) {
		pop.getSolutions().sort(comp);
		return new Population<S>(pop.getSolutions().subList(0, popSize));
	}
	
	/**
	 * Execute algorithm for numGenerations generations 
	 */
	public void run(int numGenerations) {
		for(int i=0; i < numGenerations; i++){
			nextGeneration();
		}
	}

}
