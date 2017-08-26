package core.algorithm;

import java.util.Comparator;

import core.Population;
import core.Problem;
import core.points.Solution;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import solutionRankers.ChebyshevRanker;

public class SingleObjectiveEA extends EA{
	
	private static final int DEFAULT_NUM_GENERATIONS = 100;
	private static final int DEFAULT_POP_SIZE = 100;
	protected Comparator <Solution> comp;
	protected int popSize;
	
	public SingleObjectiveEA(Problem problem, ChebyshevRanker cr) {
		this(
			problem,
			new BinaryTournament(cr),
			new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
			new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()),
			cr,
			DEFAULT_POP_SIZE);
	}
	
	public SingleObjectiveEA(Problem problem, ChebyshevRanker cr, int popSize) {
		this(
			problem,
			new BinaryTournament(cr),
			new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
			new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()),
			cr,
			popSize);
	}
	
	public SingleObjectiveEA(Problem problem, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator,
			MutationOperator mutationOperator, ChebyshevRanker cr, int popSize) {
		super(problem, selectionOperator, crossoverOperator, mutationOperator);
		this.popSize = popSize;
		population = problem.createPopulation(popSize);
		comp = cr;
	}

	@Override
	protected Population selectNewPopulation(Population pop) {
		pop.getSolutions().sort(comp);
		Population res = new Population(pop.getSolutions().subList(0, popSize));
		return res;
	}

	public void run() {
		for(int i=0; i < DEFAULT_NUM_GENERATIONS; i++){
			nextGeneration();
		}
	}

}
