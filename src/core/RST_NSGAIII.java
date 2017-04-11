package core;

import java.util.logging.Level;
import java.util.logging.Logger;

import history.ExecutionHistory;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.Elicitator;
import solutionRankers.ChebyshevRanker;
import solutionRankers.NonDominationRanker;
import solutionRankers.SolutionsBordaRanker;

public class RST_NSGAIII extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII.class.getName());

	private final static int NUM_LAMBDAS = 50;

	public static boolean assertions = false;

	private Problem problem;
	private int populationSize;
	private int numGenerations1;
	private int numGenerations2;
	private int numElicitations1;
	private int numElicitations2;
	private int elicitationInterval;
	private int generation;
	
	private ChebyshevRanker DMranker;
	
	private NSGAIII nsgaiii;
	private Lambda lambda;

	public RST_NSGAIII(Problem problem, int numGenerations1, int numGenerations2, int numElicitations1, int numElicitations2, int elicitationInterval, ChebyshevRanker decisionMakerRanker) {
		super(  new BinaryTournament(new SolutionsBordaRanker()),
				//new noCrossover(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.nsgaiii = new NSGAIII(	problem, 
									new BinaryTournament(new NonDominationRanker()),
									new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.lambda = Lambda.getInstance(); 
		lambda.init( problem.getNumObjectives(), NUM_LAMBDAS );
		
		this.population = nsgaiii.getPopulation();
		this.populationSize = population.size();
		
		// Parameters of algorithm execution
		this.problem = problem;
		this.DMranker = decisionMakerRanker;
		this.numGenerations1 = numGenerations1;
		this.numGenerations2 = numGenerations2;
		this.numElicitations1 = numElicitations1;
		this.numElicitations2 = numElicitations2;
		this.elicitationInterval = elicitationInterval;
		
		// Structure for storing intermediate state of algorithm for further analysis, display, etc.
		ExecutionHistory.getInstance().init(problem, nsgaiii, lambda, decisionMakerRanker, numGenerations1, numGenerations2, numElicitations1, numElicitations2, elicitationInterval);
	}

	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations1 + "/" + numElicitations2 + " generations, for DecisionMaker: ." + DMranker.getName());
			
		for(generation = 0; generation < numGenerations1 + numGenerations2; generation++){
			if(generation < numGenerations1){
				nsgaiii.nextGeneration();
				this.population = nsgaiii.getPopulation();
			}
			else{
				nextGeneration();
			}
			
			if(generation > numGenerations1 - elicitationInterval * numElicitations1 
					&& generation < numGenerations1 + elicitationInterval * numElicitations2
					&& generation % elicitationInterval == 0){
				System.out.println("GENERATION: " + generation + "FIRST_PHASE: " + generation);
				Elicitator.elicitateN(1, population, DMranker, lambda);
			}
			
			problem.evaluate(population);
			ExecutionHistory.getInstance().update(population, lambda);
		}
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
}
