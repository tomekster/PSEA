package algorithm.psea;

import java.util.logging.Level;
import java.util.logging.Logger;

import algorithm.geneticAlgorithm.EA;
import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.geneticAlgorithm.Solution;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.nsgaiii.NSGAIII;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import algorithm.psea.preferences.ASFBundle;
import algorithm.psea.preferences.Elicitator;
import algorithm.psea.preferences.PreferenceCollector;
import algorithm.rankers.AsfRanker;
import algorithm.rankers.NonDominationRanker;
import algorithm.rankers.WeightedBordaRanker;
import experiment.ExecutionHistory;
import problems.Problem;
import utils.math.Geometry;
import utils.math.structures.Pair;

public class PSEA extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(PSEA.class.getName());

	public static boolean assertions = true;

	private Problem problem;
	private int populationSize;
	private AsfRanker DMranker;
	private int generation;
	private NSGAIII nsgaiii;
	private ASFBundle asfBundle;
	private double spreadThreshold = 0.95;
	private int explorationComparisons;
	private int exploitationComparisons;
	private int maxExplorationComparisons=0;
	private int maxExploitationComparisons=0;

	public PSEA(Problem problem, AsfRanker decisionMakerRanker, int maxExplorCom, int maxExploitComp) {
		this(problem,decisionMakerRanker);
		this.maxExplorationComparisons = maxExplorCom;
		this.maxExploitationComparisons = maxExploitComp;
	}
	
	public PSEA(Problem problem, AsfRanker decisionMakerRanker) {
		super(  problem,
				new SBX(1.0, 30.0, problem.getLowerBounds(), problem.getUpperBounds()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBounds(), problem.getUpperBounds()),
				new BinaryTournament(new WeightedBordaRanker())
				);
		
		this.nsgaiii = new NSGAIII(	problem, 
									new SBX(1.0, 30.0, problem.getLowerBounds(), problem.getUpperBounds()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBounds(), problem.getUpperBounds()),
									new BinaryTournament(new NonDominationRanker()));
		this.asfBundle = ASFBundle.getInstance();		
		asfBundle.init(problem);
		
		this.population = nsgaiii.getPopulation();
		this.populationSize = population.size();
		
		// Parameters of algorithm execution
		this.problem = problem;
		this.DMranker = decisionMakerRanker;
		
		// Structure for storing intermediate state of algorithm for further analysis, display, etc.
		ExecutionHistory.getInstance().init(problem, nsgaiii, asfBundle, decisionMakerRanker);
		
		this.generation = 0;
		this.explorationComparisons = 0;
		this.exploitationComparisons = 0;
		PreferenceCollector.getInstance().clear();
		
		if(problem.getNumObjectives() == 3){
			maxExplorationComparisons = 20;
			maxExploitationComparisons = 10;
		}
		else if(problem.getNumObjectives() == 5){
			maxExplorationComparisons = 20;
			maxExploitationComparisons = 20;
		}
		else if(problem.getNumObjectives() == 8){
			maxExplorationComparisons = 30;
			maxExploitationComparisons = 30;
		}
	}

	@Override
	public Population selectNewPopulation(Population pop) {
		problem.evaluate(pop);
		WeightedBordaRanker sbr = new WeightedBordaRanker();
		Population sortedPopulation = sbr.sortSolutions(pop);
		return new Population(sortedPopulation.getSolutions().subList(0, populationSize));
	}
	
	public int getGeneration(){
		return generation;
	}


	public int getNumLambdas() {
		return asfBundle.getNumLambdas();
	}
	
	/**
	 * Version with set percentage of reference lines coverage instead of number of exploration generations 
	 */
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for" + problem.getName() + "_" + problem.getNumObjectives() + "obj_" + DMranker.getName());
		
//		singleObjective();
		exploreExploit();
