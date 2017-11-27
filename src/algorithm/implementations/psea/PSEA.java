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
	private Point refPoint;	

	private ArrayList <Comparison> pairwiseComparisons;
	private ASFBundle asfBundle;

	//Parameters
	private AsfDm adm;
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

	public PSEA(Builder <S> builder) {
		super(builder.getProblem(), builder.getPopSize(), builder.getGo());
		
		this.nsgaiii = new NSGAIII <S>(	problem,
					popSize,
					builder.getGo()
					);		
		
		this.population = nsgaiii.getPopulation();
		this.popSize = population.size();
		
		// Algorithm execution parameters 
		this.adm = builder.getAdm();
		this.asfBundle = new ASFBundle(refPoint, this.asfBundleSize, lambdaMutationProbability, lambdaMutationNeighborhoodRadius, lambdaRho);
		this.pairwiseComparisons = new ArrayList<>();
		this.generation = 0;
		this.explorationComparisons = 0;
		this.exploitationComparisons = 0;
		
		
		this.spreadThreshold = 					builder.getSpreadThreshold();
		this.lambdaMutationProbability = 		builder.getLambdaMutationProbability();
		this.lambdaMutationNeighborhoodRadius = builder.getLambdaMutationNeighborhoodRadius();
		this.lambdaRho =						builder.getLambdaRho();
		this.maxExplorationComparisons = 		builder.getMaxExplorationComparisons();
		this.maxExploitationComparisons = 		builder.getMaxExploitationComparisons();
		this.maxZeroDiscriminativePower = 		builder.getMaxZeroDiscriminativePower();
		this.elicitationInterval = 				builder.getElicitationInterval();
		this.maxExploitGenerations = 			builder.getMaxExploitGenerations();
		this.maxNumGenWithNoSpreadImprovment =  builder.getMaxNumGenWithNoSpreadImprovment();
		this.asfBundleSize = 					builder.getAsfBundleSize();
		this.asfDmMutation = 					builder.isAsfDmMutation();
	}
	
	public static class Builder<S extends Solution>{
		
		private final Problem <S> problem;
		private final int popSize;
		private final AsfDm adm;
		private final EA.GeneticOperators<S> go;
		
		private double lambdaMutationProbability;
		private double lambdaMutationNeighborhoodRadius;
		private double lambdaRho;
		private int maxExplorationComparisons;
		private int maxExploitationComparisons;
		private int maxZeroDiscriminativePower;
		private int elicitationInterval;
		private int maxExploitGenerations;
		private int maxNumGenWithNoSpreadImprovment;
		private int asfBundleSize;
		private double spreadThreshold;
		private boolean asfDmMutation;
		
		public Builder(Problem <S> problem, int popSize, AsfDm adm, EA.GeneticOperators<S> go){
			this.problem = problem;
			this.popSize = popSize;
			this.adm = adm;
			this.go = go;
			
			this.spreadThreshold = 					0.95;
			this.lambdaMutationProbability = 		0.05;
			this.lambdaMutationNeighborhoodRadius = 0.3;
			this.lambdaRho =						0.0001;
			this.maxExplorationComparisons = 		20;
			this.maxExploitationComparisons = 		20;
			this.maxZeroDiscriminativePower = 		5;
			this.elicitationInterval = 				10;
			this.maxExploitGenerations = 			800;
			this.maxNumGenWithNoSpreadImprovment =  50;
			this.asfBundleSize = 					50;
			this.asfDmMutation = 					false;
		}

		public Builder <S> setAsfDmMutation(boolean asfDmMutation) {
			this.asfDmMutation = asfDmMutation;
			return this;
		}

		public double getLambdaMutationProbability() {
			return lambdaMutationProbability;
		}

		public Builder <S> setLambdaMutationProbability(double lambdaMutationProbability) {
			this.lambdaMutationProbability = lambdaMutationProbability;
			return this;
		}

		public double getLambdaMutationNeighborhoodRadius() {
			return lambdaMutationNeighborhoodRadius;
		}

		public Builder <S> setLambdaMutationNeighborhoodRadius(double lambdaMutationNeighborhoodRadius) {
			this.lambdaMutationNeighborhoodRadius = lambdaMutationNeighborhoodRadius;
			return this;
		}

		public double getLambdaRho() {
			return lambdaRho;
		}

		public Builder <S> setLambdaRho(double lambdaRho) {
			this.lambdaRho = lambdaRho;
			return this;
		}

		public int getMaxExplorationComparisons() {
			return maxExplorationComparisons;
		}

		public Builder <S> setMaxExplorationComparisons(int maxExplorationComparisons) {
			this.maxExplorationComparisons = maxExplorationComparisons;
			return this;
		}

		public int getMaxExploitationComparisons() {
			return maxExploitationComparisons;
		}

		public Builder <S> setMaxExploitationComparisons(int maxExploitationComparisons) {
			this.maxExploitationComparisons = maxExploitationComparisons;
			return this;
		}

		public int getMaxZeroDiscriminativePower() {
			return maxZeroDiscriminativePower;
		}

		public Builder <S> setMaxZeroDiscriminativePower(int maxZeroDiscriminativePower) {
			this.maxZeroDiscriminativePower = maxZeroDiscriminativePower;
			return this;
		}

		public int getElicitationInterval() {
			return elicitationInterval;
		}

		public Builder <S> setElicitationInterval(int elicitationInterval) {
			this.elicitationInterval = elicitationInterval;
			return this;
		}

		public int getMaxExploitGenerations() {
			return maxExploitGenerations;
		}

		public Builder <S> setMaxExploitGenerations(int maxExploitGenerations) {
			this.maxExploitGenerations = maxExploitGenerations;
			return this;
		}

		public int getMaxNumGenWithNoSpreadImprovment() {
			return maxNumGenWithNoSpreadImprovment;
		}

		public Builder <S> setMaxNumGenWithNoSpreadImprovment(int maxNumGenWithNoSpreadImprovment) {
			this.maxNumGenWithNoSpreadImprovment = maxNumGenWithNoSpreadImprovment;
			return this;
		}

		public int getAsfBundleSize() {
			return asfBundleSize;
		}

		public Builder <S> setAsfBundleSize(int asfBundleSize) {
			this.asfBundleSize = asfBundleSize;
			return this;
		}

		public boolean isAsfDmMutation() {
			return asfDmMutation;
		}

		public Builder <S> setAsfDMsMutation(boolean asfDMsMutation) {
			this.asfDmMutation = asfDMsMutation;
			return this;
		}

		public double getSpreadThreshold() {
			return spreadThreshold;
		}

		public Builder <S> setSpreadThreshold(double spreadThreshold) {
			this.spreadThreshold = spreadThreshold;
			return this;
		}

		public Problem<S> getProblem() {
			return problem;
		}

		public int getPopSize() {
			return popSize;
		}

		public AsfDm getAdm() {
			return adm;
		}

		public EA.GeneticOperators<S> getGo() {
			return go;
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
		
		reachSpreadThresh(); //Perform optimization first to distribute population among large part of objective space and to obtain better quality solutions
		System.out.println("SPREAD REACHED GEN: " + generation);
		explore();
		exploit();
		
		if(generation%100 == 0){
			System.out.println("Exploration/Exploitation comparisons: " + explorationComparisons + "/" + exploitationComparisons);
		}
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
					asfBundle.updateDMs(asfDmMutation, pairwiseComparisons);
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
					asfBundle.updateDMs(asfDmMutation, pairwiseComparisons);
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
