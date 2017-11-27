package algorithm.implementations.psea;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.nsgaiii.NSGAIII;
import algorithm.implementations.psea.history.GenerationSnapshot;
import algorithm.implementations.psea.preferences.Elicitator;
import problems.Problem;
import utils.NonDominationSort;
import utils.math.structures.Pair;
import utils.math.structures.Point;

public class PSEA <S extends Solution> extends EA<S> implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(PSEA.class.getName());

	public static boolean assertions = true;
	private NSGAIII <S> nsgaiii;
	private int explorationComparisons;
	private int exploitationComparisons;
	private ArrayList <GenerationSnapshot> history = new ArrayList<>();
	
	private final double DEFAULT_SPREAD_THRESHOLD = 0.95;
	private final double DEFAULT_LAMBDA_MUTATION_PROBABILITY = 0.05;
	private final double DEFAULT_LAMBDA_MUTATION_NEIGHBORHOOD_RADIUS = 0.3;
	private final double DEFAULT_LAMBDA_RHO = 0.0001;
	private final int DEFAULT_MAX_EXPLORATION_COMPARISONS = 20;
	private final int DEFAULT_MAX_EXPLOITATION_COMPARISONS = 20;
	private final int DEFAULT_MAX_ZERO_DISCRIMINATIVE_POWER = 5;

	private final int DEFAULT_ELICITATION_INTERVAL = 10;
	private final int DEFAULT_MAX_EXPLITATION_GENERATIONS = 800;
	private final int DEFAULT_POP_SIZE = 100;
	private final int DEFAULT_MAX_NUM_GEN_WITH_NO_SPREAD_IMPROVEMENT = 50;
	private final int DEFAULT_ASF_BUNDLE_SIZE = 50;
	private final boolean DEFAULT_ASF_DMS_MUTATION = false;
	
	//Parameters
	private AsfDm adm;
	private ASFBundle asfBundle;
	private ArrayList <Comparison> pairwiseComparisons;
	private double spreadThreshold;
	private double lambdaMutationProbability;
	private double lambdaMutationNeighborhoodRadius;
	private double lambdaRho = DEFAULT_LAMBDA_RHO;
	private int maxExplorationComparisons;
	private int maxExploitationComparisons;

	private int maxZeroDiscriminativePower;
	private int elicitationInterval;
	private int maxExploitGenerations;
	private int maxNumGenWithNoSpreadImprovment;
	private int asfBundleSize;
	private boolean asfDMsMutation;
	
	public PSEA(Problem <S> problem, int popSize, AsfDm adm, EA.GeneticOperators<S> go, Point refPoint) {
		super(problem, popSize, go);
		
		this.nsgaiii = new NSGAIII <S>(	problem,
					popSize,
					go
					);		
		
		this.population = nsgaiii.getPopulation();
		this.popSize = population.size();
		
		// Algorithm execution parameters 
		this.problem = problem;
		this.adm = adm;
		
		this.asfBundle = new ASFBundle(refPoint, this.asfBundleSize, lambdaMutationProbability, lambdaMutationNeighborhoodRadius, lambdaRho);
		this.pairwiseComparisons = new ArrayList<>();
		
		this.generation = 0;
		this.explorationComparisons = 0;
		this.exploitationComparisons = 0;
		
		this.lambdaMutationProbability = DEFAULT_LAMBDA_MUTATION_PROBABILITY;
		this.lambdaMutationNeighborhoodRadius = DEFAULT_LAMBDA_MUTATION_NEIGHBORHOOD_RADIUS;
		this.spreadThreshold = DEFAULT_SPREAD_THRESHOLD;
		this.maxExplorationComparisons = DEFAULT_MAX_EXPLORATION_COMPARISONS;
		this.maxExploitationComparisons = DEFAULT_MAX_EXPLOITATION_COMPARISONS;
		this.maxZeroDiscriminativePower = DEFAULT_MAX_ZERO_DISCRIMINATIVE_POWER;
		this.elicitationInterval = DEFAULT_ELICITATION_INTERVAL;
		this.maxExploitationComparisons = DEFAULT_MAX_EXPLITATION_GENERATIONS;
		this.popSize = DEFAULT_POP_SIZE;
		this.maxNumGenWithNoSpreadImprovment = DEFAULT_MAX_NUM_GEN_WITH_NO_SPREAD_IMPROVEMENT;
		this.asfBundleSize = DEFAULT_ASF_BUNDLE_SIZE;
		this.asfDMsMutation = DEFAULT_ASF_DMS_MUTATION;
	}
	
	public PSEA(Problem <S> problem, int popSize, AsfDm adm, int maxExplorCom, int maxExploitComp, EA.GeneticOperators<S> go, Point refPoint) {
		this(problem, popSize, adm, go, refPoint);
		this.maxExplorationComparisons = maxExplorCom;
		this.maxExploitationComparisons = maxExploitComp;
	}
	
	public class Builder{
		
		private final String problemName;
		private final int popSize;
		private final AsfDm adm;
		private final EA.GeneticOperators<S> go;
		
		private 
		
		public Builder(String problemName, int popSize, AsfDm adm, EA.GeneticOperators<S> go){
			this.problemName = problemName;
			this.popSize = popSize;
			this.adm = adm;
			this.go = go;
		}
		
		
		
	}

	@Override
	public Population <S> selectNewPopulation(Population <S> pop) {
		problem.evaluate(pop);
		Population <S> sortedPopulation = (Population <S>) asfBundle.sortSolutions(pop);
		return new Population <S> ( sortedPopulation.getSolutions().subList(0, popSize) );
	}
	
	public int getGeneration(){
		return generation;
	}
	
	/**
	 * Version with set percentage of reference lines coverage instead of number of exploration generations 
	 */
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for" + problem.getName() + "_" + problem.getNumObjectives() + "obj_" + adm.getName());
		
		exploreExploit();

		if(generation%100 == 0){
			System.out.println("Exploration/Exploitation comparisons: " + explorationComparisons + "/" + exploitationComparisons);
		}
	}

	private void exploreExploit() {
		reachSpreadThresh(); //Perform optimization first to distribute population among large part of objective space and to obtain better quality solutions
		System.out.println("SPREAD REACHED GEN: " + generation);
		explore();
		exploit();
	}

	private void reachSpreadThresh() {
		int lastImprovedGen = 0;
		double maxSpread = 0;
		
		while(true){
			double currentSpread = (double)(nsgaiii.getHyperplane().getNumNiched()) / nsgaiii.getHyperplane().getReferencePoints().size();
			if(currentSpread > spreadThreshold || generation - lastImprovedGen > maxNumGenWithNoSpreadImprovment) break;
			if(currentSpread > maxSpread){
				lastImprovedGen = generation;
				maxSpread = currentSpread;
			}
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
		}		
	}
	
	private void explore() {
		int maxDiscriminativePower = 0, numZeroDiscriminativePower = 0;

		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		
		//Elicitate while population is well spread
		while(numZeroDiscriminativePower < maxZeroDiscriminativePower && explorationComparisons < maxExplorationComparisons){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			
			//We want to select for comparison only non-dominated solutions, therefore we consider only solutions from first front
			Population <S> firstFront = NonDominationSort.sortPopulation(population, problem.getOptimizationType()).get(0);
			
			//If first front (nondominated set) consists of at least two solutions try to elicitate
			if(firstFront.size() > 1){
				maxDiscriminativePower = Elicitator.elicitate(population, adm, asfBundle, p);
				if(maxDiscriminativePower == 0){
					numZeroDiscriminativePower++;
				}
				else{
					numZeroDiscriminativePower = 0;
					Elicitator.compare(adm, p.first, p.second, generation);
					explorationComparisons++;
					asfBundle.updateDMs(asfDMsMutation, pairwiseComparisons);
				}
			}
			
			history.add(new GenerationSnapshot(generation, population.copy(), asfBundle.getAsfDMs()));
			
			System.out.println("Exploration: " + generation + " " + explorationComparisons);
		}
	}
	
	private void exploit() {
		//Guide evolution with generated model until it converges
		Pair <Solution, Solution> p = new Pair<>(null,null);
		int maxDiscriminativePOwer = 0;
		
		double maxDist;
		do{
			generation++;
			nextGeneration();
			if(generation % elicitationInterval == 0 &&  exploitationComparisons < maxExploitationComparisons){
				maxDiscriminativePOwer = Elicitator.elicitate( population, adm, asfBundle, p);
				if(maxDiscriminativePOwer != 0){
					Elicitator.compare(adm, p.first, p.second, generation);
					exploitationComparisons++;
					asfBundle.updateDMs(asfDMsMutation, pairwiseComparisons);
				}
			}
			history.add(new GenerationSnapshot(generation, population.copy(), asfBundle.getAsfDMs()));
			maxDist = population.maxDist();
			
//			double nearestLambda = dmModel.getAsfBundle().getAsfDMs().stream().mapToDouble(lambda -> Geometry.euclideanDistance(lambda.getLambda(), adm.getLambda())).min().getAsDouble();
//			double trueNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetPoint(adm))).min().getAsDouble();
//			double lambdaNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetAsfPoint(dmModel.getAsfBundle().getAverageLambdaPoint()))).min().getAsDouble();
			int minCV = asfBundle.getAsfDMs().stream().mapToInt(asf -> asf.getNumViolations()).min().getAsInt();
			int maxCV = asfBundle.getAsfDMs().stream().mapToInt(asf -> asf.getNumViolations()).max().getAsInt();
			int numLambdas = asfBundle.getAsfDMs().size();
			
			if(generation % 100 == 0){
				System.out.println("Exploitation: " + generation + " " + exploitationComparisons + " solDist: " + String.format("%6.3e",maxDist) + " MinCV: " + minCV + " MaxCV: " + maxCV + " NumLambdas: " + numLambdas);
			}
		//}while(maxDist > 1e-4 && generation < 1500);
		}while(generation < maxExploitGenerations);
	}
	
	public AsfDm getAdm() {
		return adm;
	}

	public PSEA <S> setAdm(AsfDm adm) {
		this.adm = adm;
		return this;
	}

	public ASFBundle asfBundle() {
		return asfBundle;
	}

	public PSEA <S> setPreferenceModel(ASFBundle asfBundle) {
		this.asfBundle= asfBundle;
		return this;
	}

	public double getSpreadThreshold() {
		return spreadThreshold;
	}

	public PSEA <S> setSpreadThreshold(double spreadThreshold) {
		this.spreadThreshold = spreadThreshold;
		return this;
	}

	public int getMaxExplorationComparisons() {
		return maxExplorationComparisons;
	}

	public PSEA <S> setMaxExplorationComparisons(int maxExplorationComparisons) {
		this.maxExplorationComparisons = maxExplorationComparisons;
		return this;
	}

	public int getMaxExploitationComparisons() {
		return maxExploitationComparisons;
	}

	public PSEA <S> setMaxExploitationComparisons(int maxExploitationComparisons) {
		this.maxExploitationComparisons = maxExploitationComparisons;
		return this;
	}

	public int getMaxZeroDiscriminativePower() {
		return maxZeroDiscriminativePower;
	}

	public PSEA <S> setMaxZeroDiscriminativePower(int maxZeroDiscriminativePower) {
		this.maxZeroDiscriminativePower = maxZeroDiscriminativePower;
		return this;
	}

	public int getElicitationInterval() {
		return elicitationInterval;
	}

	public PSEA <S> setElicitationInterval(int elicitationInterval) {
		this.elicitationInterval = elicitationInterval;
		return this;
	}

	public int getMaxExploitGenerations() {
		return maxExploitGenerations;
	}

	public PSEA <S> setMaxExploitGenerations(int maxExploitGenerations) {
		this.maxExploitGenerations = maxExploitGenerations;
		return this;
	}

	public int getMaxNumGenWithNoSpreadImprovment() {
		return maxNumGenWithNoSpreadImprovment;
	}

	public PSEA <S> setMaxNumGenWithNoSpreadImprovment(int maxNumGenWithNoSpreadImprovment) {
		this.maxNumGenWithNoSpreadImprovment = maxNumGenWithNoSpreadImprovment;
		return this;
	}
}
