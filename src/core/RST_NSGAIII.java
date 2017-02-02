package core;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.Hyperplane;
import core.points.ReferencePoint;
import core.points.Solution;
import history.ExecutionHistory;
import igd.TargetFrontGenerator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.Elicitator;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.NonDominationRanker;
import utils.Geometry;
import utils.GradientLambdaSearch;
import utils.MyMath;
import utils.Pair;

public class RST_NSGAIII extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII.class.getName());

	private static final double SPREAD_THRESHOLD = 0.9;

	private final static int NUM_LAMBDAS = 50;

	private Problem problem;
	private int numGenerations;
	private int populationSize;
	private int elicitationInterval;
	private int generation;
	
	private Hyperplane hyperplane;
	private ChebyshevRanker decisionMakerRanker;
	
	private NSGAIII nsgaiii;
	private Lambda lambda;

	public RST_NSGAIII(Problem problem, int numGenerations, int elicitationInterval, ChebyshevRanker decisionMakerRanker) {
		super(  new BinaryTournament(new NonDominationRanker()),
				//new noCrossover(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.nsgaiii = new NSGAIII(	problem, 
									new BinaryTournament(new NonDominationRanker()),
									new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.lambda = new Lambda( problem.getNumObjectives(), NUM_LAMBDAS );

		// Hyperplane is one of basic constructs used in NSGA-III algorithm. It is responsible 
		// for keeping solutions uniformly spread among objective space. 
		// In modified NSGA-III it is used to store information about directions which are interesting 
		// from DM's point of view, based on preference information elicitated during algorithm run 
		this.hyperplane = new Hyperplane(problem.getNumObjectives());

		// Number of solutions in every generation. Depends on Hyperplane because number 
		// of solutions in population should be close to number of Reference Points on Hyperplane
		this.population = nsgaiii.getPopulation();
		this.populationSize = population.size();
		
		// Parameters of algorithm execution
		this.numGenerations = numGenerations;
		this.problem = problem;
		this.elicitationInterval = elicitationInterval;
		this.decisionMakerRanker = decisionMakerRanker;
		
		// Structure for storing intermediate state of algorithm for further
		// analysis, display, etc.
		ExecutionHistory.clear();
		ExecutionHistory history = ExecutionHistory.getInstance();
		history.setNumVariables(problem.getNumVariables());
		history.setNumObjectives(problem.getNumObjectives());
		history.addGeneration(nsgaiii.getPopulation());
		history.addLambdas(lambda.getLambdas());
		history.setTargetPoints(TargetFrontGenerator.generate(this.hyperplane.getReferencePoints(), problem));
		history.setPreferenceCollector(PreferenceCollector.getInstance());
		history.setChebyshevRanker(decisionMakerRanker);
	}

	public void run() {
		ExecutionHistory history = ExecutionHistory.getInstance();
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations + " generations, for DecisionMaker: ." + decisionMakerRanker.getName());

		boolean secondPhase = false;
		boolean switchPhase = false;
		for (generation = 0; generation < numGenerations; generation++) {
			switchPhase = false;
			if(!secondPhase){
				System.out.println(nsgaiii.getHyperplane().getNumNiched() + "/" + nsgaiii.getPopulation().size() + " (" + nsgaiii.getHyperplane().getNumNiched() * 100.0 / nsgaiii.getPopulation().size() + "%)");
			}
			if(!secondPhase && nsgaiii.getHyperplane().getNumNiched() >= nsgaiii.getHyperplane().getReferencePoints().size() * SPREAD_THRESHOLD){
				System.out.println("SECOND PHASE: " + generation);
				switchPhase = true;
				secondPhase = true;
				history.setSecondPhaseId(generation);
			}
			if (switchPhase) for(int i=0; i<problem.getNumObjectives() * 3; i++) elicitate();
			
			if(secondPhase){
				if(generation > 0 && generation % elicitationInterval == 0){
					for(int i=0; i<problem.getNumObjectives()/2; i++){
						elicitate();
					}
				}
				lambda.nextGeneration();
				nextGeneration();
			}
			else{
				nsgaiii.nextGeneration();
				this.population = nsgaiii.getPopulation();
			}
			
			problem.evaluate(population);
			history.addGeneration(population.copy());
			history.addLambdas((ArrayList <ReferencePoint>)lambda.getLambdas().clone());
			history.addBestChebVal(evaluateGeneration(population));
		}
		
		evaluateFinalResult(population);
		System.out.println("Generation min: " + history.getFinalMinDist());
		System.out.println("Generation avg: " + history.getFinalAvgDist());
	}

	private void elicitate() {
		System.out.println("GENERATION: " + generation);
		NonDominationRanker ndr = new NonDominationRanker();
		Population firstFront = ndr.sortPopulation(population).get(0);
		if (firstFront.size() > 1){
			Elicitator.elicitate(firstFront, decisionMakerRanker, PreferenceCollector.getInstance());
		}	
	}

	@Override
	public Population selectNewPopulation(Population pop) {
		problem.evaluate(pop);
		Population sortedNewPopulation = lambda.selectKSolutionsByChebyshevBordaRanking(pop, populationSize);
		return sortedNewPopulation;
	}

	public Pair<Solution, Double> evaluateGeneration(Population pop) {
		return decisionMakerRanker.getBestSolutionVal(pop);
	}
	
	public void evaluateFinalResult(Population res){
		evaluateRun(problem, decisionMakerRanker, res);
	}
	
	private void evaluateRun(Problem prob, ChebyshevRanker dmr, Population res) {
		String pname = prob.getName();
		double targetPoint[] = {};

		switch(pname){
			case "DTLZ1":
				targetPoint = Geometry.lineCrossDTLZ1HyperplanePoint(Geometry.invert(dmr.getLambda()));
				break;
			case "DTLZ2":
			case "DTLZ3":
			case "DTLZ4":
				targetPoint = Geometry.lineCrossDTLZ234HyperspherePoint(Geometry.invert(dmr.getLambda()));
				break;
		}
		System.out.println("TARGET POINT: ");
		for(double d : targetPoint){
			System.out.print(d + " ");
		}
		System.out.println();
		
		System.out.println("PREF: ");
		for(int i=0; i< prob.getNumObjectives(); i++){
			double min = Double.MAX_VALUE, sum = 0, max = -Double.MAX_VALUE;
			for(Solution s : res.getSolutions()){
				double o = s.getObjective(i);
				min = Double.min(min, o);
				max = Double.max(max, o);
				sum += o;
			}
			
			System.out.println(i + ": " + min + ", " + sum/res.getSolutions().size() + ", ");
			
		}
		ExecutionHistory.getInstance().setFinalMinDist(MyMath.getMinDist(targetPoint, res));
		ExecutionHistory.getInstance().setFinalAvgDist(MyMath.getAvgDist(targetPoint, res));
		
		Hyperplane hp = new Hyperplane(3, 100);
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
		for(ReferencePoint rp : hp.getReferencePoints()){
			double lambda[] = Geometry.normalize(Geometry.invert(rp.getDim()));
			double point[] = gls.lambda2theta(lambda);
			for(int i=0; i<3; i++){
				System.out.print(point[i] + " ");
			}
			System.out.println(Lambda.evaluateLambda(new ReferencePoint(lambda)));
		}
	}

	public Hyperplane getHyperplane(){
		return hyperplane;
	}
	
	public int getGeneration(){
		return generation;
	}
}
