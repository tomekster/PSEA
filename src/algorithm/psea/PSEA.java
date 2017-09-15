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
import algorithm.psea.preferences.Elicitator;
import algorithm.psea.preferences.PreferenceCollector;
import algorithm.psea.preferences.DMmodel;
import algorithm.rankers.NonDominationRanker;
import artificialDM.ArtificialDM;
import experiment.ExecutionHistory;
import problems.Problem;
import utils.math.Geometry;
import utils.math.structures.Pair;

public class PSEA extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(PSEA.class.getName());

	public static boolean assertions = true;

	private Problem problem;
	private int populationSize;
	private ArtificialDM adm;
	private int generation;
	private NSGAIII nsgaiii;
	private double spreadThreshold = 0.95;
	private int explorationComparisons;
	private int exploitationComparisons;
	private int maxExplorationComparisons=0;
	private int maxExploitationComparisons=0;
	private DMmodel dmModel;
	
	public PSEA(Problem problem, ArtificialDM adm, int maxExplorCom, int maxExploitComp) {
		this(problem,adm);
		this.maxExplorationComparisons = maxExplorCom;
		this.maxExploitationComparisons = maxExploitComp;
	}
	
	public PSEA(Problem problem, ArtificialDM adm) {
		super(  problem,
				new BinaryTournament(null), //Replaced below
				new SBX(1.0, 30.0, problem.getLowerBounds(), problem.getUpperBounds()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBounds(), problem.getUpperBounds())
				);
		this.dmModel = new DMmodel(problem.findIdealPoint());
		this.setSelectionOperator(new BinaryTournament(dmModel));

		this.nsgaiii = new NSGAIII(	problem, 
				new BinaryTournament(new NonDominationRanker()),
									new SBX(1.0, 30.0, problem.getLowerBounds(), problem.getUpperBounds()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBounds(), problem.getUpperBounds())
									);		
		
		this.population = nsgaiii.getPopulation();
		this.populationSize = population.size();
		
		// Parameters of algorithm execution
		this.problem = problem;
		this.adm = adm;
		
		// Structure for storing intermediate state of algorithm for further analysis, display, etc.
		ExecutionHistory.getInstance().init(problem, nsgaiii, dmModel.getAsfBundle(), adm);
		
		this.generation = 0;
		this.explorationComparisons = 0;
		this.exploitationComparisons = 0;
		PreferenceCollector.getInstance().clear();
		
		if(problem.getNumObjectives() == 3){
			maxExplorationComparisons = 20;
			maxExploitationComparisons = 10;
		}
		else if(problem.getNumObjectives() == 5){
			maxExplorationComparisons = 25;
			maxExploitationComparisons = 15;
		}
		else if(problem.getNumObjectives() == 8){
			maxExplorationComparisons = 30;
			maxExploitationComparisons = 20;
		}
	}

	@Override
	public Population selectNewPopulation(Population pop) {
		problem.evaluate(pop);
		Population sortedPopulation = dmModel.sortSolutions(pop);
		return new Population(sortedPopulation.getSolutions().subList(0, populationSize));
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
		
//		singleObjective();
		exploreExploit();
//		shrinkingHyperplane();
//		exactHyperplane();
//		exactShrinkHyperplane();
		if(generation%100 == 0){
			System.out.println("Exploration/Exploitation comparisons: " + explorationComparisons + "/" + exploitationComparisons);
		}
	}

