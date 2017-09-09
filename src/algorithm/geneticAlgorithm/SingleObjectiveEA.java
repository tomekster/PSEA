package algorithm.geneticAlgorithm;

import java.util.Comparator;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import artificialDM.AsfDM;
import problems.Problem;

public class SingleObjectiveEA extends EA{
	
	private static final int DEFAULT_NUM_GENERATIONS = 100;
	private static final int DEFAULT_POP_SIZE = 100;
	protected Comparator <Solution> comp;
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
	public SingleObjectiveEA(Problem problem, AsfDM asfRanker) {
		this(
			problem,
			new SBX(problem),
			new PolynomialMutation(problem),
			new BinaryTournament(asfRanker),
			asfRanker,
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
	public SingleObjectiveEA(Problem problem, AsfDM cr, int popSize) {
		this(
			problem,
			new SBX(problem),
			new PolynomialMutation(problem),
			new BinaryTournament(cr),
			cr,
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
	public SingleObjectiveEA(Problem problem, CrossoverOperator crossoverOperator,
			MutationOperator mutationOperator, SelectionOperator selectionOperator, AsfDM cr, int popSize) {
		super(problem, crossoverOperator, mutationOperator, selectionOperator);
		this.popSize = popSize;
		population = problem.createPopulation(popSize);
		comp = cr;
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
