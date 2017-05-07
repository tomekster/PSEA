package experiment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.LogManager;

import core.Evaluator;
import core.Problem;
import core.algorithm.RST_NSGAIII;
import core.algorithm.SingleObjectiveEA;
import core.points.Solution;
import history.ExecutionHistory;
import preferences.PreferenceCollector;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import utils.MyMath;
import utils.Pair;

public class ExperimentRunner {
	private static ArrayList<Problem> problems = new ArrayList<Problem>();
	
	private static HashMap<Pair<String, Integer>, ExecutionParameters> executionParametersMap = new HashMap<>();
	
	private static ArrayList<ChebyshevRanker> decisionMakerRankers = null;
	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		int numRuns = 20;
		initExecutionData();
		for (Problem p : problems) {
			double idealPoint[] = p.findIdealPoint();
			decisionMakerRankers = ChebyshevRankerBuilder.getExperimentalRankers(p.getNumObjectives(), idealPoint);
			for(ChebyshevRanker cr : decisionMakerRankers){
				
				int numGen = 3000;
				for (int runId = 1; runId <= numRuns; runId++) {
					double minDist[] = new double[numGen+1], avgDist[] = new double[numGen+1];
					System.out.print("Run: " + runId + "/" + numRuns );
					System.out.print(" Problem: " + p.getName());
					System.out.print(" NumObjectives: " + p.getNumObjectives());
					System.out.print(" Ideal: " + Arrays.toString(idealPoint));
					System.out.println(" DecisionMaker: " + cr.getName());
					
					runSingleObjectiveExperiment(p, cr, numGen, minDist, avgDist);
//					runNSGAIIIExperiment(p, cr);
//					ExecutionHistory.serialize("/home/tomasz/Desktop/experiment/" + p.getName() + "_" + p.getNumObjectives() + "_" + runId + "_" + rankerId + "_" + NSGAIIIRandom.getInstance().nextInt() + ".ser");
//					System.out.println("================================");
					writeResultToFile(p.getName(), cr.getName(), runId, minDist, avgDist);
				}
//				for(int i=0; i<=numGen; i++){
//					minDist[i] /= numRuns;
//					avgDist[i] /= numRuns;
//				}
//				System.out.println();
			}
		}
	}

	private static void runSingleObjectiveExperiment(Problem p, ChebyshevRanker cr, int numGen, double minDist[], double avgDist[]) {
		SingleObjectiveEA so = new SingleObjectiveEA(p, cr, 100);
//		System.out.println("Lambda: " + Arrays.toString(cr.getLambda()));
//		System.out.println("Target: " + Arrays.toString(p.getTargetPoint(cr.getLambda())));
		for(int i=0; i<=numGen; i++){
			minDist[i] += MyMath.getMinDist(p.getTargetPoint(cr.getLambda()), so.getPopulation());
			avgDist[i] += MyMath.getAvgDist(p.getTargetPoint(cr.getLambda()), so.getPopulation()); 
			so.nextGeneration();
		}
		
//		System.out.println("OBJrange");
//		for(int i=0; i< p.getNumObjectives(); i++){
//			double min = Double.MAX_VALUE, sum = 0, max = -Double.MAX_VALUE;
//			for(Solution s : so.getPopulation().getSolutions()){
//				double o = s.getObjective(i);
//				min = Double.min(min, o);
//				max = Double.max(max, o);
//				sum += o;
//			}
//			System.out.println("OBJ" + i + ": " + min + ", " + sum/so.getPopulation().getSolutions().size() + ", " + max);
//		}
	}
	
	private static void writeResultToFile(String problemName, String rankerName, int runId, double[] minDist, double[] avgDist) {
		try{
			PrintWriter writer = new PrintWriter("SingleObjective/" + problemName + "_" + rankerName + "_" + runId, "UTF-8");
			for(int i=0; i<minDist.length; i++){
				writer.println(i + ", " + minDist[i] + ", " + avgDist[i]);
			}
			writer.close();
		} catch (IOException e) {
		}
		System.out.println(minDist[minDist.length - 1] + ", " + avgDist[avgDist.length - 1]);
	}

	private static void runNSGAIIIExperiment(Problem p, ChebyshevRanker decisionMakerRanker) {
//		NSGAIII alg = new NSGAIII(p, numGenerationsMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())) 1000, true, 25);
		PreferenceCollector.getInstance().clear();
		RST_NSGAIII alg = new RST_NSGAIII(p, 
				executionParametersMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				decisionMakerRanker
			);
		alg.run();
		
		ExecutionHistory history = ExecutionHistory.getInstance();
		Evaluator.evaluateRun(p, decisionMakerRanker, alg.getPopulation());
		System.out.println("Final min: " + history.getFinalMinDist());
		System.out.println("Final avg: " + history.getFinalAvgDist());
		
//		Population finalPop = history.getGeneration(history.getGenerations().size()-1);
//		Population firstFront = NonDominationRanker.sortPopulation(finalPop).get(0);		
//		PythonVisualizer pv = new PythonVisualizer();
//		pv.visualise(history.getProblem().getReferenceFront(), firstFront);
//		pv.visualise(history.getProblem().getReferenceFront(), finalPop);
//		saveHistory(alg.getHistory(), "RST_NSGAIII_" + p.getName() + '_' + p.getNumObjectives() + '_' + runId, false);
	}

	private static void initExecutionData() {
		int numObjectives[] = {5};
		for (int no : numObjectives) {
			problems.add(new DTLZ1(no));
			problems.add(new DTLZ2(no)); 
//			problems.add(new DTLZ3(no));
//			problems.add(new DTLZ4(no));

//			problems.add(new WFG1(no));
//			problems.add(new WFG6(3));
//			problems.add(new WFG7(no));
		}
		
		int elicInter = 1;
		int numLambdas = 50;
		double spreadThresh = 0.9;
		
		//TODO - temprory placeholder
		int numExplor = 300, numExploit = 300, numElic1 = 50, numElic2 = 30;
		
		//popSize, numGen, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh
		executionParametersMap.put(new Pair<String, Integer>("DTLZ1", 3), new ExecutionParameters(92, 400, 150, 150, 50, 30, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ1", 5), new ExecutionParameters(212, 600, 200, 200, 50, 30, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ1", 8), new ExecutionParameters(156, 750, 300, 300, 50, 30, elicInter, numLambdas, spreadThresh));
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ1", 10), new ExecutionParameters(276, 1000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ1", 15), new ExecutionParameters(136, 1500, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
		
		executionParametersMap.put(new Pair<String, Integer>("DTLZ2", 3), new ExecutionParameters(92, 250, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ2", 5), new ExecutionParameters(212, 350, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ2", 8), new ExecutionParameters(156, 500, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ2", 10), new ExecutionParameters(276, 750, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ2", 15), new ExecutionParameters(136, 1000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
		
		executionParametersMap.put(new Pair<String, Integer>("DTLZ3", 3), new ExecutionParameters(92, 1000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ3", 5), new ExecutionParameters(212, 1000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ3", 8), new ExecutionParameters(156, 1000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ3", 10), new ExecutionParameters(276, 1500, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ3", 15), new ExecutionParameters(136, 2000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
		
		executionParametersMap.put(new Pair<String, Integer>("DTLZ4", 3), new ExecutionParameters(92, 600, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ4", 5), new ExecutionParameters(212, 1000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("DTLZ4", 8), new ExecutionParameters(156, 1250, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ4", 10), new ExecutionParameters(276, 2000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))
//		executionParametersMap.put(new Pair<String, Integer>("DTLZ4", 15), new ExecutionParameters(136, 3000, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdas, spreadThresh))

		numElic1 = 50;
		numElic2 = 30;
		
		executionParametersMap.put(new Pair<String, Integer>("WFG1", 2), new ExecutionParameters(92, 400, 100, 100, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG1", 3), new ExecutionParameters(92, 400, 150, 150, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG1", 5), new ExecutionParameters(212, 750, 200, 200, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG1", 8), new ExecutionParameters(156, 1500, 300, 300, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		
		executionParametersMap.put(new Pair<String, Integer>("WFG6", 2), new ExecutionParameters(92, 400, 100, 100, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG6", 3), new ExecutionParameters(92, 400, 150, 150, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG6", 5), new ExecutionParameters(212, 750, 200, 200, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG6", 8), new ExecutionParameters(156, 1500, 300, 300, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		
		executionParametersMap.put(new Pair<String, Integer>("WFG7", 2), new ExecutionParameters(92, 400, 100, 100, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG7", 3), new ExecutionParameters(92, 400, 150, 150, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG7", 5), new ExecutionParameters(212, 750, 200, 200, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
		executionParametersMap.put(new Pair<String, Integer>("WFG7", 8), new ExecutionParameters(156, 1500, 300, 300, numElic1, numElic2, elicInter, numLambdas, spreadThresh));
	}
}