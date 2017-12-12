package algorithm.implementations.psea;

import java.util.ArrayList;
import java.util.logging.Logger;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.RferencePointDm;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.nsgaiii.NSGAIII;
import algorithm.implementations.psea.history.GenerationSnapshot;
import algorithm.implementations.psea.preferences.Elicitator;
import utils.NonDominationSort;
import utils.enums.PSEAphase;
import utils.math.structures.Pair;
import utils.math.structures.Point;

public class PSEA <S extends Solution> extends EA<S> implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(PSEA.class.getName());

	public static boolean assertions = true;
	private NSGAIII <S> nsgaiii;
	private int explorationComparisons;
	private int exploitationComparisons;
	private ArrayList <GenerationSnapshot> history = new ArrayList<>();
	private Point refPoint;	

	private ArrayList <Comparison> pairwiseComparisons;
	private ASFBundle asfBundle;

	private PSEAphase pseaPhase;
	
	//Parameters
	private RferencePointDm simulatedDM;
	private int maxExplorationComparisons;
	private int maxExploitationComparisons;
	private double spreadThreshold;
	private double lambdaMutationProbability;
	private double lambdaMutationNeighborhoodRadius;
	private double lambdaRho;
	private int maxZeroDiscriminativePower;
	private int elicitationInterval;
	private int maxExploitGenerations;
	private int maxNumGenWithNoSpreadImprovment;
	private int asfBundleSize;
	private boolean asfDmMutation;

	private int lastImprovedSpreadGen;
	private double maxSpread;
	private int numZeroDiscriminativePower;

	
	public PSEA(PSEABuilder <S> builder) {
		super(builder.getProblem(), 0, builder.getGo());
		
		this.nsgaiii = new NSGAIII <S>(	problem,
					popSize,
					builder.getGo()
					);		
		
		this.population = nsgaiii.getPopulation();
		this.popSize = population.size();
		
		// Reset counters
		this.pseaPhase = PSEAphase.REACHING_SPREAD;
		this.generation 				= 0;
		this.explorationComparisons 	= 0;
		this.exploitationComparisons 	= 0;
		this.lastImprovedSpreadGen 		= 0;
		this.maxSpread 					= 0;
		this.numZeroDiscriminativePower = 0;
		
		// Algorithm execution parameters 
		this.spreadThreshold 					= builder.getSpreadThreshold();
		this.lambdaMutationProbability 			= builder.getLambdaMutationProbability();
		this.lambdaMutationNeighborhoodRadius 	= builder.getLambdaMutationNeighborhoodRadius();
		this.lambdaRho 							= builder.getLambdaRho();
		this.maxExplorationComparisons 			= builder.getMaxExplorationComparisons();
		this.maxExploitationComparisons 		= builder.getMaxExploitationComparisons();
		this.maxZeroDiscriminativePower 		= builder.getMaxZeroDiscriminativePower();
		this.elicitationInterval 				= builder.getElicitationInterval();
		this.maxExploitGenerations 				= builder.getMaxExploitGenerations();
		this.maxNumGenWithNoSpreadImprovment 	= builder.getMaxNumGenWithNoSpreadImprovment();
		this.asfBundleSize 						= builder.getAsfBundleSize();
		this.asfDmMutation 						= builder.isAsfDmMutation();
		this.refPoint 							= builder.getAdm().getReferencePoint();
		
		this.asfBundle = new ASFBundle(refPoint, this.asfBundleSize, lambdaMutationProbability, lambdaMutationNeighborhoodRadius, lambdaRho);
		this.pairwiseComparisons = new ArrayList<>();
		this.simulatedDM = builder.getAdm();
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
		if(pseaPhase == PSEAphase.REACHING_SPREAD){
			reachSpreadThresh(); //Perform optimization first to distribute population among large part of objective space and to obtain better quality solutions
		}
		else if(pseaPhase == PSEAphase.EXPLOARATION){
			explore();
		}
		else if(pseaPhase == PSEAphase.EXPLOITATION){
			exploit();
		}
		
		if(generation > history.size()) history.add(new GenerationSnapshot(generation, population.copy(), asfBundle.getAsfDMs()));
		
		if(generation % 100 == 0){
			System.out.println("Exploration/Exploitation comparisons: " + explorationComparisons + "/" + exploitationComparisons);
		}
	}

	private void reachSpreadThresh() {
		double currentSpread = (double)(nsgaiii.getHyperplane().getNumNiched()) / nsgaiii.getHyperplane().getReferencePoints().size();
		if(currentSpread > maxSpread){
			lastImprovedSpreadGen = generation;
			maxSpread = currentSpread;
		}
		
		if(currentSpread > spreadThreshold || generation - lastImprovedSpreadGen > maxNumGenWithNoSpreadImprovment){
			pseaPhase = PSEAphase.EXPLOARATION; //Switch to next phase
			System.out.println("Gen: " + this.generation + ", starting EXPLORATION");
			return;
		}
		
		nsgaiii.nextGeneration();
		generation++;
		this.population = nsgaiii.getPopulation();	
	}
	
	private void explore() {
		if( numZeroDiscriminativePower > maxZeroDiscriminativePower || explorationComparisons > maxExplorationComparisons){
			pseaPhase = PSEAphase.EXPLOITATION; //Switch to next phase
			System.out.println("Gen: " + this.generation + ", starting EXPLOITATION");
			return;
		}
		
		int maxDiscriminativePower = 0;
		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null, null);
		nsgaiii.nextGeneration();
		generation++;
		this.population = nsgaiii.getPopulation();
		
		//We want to select for comparison only non-dominated solutions, therefore we consider only solutions from the first front
		Population <S> firstFront = NonDominationSort.sortPopulation(population, problem.getOptimizationType()).get(0);
		
		//If first front (nondominated set) consists of at least two solutions try to elicitate
		if(firstFront.size() > 1){
			maxDiscriminativePower = Elicitator.elicitate(population, simulatedDM, asfBundle, p);
			if(maxDiscriminativePower == 0){
				numZeroDiscriminativePower++;
			}
			else{
				numZeroDiscriminativePower = 0;
				Comparison cmp = Elicitator.compare(simulatedDM, p.first, p.second, generation);
				if(cmp != null){
					pairwiseComparisons.add(cmp);
				}
				explorationComparisons++;
				asfBundle.updateDMs(asfDmMutation, pairwiseComparisons);
			}
		}
		System.out.println("Exploration: " + generation + " " + explorationComparisons);
	}
	
	private void exploit() {
		if(generation > maxExploitGenerations){
			pseaPhase = PSEAphase.REACHED_MAX_GENERATIONS;
			System.out.println("Gen: " + this.generation + ", REACHED MAX GEN");
			return;
		}
		
		//Guide evolution with generated model until it converges
		Pair <Solution, Solution> p = new Pair<>(null,null);
		int maxDiscriminativePower = 0;

		generation++;
		nextGeneration();
		
		if(generation % elicitationInterval == 0 &&  exploitationComparisons < maxExploitationComparisons){
			maxDiscriminativePower = Elicitator.elicitate( population, simulatedDM, asfBundle, p);
			if(maxDiscriminativePower != 0){
				Comparison cmp = Elicitator.compare(simulatedDM, p.first, p.second, generation);
				if(cmp != null){
					pairwiseComparisons.add(cmp);
				}
				
				exploitationComparisons++;
				asfBundle.updateDMs(asfDmMutation, pairwiseComparisons);
			}
		}

		double maxDist = population.maxDist();
//		double nearestLambda = dmModel.getAsfBundle().getAsfDMs().stream().mapToDouble(lambda -> Geometry.euclideanDistance(lambda.getLambda(), adm.getLambda())).min().getAsDouble();
//		double trueNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetPoint(adm))).min().getAsDouble();
//		double lambdaNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetAsfPoint(dmModel.getAsfBundle().getAverageLambdaPoint()))).min().getAsDouble();
		int minCV = asfBundle.getAsfDMs().stream().mapToInt(asf -> asf.getNumViolations()).min().getAsInt();
		int maxCV = asfBundle.getAsfDMs().stream().mapToInt(asf -> asf.getNumViolations()).max().getAsInt();
		int numLambdas = asfBundle.getAsfDMs().size();
		
		if(generation % 100 == 0){
			System.out.println("Exploitation: " + generation + " " + exploitationComparisons + " solDist: " + String.format("%6.3e",maxDist) + " MinCV: " + minCV + " MaxCV: " + maxCV + " NumLambdas: " + numLambdas);
		}
	}
	
	public RferencePointDm getSimulatedDm() {
		return simulatedDM;
	}

	public PSEA <S> setAdm(AsfDm adm) {
		this.simulatedDM = adm;
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

	public ArrayList<GenerationSnapshot> getHistory() {
		return this.history;
	}
	
	public PSEAphase getPseaPhase(){
		return this.pseaPhase;
	}
}
