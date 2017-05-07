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
	
	protected Comparator <Solution> comp;
	protected int popSize;
	
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

}
