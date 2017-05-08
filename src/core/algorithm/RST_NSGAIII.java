package core.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;

import core.Lambda;
import core.Population;
import core.Problem;
import experiment.ExecutionParameters;
import history.ExecutionHistory;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.Elicitator;
import solutionRankers.ChebyshevRanker;
import solutionRankers.NonDominationRanker;
import solutionRankers.SolutionsBordaRanker;

public abstract class RST_NSGAIII extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII.class.getName());

	public static boolean assertions = false;

	protected Problem problem;
	protected int populationSize;
	protected int elicitationInterval;
	protected ChebyshevRanker DMranker;
	protected int generation;
	protected NSGAIII nsgaiii;
	protected Lambda lambda;
	protected int numElic1;
	protected int numElic2;

	public RST_NSGAIII(Problem problem, ChebyshevRanker decisionMakerRanker, int numLambdas, int elicitationInterval, int numElic1, int numElic2) {
		super(  problem, new BinaryTournament(new SolutionsBordaRanker()),
				//new noCrossover(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.nsgaiii = new NSGAIII(	problem, 
									new BinaryTournament(new NonDominationRanker()),
									new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.lambda = Lambda.getInstance(); 
		lambda.init( problem.getNumObjectives(), numLambdas );
		
		this.population = nsgaiii.getPopulation();
		this.populationSize = population.size();
		
		// Parameters of algorithm execution
		this.problem = problem;
		this.DMranker = decisionMakerRanker;
		this.elicitationInterval = elicitationInterval;
		this.numElic1 = numElic1;
		this.numElic2 = numElic2;
		
		// Structure for storing intermediate state of algorithm for further analysis, display, etc.
		ExecutionHistory.getInstance().init(problem, nsgaiii, lambda, decisionMakerRanker, numElic1, numElic2, elicitationInterval);
	}

	@Override
	public Population selectNewPopulation(Population pop) {
		problem.evaluate(pop);
		SolutionsBordaRanker sbr = new SolutionsBordaRanker();
		Population sortedPopulation = sbr.sortSolutions(pop);
		return new Population(sortedPopulation.getSolutions().subList(0, populationSize));
	}
	
	public int getGeneration(){
		return generation;
	}


	public int getNumLambdas() {
		return lambda.getNumLambdas();
	}
}
