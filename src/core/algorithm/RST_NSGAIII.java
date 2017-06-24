package core.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;

import core.Lambda;
import core.Population;
import core.Problem;
import core.points.ReferencePoint;
import core.points.Solution;
import history.ExecutionHistory;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.Elicitator;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.NonDominationRanker;
import solutionRankers.SolutionsBordaRanker;
import utils.Geometry;
import utils.Pair;

public class RST_NSGAIII extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII.class.getName());

	public static boolean assertions = false;

	private Problem problem;
	private int populationSize;
	private ChebyshevRanker DMranker;
	private int generation;
	private NSGAIII nsgaiii;
	private Lambda lambda;
	private double spreadThreshold = 0.95;
	private int explorationComparisons;
	private int exploitationComparisons;
	
	public RST_NSGAIII(Problem problem, ChebyshevRanker decisionMakerRanker) {
		super(  problem, new BinaryTournament(new SolutionsBordaRanker()),
				//new noCrossover(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.nsgaiii = new NSGAIII(	problem, 
									new BinaryTournament(new NonDominationRanker()),
									new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		this.lambda = Lambda.getInstance();		
		lambda.init( problem.getNumObjectives());
		
		this.population = nsgaiii.getPopulation();
		this.populationSize = population.size();
		
		// Parameters of algorithm execution
		this.problem = problem;
		this.DMranker = decisionMakerRanker;
		
		// Structure for storing intermediate state of algorithm for further analysis, display, etc.
		ExecutionHistory.getInstance().init(problem, nsgaiii, lambda, decisionMakerRanker);
		
		this.generation = 0;
		this.explorationComparisons = 0;
		this.exploitationComparisons = 0;
		PreferenceCollector.getInstance().clear();
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
	
	/**
	 * Version with set percentage of reference lines coverage instead of number of exploration generations 
	 */
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII with SpreadThreshold.");
		
		reachSpreadThresh();
		System.out.println("SPREAD REACHED GEN: " + generation);
	
		//singleObjective();
		//exploreExploit();
		shrinkingHyperplane();
		//exactHyperplane();
		//exactShrinkHyperplane();
		exploit();
		System.out.println("Exploration/Exploitation comparisons: " + explorationComparisons + "/" + exploitationComparisons);
		
	}

	private void exactHyperplane() {
		nsgaiii.setNewHyperplane(1e-3, Geometry.dir2point(DMranker.getDirection()));
		for(int i=0; i<3000; i++){
			generation++;
			nsgaiii.nextGeneration();
			ExecutionHistory.getInstance().update(nsgaiii.getPopulation(), lambda, nsgaiii.getHyperplane());
			this.population = nsgaiii.getPopulation();
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, DMranker.eval(s));
			}
			System.out.println(i + ": " + bestVal);
		}
	}

	private void exactShrinkHyperplane() {
		lambda.getLambdaPoints().clear();
		nsgaiii.setNewHyperplane(0.01, Geometry.dir2point(DMranker.getDirection()));
		for(ReferencePoint rp : nsgaiii.getHyperplane().getReferencePoints()){
			lambda.getLambdaPoints().add(rp);
		}
		for(int i=0; i<1500; i++){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			
			ExecutionHistory.getInstance().update(population, lambda, nsgaiii.getHyperplane());
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, DMranker.eval(s));
			}
			System.out.println(i + ": " + bestVal);
		}
	}

	private void shrinkingHyperplane() {
		explore();
		shrinkHyperplane();
	}

	private void exploreExploit() {
		explore();
		exploit();
	}

	private void singleObjective() {
		SingleObjectiveEA so = new SingleObjectiveEA(problem, this.DMranker, populationSize);
		for(int i=0; i<3000; i++){
			generation++;
			so.nextGeneration();
			ExecutionHistory.getInstance().update(so.getPopulation(), lambda, nsgaiii.getHyperplane());
			this.population = so.getPopulation();
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, DMranker.eval(s));
			}
			System.out.println(i + ": " + bestVal);
		}
	}

	private void reachSpreadThresh() {
		int lastImprovedGen = 0, maxNumGenWithNoImprovment = 50;
		double maxSpread = 0;
		
		while(true){
			double currentSpread = (double)(nsgaiii.getHyperplane().getNumNiched()) / nsgaiii.getHyperplane().getReferencePoints().size();
			if(currentSpread > spreadThreshold || generation - lastImprovedGen > maxNumGenWithNoImprovment) break;
			if(currentSpread > maxSpread){
				lastImprovedGen = generation;
				maxSpread = currentSpread;
			}
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			ExecutionHistory.getInstance().update(population, lambda, nsgaiii.getHyperplane());
		}		
	}
	
	private void explore() {
		int split = 0, maxZeroSplits = 5, numZeroSplits = 0, maxExplorComp=20;
		
		if(problem.getNumObjectives() == 3){
			maxExplorComp = 20;
		}
		else if(problem.getNumObjectives() == 5){
			maxExplorComp = 20;
		}
		else if(problem.getNumObjectives() == 8){
			maxExplorComp = 30;
		}
		
		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		
		//Elicitate while population is well spread
		while(numZeroSplits < maxZeroSplits && explorationComparisons < maxExplorComp){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();	
			split = Elicitator.elicitate(population, DMranker, lambda, p);
			
			//If first front (nondominated set) consists of only one solution
			if(split == -1){
				//Do nothing, just go to next generation
			}
			else if(split == 0){
				numZeroSplits++;
			}
			else{
				numZeroSplits = 0;
				Elicitator.compare(DMranker, p.first, p.second);
				explorationComparisons++;
				lambda.nextGeneration();
			}
			ExecutionHistory.getInstance().update(population, lambda, nsgaiii.getHyperplane());
			System.out.println("Exploration: " + generation + " " + explorationComparisons);
		}
	}
	
	private void exploit() {
		//Guide evolution with generated model until it converges
		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		int split = 0, maxExploitComp=30;
		
		if(problem.getNumObjectives() == 3){
			maxExploitComp = 30;
		}
		else if(problem.getNumObjectives() == 5){
			maxExploitComp = 30;
		}
		else if(problem.getNumObjectives() == 8){
			maxExploitComp = 40;
		}
		
		double maxDist;
		do{
			generation++;
			nextGeneration();
			if(generation %10 == 0 &&  exploitationComparisons < maxExploitComp){
				split = Elicitator.elicitate( population, DMranker, lambda, p);
				if(split != 0){
					Elicitator.compare(DMranker, p.first, p.second);
					exploitationComparisons++;
					lambda.nextGeneration();
				}
			}
			ExecutionHistory.getInstance().update(population, lambda, nsgaiii.getHyperplane());
			maxDist = population.maxDist();
			System.out.println("Exploitation: " + generation + " " + exploitationComparisons + " " + maxDist);
		}while(maxDist > 1e-4 && generation < 1500);
	}
	
	private void shrinkHyperplane(){
		int lastImprovedGen = generation, maxNumGenWithNoImprovment = 50, split;
		double size=1.0, maxSpread = 0, currentSpread, maxDist;
		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		
		while(true){
			if(size < 0.01 || generation >= 1500){
				break;
			}
			
			currentSpread = (double)(nsgaiii.getHyperplane().getNumNiched()) / nsgaiii.getHyperplane().getReferencePoints().size();
			if(currentSpread > spreadThreshold || generation - lastImprovedGen > maxNumGenWithNoImprovment){
				size/=4;
				nsgaiii.setNewHyperplane(size, lambda.getAverageLambdaPoint());
				maxSpread=0;
			}
			if(currentSpread > maxSpread){
				lastImprovedGen = generation;
				maxSpread = currentSpread;
			}
			generation++;
			nsgaiii.nextGeneration();
			population = nsgaiii.getPopulation();
			if(generation %10 == 0 &&  exploitationComparisons < 20){
				split = Elicitator.elicitate( population, DMranker, lambda, p);
				if(split != 0){
					Elicitator.compare(DMranker, p.first, p.second);
					exploitationComparisons++;
					lambda.nextGeneration();
				}
			}
			maxDist = population.maxDist();
			System.out.println("ShrinkHyperplane: " + generation + " " + exploitationComparisons + " " + size + " " + maxDist);
			this.population = nsgaiii.getPopulation();
			ExecutionHistory.getInstance().update(population, lambda, nsgaiii.getHyperplane());
		}
	}
}
