package algorithm.geneticAlgorithm;

import java.util.Comparator;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.geneticAlgorithm.solutions.Solution;
import problems.Problem;

public class SingleObjectiveEA <S extends Solution> extends EA <S>{
	protected Comparator <S> comp;
	
	public SingleObjectiveEA(Problem<S> p, int popSize, SelectionOperator so, CrossoverOperator<S> co, MutationOperator<S> mo, Comparator <S> comp){
		super(p, popSize, so, co, mo);
		this.comp = comp;
	}
	
	/**
	 * Population is sorted using comparator (ranker) provided during class instantiation.
	 * First popSize solutions are returned as new population.
	 */
	@Override
	protected Population <S> selectNewPopulation(Population <S> pop) {
		pop.getSolutions().sort(comp);
		return new Population(pop.getSolutions().subList(0, popSize));
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
