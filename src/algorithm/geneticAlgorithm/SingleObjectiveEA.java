package algorithm.geneticAlgorithm;

import java.util.Comparator;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.geneticAlgorithm.solution.DoubleSolution;
import artificialDM.ArtificialDM;
import problems.Problem;

public class SingleObjectiveEA extends EA{
	
	private static final int DEFAULT_NUM_GENERATIONS = 100;
	private static final int DEFAULT_POP_SIZE = 100;
	protected Comparator <DoubleSolution> comp;
	protected int popSize;
	
	/**
	 * Single objective Evolutionary Algorithm in which multiple objectives are aggregated
	 * into single objective using Achievement Scalarizing Function (ASF) embedded into
	 * asfRanker class.
	 * By default following operators are used:
	 * 	- crossover: SBX 
	 * 	- mutation: PolynomialMutation
	 *  - selection: BinaryTournament
	 *  Default population size = 100
	 *  Default number of generations = 100
	 * @param problem
	 * @param asfRanker
	 */
	public SingleObjectiveEA(Problem problem, ArtificialDM dm) {
		this(
			problem,
			new BinaryTournament(dm),
			new SBX(problem),
			new PolynomialMutation(problem),
			dm,
			DEFAULT_POP_SIZE);
	}
	
	/**
	 * Single objective Evolutionary Algorithm in which multiple objectives are aggregated
	 * into single objective using Achievement Scalarizing Function (ASF) embedded into
	 * asfRanker class.
	 * By default following operators are used:
	 * 	- crossover: SBX 
	 * 	- mutation: PolynomialMutation
	 *  - selection: BinaryTournament
	 *  Default number of generations = 100
	 * @param problem
	 * @param asfRanker
	 */
	public SingleObjectiveEA(Problem problem, ArtificialDM dm, int popSize) {
		this(
			problem,
			new BinaryTournament(dm),
			new SBX(problem),
			new PolynomialMutation(problem),
			dm,
			popSize);
	}
	
	/**
	 * Single objective Evolutionary Algorithm in which multiple objectives are aggregated
	 * into single objective using Achievement Scalarizing Function (ASF) embedded into
	 * asfRanker class.
	 *  Default number of generations = 100
	 * @param problem
	 * @param asfRanker
	 */
	public SingleObjectiveEA(Problem problem, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator,
			MutationOperator mutationOperator, ArtificialDM dm, int popSize) {
		super(problem, selectionOperator, crossoverOperator, mutationOperator);
		this.popSize = popSize;
		population = problem.createPopulation(popSize);
		comp = dm;
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
	 * Execute algorithm for DEFAULT_NUM_GENERATIONS=100 generations 
	 */
	public void run() {
		run(DEFAULT_NUM_GENERATIONS);
	}
	
	/**
	 * Execute algorithm fro numGenerations generations 
	 */
	public void run(int numGenerations) {
		for(int i=0; i < numGenerations; i++){
			nextGeneration();
		}
	}

}
