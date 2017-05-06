package core.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;

import core.Lambda;
import core.Population;
import core.Problem;
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

	public static boolean assertions = false;

	private Problem problem;
	private int populationSize;
	private int numGenerations1;
	private int numGenerations2;
	private int numElicitations1;
	private int numElicitations2;
	private int elicitationInterval;
	private int generation;
	private int numLambdas;
	private double spreadThreshold;
	
	private ChebyshevRanker DMranker;
	
	private NSGAIII nsgaiii;
	private Lambda lambda;

	public RST_NSGAIII(Problem problem, int numGenerations1, int numGenerations2, int numElicitations1, int numElicitations2, int elicitationInterval, ChebyshevRanker decisionMakerRanker, int numLambdas, double spreadThreshold) {
		super(  new BinaryTournament(new SolutionsBordaRanker()),
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
		this.numGenerations1 = numGenerations1;
		this.numGenerations2 = numGenerations2;
		this.numElicitations1 = numElicitations1;
		this.numElicitations2 = numElicitations2;
		this.elicitationInterval = elicitationInterval;
		
		// Structure for storing intermediate state of algorithm for further analysis, display, etc.
		ExecutionHistory.getInstance().init(problem, nsgaiii, lambda, decisionMakerRanker, numGenerations1, numGenerations2, numElicitations1, numElicitations2, elicitationInterval);
	}

	
	/**
	 * Version with set number of exploration generations and exploitation generations
	 */
//	public void run() {
//		LOGGER.setLevel(Level.INFO);
//		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
//				+ " objectives, and " + numGenerations1 + "/" + numElicitations2 + " generations, for DecisionMaker: ." + DMranker.getName());
//			
//		for(generation = 0; generation < numGenerations1 + numGenerations2; generation++){
//			if(generation < numGenerations1){
//				nsgaiii.nextGeneration();
//				this.population = nsgaiii.getPopulation();
//			}
//			else{
//				nextGeneration();
//			}
//			
//			if(generation > numGenerations1 - elicitationInterval * numElicitations1 
//					&& generation < numGenerations1 + elicitationInterval * numElicitations2
//					&& generation % elicitationInterval == 0){
//				System.out.println("GENERATION: " + generation + "FIRST_PHASE: " + generation);
//				Elicitator.elicitateN(1, population, DMranker, lambda);
//			}
//			
//			problem.evaluate(population);
//			ExecutionHistory.getInstance().update(population, lambda);
//		}
//	}
	
	/**
	 * Version with set percentage of reference lines coverage instead of number of exploration generations 
	 */
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations1 + "/" + numElicitations2 + " generations, for DecisionMaker: ." + DMranker.getName());
		
		while( nsgaiii.getHyperplane().getNumNiched() > nsgaiii.getHyperplane().getReferencePoints().size() * spreadThreshold){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			problem.evaluate(population);
			ExecutionHistory.getInstance().update(population, lambda);
		}
		
		for(int t=0 ; t < numElicitations1 * elicitationInterval ; t++){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			if(t % elicitationInterval == 0){
				Elicitator.elicitateN(1, population, DMranker, lambda);
			}
			problem.evaluate(population);
			ExecutionHistory.getInstance().update(population, lambda);
		}
		
		for(int t=0; t < numGenerations2; t++){
			generation++;
			nextGeneration();
			if(t < numElicitations2 * elicitationInterval &&  t % elicitationInterval == 0){
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


	public int getNumLambdas() {
		return numLambdas;
	}


	public void setNumLambdas(int numLambdas) {
		this.numLambdas = numLambdas;
	}
}