//		shrinkingHyperplane();
//		exactHyperplane();
//		exactShrinkHyperplane();
		if(generation%100 == 0){
			System.out.println("Exploration/Exploitation comparisons: " + explorationComparisons + "/" + exploitationComparisons);
		}
	}

	private void exactHyperplane() {
		nsgaiii.setHyperplane(Geometry.generateNewHyperplane(problem.getNumObjectives(),1e-3, Geometry.dir2point(DMranker.getLambda())));
		for(int i=0; i<3000; i++){
			generation++;
			nsgaiii.nextGeneration();
			ExecutionHistory.getInstance().update(nsgaiii.getPopulation(), asfBundle, nsgaiii.getHyperplane());
			this.population = nsgaiii.getPopulation();
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, DMranker.eval(s));
			}
			System.out.println(i + ": " + bestVal);
		}
	}

	private void exactShrinkHyperplane() {
		asfBundle.getPreferenceModels().clear();
		nsgaiii.setHyperplane(Geometry.generateNewHyperplane(problem.getNumObjectives(),0.01, Geometry.dir2point(DMranker.getLambda())));
		for(ReferencePoint rp : nsgaiii.getHyperplane().getReferencePoints()){
			asfBundle.getPreferenceModels().add(new AsfPreferenceModel(rp.getDim()));
		}
		for(int i=0; i<1500; i++){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			
			ExecutionHistory.getInstance().update(population, asfBundle, nsgaiii.getHyperplane());
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
			ExecutionHistory.getInstance().update(so.getPopulation(), asfBundle, nsgaiii.getHyperplane());
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
			ExecutionHistory.getInstance().update(population, asfBundle, nsgaiii.getHyperplane());
		}		
	}
	
	private void explore() {
		reachSpreadThresh(); //Perform optimization first to distribute population among large part of objective space and to obtain better quality solutions
		System.out.println("SPREAD REACHED GEN: " + generation);
		
		int maxDiscriminativePower = 0, maxZeroDiscriminativePower = 5, numZeroDiscriminativePower = 0;

		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		
		//Elicitate while population is well spread
		while(numZeroDiscriminativePower < maxZeroDiscriminativePower && explorationComparisons < maxExplorationComparisons){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			
			//We want to select for comparison only non-dominated solutions, therefore we consider only solutions from first front
			Population firstFront = NonDominationRanker.sortPopulation(population).get(0);
			
			//If first front (nondominated set) consists of at least two solutions try to elicitate
			if(firstFront.size() > 1){
				maxDiscriminativePower = Elicitator.elicitate(population, DMranker, asfBundle, p);
				if(maxDiscriminativePower == 0){
					numZeroDiscriminativePower++;
				}
				else{
					numZeroDiscriminativePower = 0;
					Elicitator.compare(DMranker, p.first, p.second);
					explorationComparisons++;
					asfBundle.nextGeneration();
				}
			}
			ExecutionHistory.getInstance().update(population, asfBundle, nsgaiii.getHyperplane());
			if(generation % 100 == 0){
				System.out.println("Exploration: " + generation + " " + explorationComparisons);
			}
		}
	}
	
	private void exploit() {
		//Guide evolution with generated model until it converges
		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		int maxDiscriminativePOwer = 0;
		
		double maxDist;
		do{
			generation++;
			nextGeneration();
			if(generation %10 == 0 &&  exploitationComparisons < maxExploitationComparisons){
				maxDiscriminativePOwer = Elicitator.elicitate( population, DMranker, asfBundle, p);
				if(maxDiscriminativePOwer != 0){
					Elicitator.compare(DMranker, p.first, p.second);
					exploitationComparisons++;
					asfBundle.nextGeneration();
				}
			}
			ExecutionHistory.getInstance().update(population, asfBundle, nsgaiii.getHyperplane());
			maxDist = Geometry.maxDist(population);
			double nearestLambda = ASFBundle.getInstance().getPreferenceModels().stream().mapToDouble(lambda -> Geometry.euclideanDistance(lambda.getLambda(), DMranker.getLambda())).min().getAsDouble();
			double trueNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetPoint(DMranker.getLambda()))).min().getAsDouble();
			double lambdaNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetPoint(ASFBundle.getInstance().getAverageLambdaPoint()))).min().getAsDouble();
			if(generation % 100 == 0){
				System.out.println("Exploitation: " + generation + " " + exploitationComparisons + " solDist: " + String.format("%6.3e",maxDist) + " model dist: " + String.format("%6.3e",nearestLambda) + " trueSolDist: " + String.format("%6.3e",trueNearestSolution) + " lambdaSolDist: " + String.format("%6.3e",lambdaNearestSolution));
			}
		}while(maxDist > 1e-4 && generation < 1500);
	}
	
	private void shrinkHyperplane(){
		int lastImprovedGen = generation, maxNumGenWithNoImprovment = 50, maxDiscriminativePower;
		double size=1.0, maxSpread = 0, currentSpread, maxDist;
		Pair <Solution, Solution> p = new Pair<Solution, Solution>(null,null);
		
		while(true){
			if(size < 0.01 || generation >= 1500){
				break;
			}
			
			currentSpread = (double)(nsgaiii.getHyperplane().getNumNiched()) / nsgaiii.getHyperplane().getReferencePoints().size();
			if(currentSpread > spreadThreshold || generation - lastImprovedGen > maxNumGenWithNoImprovment){
				size/=4;
				nsgaiii.setHyperplane(Geometry.generateNewHyperplane(problem.getNumObjectives(),size, asfBundle.getAverageLambdaPoint()));
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
				maxDiscriminativePower = Elicitator.elicitate( population, DMranker, asfBundle, p);
				if(maxDiscriminativePower != 0){
					Elicitator.compare(DMranker, p.first, p.second);
					exploitationComparisons++;
					asfBundle.nextGeneration();
				}
			}
			maxDist = Geometry.maxDist(population);
			System.out.println("ShrinkHyperplane: " + generation + " " + exploitationComparisons + " " + size + " " + maxDist);
			this.population = nsgaiii.getPopulation();
			ExecutionHistory.getInstance().update(population, asfBundle, nsgaiii.getHyperplane());
		}
	}
}
