package core;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.Hyperplane;
import core.points.ReferencePoint;
import core.points.Solution;
import history.ExecutionHistory;
import igd.IGD;
import igd.TargetFrontGenerator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.Elicitator;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import solutionRankers.LambdaCVRanker;
import solutionRankers.NonDominationRanker;
import utils.Geometry;
import utils.MyMath;
import utils.Pair;

public class RST_NSGAIII extends EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII.class.getName());

	private Problem problem;
	private int numGenerations;
	private int populationSize;
	private int elicitationInterval;
	private int generation;
	
	private Hyperplane hyperplane;
	private ChebyshevRanker decisionMakerRanker;
	private ExecutionHistory history;
	
	private NSGAIII nsgaiii;
	private Lambda lambda;

	public RST_NSGAIII(Problem problem, int numGenerations, int elicitationInterval) {
		super(  new BinaryTournament(new NonDominationRanker()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		
		this.nsgaiii = new NSGAIII(	problem, 
									new BinaryTournament(new NonDominationRanker()),
									new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
									new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		double lambdaLowerBound[] = new double[problem.getNumObjectives()];
		double lambdaUpperBound[] = new double[problem.getNumObjectives()];
		for(int i=0; i<problem.getNumObjectives(); i++){
			lambdaLowerBound[i] = 0.0;
			lambdaUpperBound[i] = 1.0;
		}
		
		this.lambda = new Lambda(	problem.getNumObjectives(),
									new BinaryTournament(new LambdaCVRanker()),
									new SBX(1.0, 30.0, lambdaLowerBound, lambdaUpperBound),
									new PolynomialMutation(1.0 / problem.getNumObjectives(), 20.0, lambdaLowerBound, lambdaUpperBound));

		// Hyperplane is one of basic constructs used in NSGA-III algorithm. It is responsible 
		// for keeping solutions uniformly spread among objective space. 
		// In modified NSGA-III it is used to store information about directions which are interesting 
		// from DM's point of view, based on preference information elicitated during algorithm run 
		this.hyperplane = new Hyperplane(problem.getNumObjectives());

		// Number of solutions in every generation. Depends on Hyperplane because number 
		// of solutions in population should be close to number of Reference Points on Hyperplane
		populationSize = hyperplane.getReferencePoints().size();
		populationSize += populationSize % 2;
		population = problem.createPopulation(populationSize);

		// Parameters of algorithm execution
		this.numGenerations = numGenerations;
		this.problem = problem;
		this.elicitationInterval = elicitationInterval;
		this.decisionMakerRanker = ChebyshevRankerBuilder.getMinXZChebyshevRanker(problem.getNumObjectives());
		
		// Structure for storing intermediate state of algorithm for further
		// analysis, display, etc.
		this.history = new ExecutionHistory();
		history.setNumVariables(problem.getNumVariables());
		history.setNumObjectives(problem.getNumObjectives());
		history.addPreferenceGeneration(population.copy());
		history.addSpreadGeneration(nsgaiii.getPopulation());
		history.addSolutionDirections(this.hyperplane.getReferencePoints());
		history.addLambdas(lambda.getLambdas());
		history.setTargetPoints(TargetFrontGenerator.generate(this.hyperplane.getReferencePoints(), problem));
		history.setPreferenceCollector(lambda.getPreferenceCollector());
		history.setChebyshevRanker(decisionMakerRanker);
	}

	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations + " generations.");

		for (generation = 0; generation < numGenerations; generation++) {
			if(generation > 0 && generation % elicitationInterval == 0) elicitate();
				
			nsgaiii.nextGeneration();
			lambda.nextGeneration();
			
			assert nsgaiii.getPopulation().size() == populationSize;
			
			//Mix RST-NSGAIII population with NSGA-III population to avoid degeneration in high-dimensional spaces
			if(generation % elicitationInterval == 0){
				population.addSolutions(nsgaiii.getPopulation());
				assert population.size() == 2*populationSize;
			}
			
			nextGeneration();
					
			problem.evaluate(population);
			history.addPreferenceGeneration(population.copy());
			history.addSpreadGeneration(nsgaiii.getPopulation().copy());
			history.addSolutionDirections(hyperplane.getReferencePoints());
			history.addLambdas((ArrayList <ReferencePoint>)lambda.getLambdas().clone());
			history.addBestChebVal(evaluateGeneration(population));
		}
	}

	private void elicitate() {
		System.out.println("GENERATION: " + generation);
		NonDominationRanker ndr = new NonDominationRanker();
		Population firstFront = ndr.sortPopulation(population).get(0);
		if (firstFront.size() > 1){
			Elicitator.elicitate(firstFront, decisionMakerRanker, lambda.getPreferenceCollector());
			lambda.setElicitated(true);
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
	
	public double evaluateFinalResult(Population spreadResult, Population prefResult){
		ArrayList<ReferencePoint> referencePoints = this.hyperplane.getReferencePoints();
		double igd = IGD.execute(TargetFrontGenerator.generate(referencePoints, problem), spreadResult);
		evaluateRun(problem, decisionMakerRanker, spreadResult, prefResult);
		return igd;
	}
	
	private void evaluateRun(Problem prob, ChebyshevRanker dmr, Population spreadResult, Population prefResult) {
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
		for(int i=0; i<prefResult.getSolution(0).getNumObjectives(); i++){
			double min = Double.MAX_VALUE, sum = 0, max = -Double.MAX_VALUE;
			for(Solution s : prefResult.getSolutions()){
				double o = s.getObjective(i);
				min = Double.min(min, o);
				max = Double.max(max, o);
				sum += o;
			}
			
			System.out.println(i + ": " + min + ", " + sum/prefResult.getSolutions().size() + ", " + max);
			
		}
		history.setFinalSpreadMinDist(MyMath.getMinDist(targetPoint, spreadResult));
		history.setFinalSpreadAvgDist(MyMath.getAvgDist(targetPoint, spreadResult));
		history.setFinalPrefMinDist(MyMath.getMinDist(targetPoint, prefResult));
		history.setFinalPrefAvgDist(MyMath.getAvgDist(targetPoint, prefResult));
	}

	public Hyperplane getHyperplane(){
		return hyperplane;
	}
	
	public int getGeneration(){
		return generation;
	}
	
	public ExecutionHistory getHistory(){
		return history;
	}
}