//	TODO
	private void exactHyperplane() {
		nsgaiii.setHyperplane(Geometry.generateNewHyperplane(problem.getNumObjectives(),1e-3, Geometry.dir2point(adm.getLambda())));
		for(int i=0; i<3000; i++){
			generation++;
			nsgaiii.nextGeneration();
			ExecutionHistory.getInstance().update(nsgaiii.getPopulation(), dmModel.getAsfBundle(), nsgaiii.getHyperplane());
			this.population = nsgaiii.getPopulation();
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, adm.eval(s));
			}
			System.out.println(i + ": " + bestVal);
		}
	}

	private void exactShrinkHyperplane() {
		dmModel.clearDMs();
		nsgaiii.setHyperplane(Geometry.generateNewHyperplane(problem.getNumObjectives(),0.01, Geometry.dir2point(adm.getLambda())));
		for(ReferencePoint rp : nsgaiii.getHyperplane().getReferencePoints()){
			dmModel.addAsfDM(rp.getDim());
		}
		for(int i=0; i<1500; i++){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			
			ExecutionHistory.getInstance().update(population, dmModel.getAsfBundle(), nsgaiii.getHyperplane());
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, adm.eval(s));
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
		SingleObjectiveEA so = new SingleObjectiveEA(problem, this.adm, populationSize);
		for(int i=0; i<3000; i++){
			generation++;
			so.nextGeneration();
			ExecutionHistory.getInstance().update(so.getPopulation(), dmModel.getAsfBundle(), nsgaiii.getHyperplane());
			this.population = so.getPopulation();
			double bestVal = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				bestVal = Double.min(bestVal, adm.eval(s));
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
			ExecutionHistory.getInstance().update(population, dmModel.getAsfBundle(), nsgaiii.getHyperplane());
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
				maxDiscriminativePower = Elicitator.elicitate(population, adm, dmModel.getAsfBundle(), p);
				if(maxDiscriminativePower == 0){
					numZeroDiscriminativePower++;
				}
				else{
					numZeroDiscriminativePower = 0;
					Elicitator.compare(adm, p.first, p.second);
					explorationComparisons++;
					dmModel.getAsfBundle().updateDMs();
				}
			}
			ExecutionHistory.getInstance().update(population, dmModel.getAsfBundle(), nsgaiii.getHyperplane());
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
				maxDiscriminativePOwer = Elicitator.elicitate( population, adm, dmModel.getAsfBundle(), p);
				if(maxDiscriminativePOwer != 0){
					Elicitator.compare(adm, p.first, p.second);
					exploitationComparisons++;
					dmModel.getAsfBundle().updateDMs();
				}
			}
			ExecutionHistory.getInstance().update(population, dmModel.getAsfBundle(), nsgaiii.getHyperplane());
			maxDist = Geometry.maxDist(population);
			
//			double nearestLambda = dmModel.getAsfBundle().getAsfDMs().stream().mapToDouble(lambda -> Geometry.euclideanDistance(lambda.getLambda(), adm.getLambda())).min().getAsDouble();
			double trueNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetPoint(adm))).min().getAsDouble();
//			double lambdaNearestSolution = population.getSolutions().stream().mapToDouble(solution -> Geometry.euclideanDistance(solution.getObjectives(), problem.getTargetAsfPoint(dmModel.getAsfBundle().getAverageLambdaPoint()))).min().getAsDouble();
			int minCV = dmModel.getAsfBundle().getAsfDMs().stream().mapToInt(asf -> asf.getNumViolations()).min().getAsInt();
			int maxCV = dmModel.getAsfBundle().getAsfDMs().stream().mapToInt(asf -> asf.getNumViolations()).max().getAsInt();
			int numLambdas = dmModel.getAsfBundle().getAsfDMs().size();
			
//			if(generation % 100 == 0){
//				System.out.println("Exploitation: " + generation + " " + exploitationComparisons + " solDist: " + String.format("%6.3e",maxDist) + " model dist: " + String.format("%6.3e",nearestLambda) + " trueSolDist: " + String.format("%6.3e",trueNearestSolution) + " lambdaSolDist: " + String.format("%6.3e",lambdaNearestSolution) + " MinCV: " + minCV + " MaxCV: " + maxCV + " NumLambdas: " + numLambdas);
//			}
			if(generation % 100 == 0){
				System.out.println("Exploitation: " + generation + " " + exploitationComparisons + " solDist: " + String.format("%6.3e",maxDist) + " trueSolDist: " + String.format("%6.3e",trueNearestSolution) + " MinCV: " + minCV + " MaxCV: " + maxCV + " NumLambdas: " + numLambdas);
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
				nsgaiii.setHyperplane(Geometry.generateNewHyperplane(problem.getNumObjectives(),size, dmModel.getAsfBundle().getAverageLambdaPoint()));
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
				maxDiscriminativePower = Elicitator.elicitate( population, adm, dmModel.getAsfBundle(), p);
				if(maxDiscriminativePower != 0){
					Elicitator.compare(adm, p.first, p.second);
					exploitationComparisons++;
					dmModel.getAsfBundle().updateDMs();
				}
			}
			maxDist = Geometry.maxDist(population);
			System.out.println("ShrinkHyperplane: " + generation + " " + exploitationComparisons + " " + size + " " + maxDist);
			this.population = nsgaiii.getPopulation();
			ExecutionHistory.getInstance().update(population, dmModel.getAsfBundle(), nsgaiii.getHyperplane());
		}
	}
	
	public DMmodel getDMmodel(){
		return dmModel;
	}
}
