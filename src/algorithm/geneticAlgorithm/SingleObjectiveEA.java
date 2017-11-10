package algorithm.geneticAlgorithm;

import java.util.Comparator;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.geneticAlgorithm.solutions.Solution;
import problems.Problem;

public class SingleObjectiveEA extends EA{
	protected Comparator <Solution> comp;
	protected int popSize;
	
	public static class Builder{
		private final Comparator <Solution> comp;
		private final Problem problem;
		private final SelectionOperator selection;
		private final CrossoverOperator crossover;
		private final MutationOperator mutation;
		
		private int popSize = 100;
		
		public Builder(Problem problem, Comparator <Solution> comp, SelectionOperator selection, CrossoverOperator crossover, MutationOperator mutation){
			this.problem = problem;
			this.comp = comp;
			this.selection = selection;
			this.crossover = crossover;
			this.mutation = mutation; 
		}
		
		public Builder popSize(int val){
			this.popSize = val;
			return this;
		}
		
		public SingleObjectiveEA build(){
			return new SingleObjectiveEA(this);
		}
	}
	
	private SingleObjectiveEA(Builder builder){
		super(builder.problem, builder.selection, builder.crossover, builder.mutation);
		this.popSize = builder.popSize;
		population = problem.createPopulation(popSize);
		comp = builder.comp;
	}
	
	/**
	 * Population is sorted using comparator (ranker) provided during class instantiation.
	 * First popSize solutions are returned as new population.
	 */
	@Override
	protected Population selectNewPopulation(Population pop) {
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
