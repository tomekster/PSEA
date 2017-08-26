package experiment;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.LogManager;

import core.Evaluator;
import core.Population;
import core.Problem;
import core.algorithm.RST_NSGAIII;
import core.algorithm.SingleObjectiveEA;
import core.points.Lambda;
import core.points.Solution;
import history.ExecutionHistory;
import preferences.PreferenceCollector;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import utils.Geometry;
import utils.MyMath;

public class ExperimentRunner {
	private static ArrayList<Problem> problems = new ArrayList<Problem>();	
	private static String DIR = "l4Cheb";
	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		int numRuns = 10;
		int numObjectives[] = {8};

		for (int no : numObjectives) {
			problems.add(new DTLZ1(no));
			problems.add(new DTLZ2(no)); 
			problems.add(new DTLZ3(no));
			problems.add(new DTLZ4(no));

//			problems.add(new WFG1(no));
//			problems.add(new WFG6(3));
//			problems.add(new WFG7(no));
		}
		
		String serializePath = "";
		String sysname = System.getProperty("os.name");
		if(sysname.toLowerCase().contains("windows")){
			serializePath = "C:/Users/Tomasz/Documents/nsgaiii/serializedRuns/";
		}
		else{
			serializePath = "/home/tomasz/Desktop/experiment/";
		}
		
		for (Problem p : problems) {
			double idealPoint[] = p.findIdealPoint();
			ArrayList<ChebyshevRanker> decisionMakerRankers = ChebyshevRankerBuilder.getExperimentalRankers(p.getNumObjectives(), idealPoint);
			for(ChebyshevRanker cr : decisionMakerRankers){
				for (int runId = 1; runId <= numRuns; runId++) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					System.out.println(dateFormat.format(date));
					ArrayList <Double> minChebDist = new ArrayList<Double>();
					ArrayList <Double> avgChebDist = new ArrayList<Double>();
					ArrayList <Double> modelChebDist = new ArrayList<Double>();
					ArrayList <Double> minEucDist = new ArrayList<Double>();
					ArrayList <Double> avgEucDist = new ArrayList<Double>();
					ArrayList <Double> modelEucDist = new ArrayList<Double>();
					
					System.out.print("Run: " + runId + "/" + numRuns );
					System.out.print(" Problem: " + p.getName());
					System.out.print(" NumObjectives: " + p.getNumObjectives());
					System.out.print(" Ideal: " + Arrays.toString(idealPoint));
					System.out.println(" DecisionMaker: " + cr.getName());
					
					runSingleObjectiveExperiment(p, cr, minChebDist, avgChebDist, modelChebDist, minEucDist, avgEucDist, modelEucDist);
//					runNSGAIIIExperiment(p, cr, minDist, avgDist, modelDist);
					ExecutionHistory.serialize(serializePath + DIR + "_" + p.getName() + "_" + p.getNumObjectives() + "_" + runId + "_" + cr.getName() + ".ser");
					writeResultToFile(p, cr.getName(), runId, minChebDist, avgChebDist, modelChebDist,  minEucDist, avgEucDist, modelEucDist);
				}
			}
		}
	}

	private static void runSingleObjectiveExperiment(Problem p, ChebyshevRanker cr, ArrayList<Double> minChebDist, ArrayList<Double> avgChebDist, ArrayList<Double> modelChebDist, ArrayList<Double> minEucDist, ArrayList<Double> avgEucDist, ArrayList<Double> modelEucDist) {
		SingleObjectiveEA so = new SingleObjectiveEA(p, cr);
//		System.out.println("Lambda: " + Arrays.toString(cr.getLambda()));
//		System.out.println("Target: " + Arrays.toString(p.getTargetPoint(cr.getLambda())));
		int numGen=1500;
		for(int i=0; i<=numGen; i++){
			
			//ChebDist
			minChebDist.add(Arrays.stream(so.getPopulation().getSolutions().toArray()).mapToDouble(s-> cr.eval((Solution)s)).min().getAsDouble());
			avgChebDist.add(Arrays.stream(so.getPopulation().getSolutions().toArray()).mapToDouble(s-> cr.eval((Solution)s)).sum()/so.getPopulation().size());
			double var [] = new double[0];
			modelChebDist.add(cr.eval(new Solution(var, p.getTargetPoint(cr.getLambda()))));
			
			//EuclidianDist
			minEucDist.add(MyMath.getMinDist(p.getTargetPoint(cr.getLambda()), so.getPopulation()));
			avgEucDist.add(MyMath.getAvgDist(p.getTargetPoint(cr.getLambda()), so.getPopulation())); 
			modelEucDist.add(.0);
			
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
	
	private static void writeResultToFile(Problem p, String rankerName, int runId, ArrayList<Double> minChebDist, ArrayList <Double> avgChebDist, ArrayList <Double> modelChebDist,  ArrayList<Double> minEucDist, ArrayList <Double> avgEucDist, ArrayList <Double> modelEucDist) {
		try{
			PrintWriter writer = new PrintWriter("C:/Users/Tomasz/Documents/nsgaiii/PSEA/" + DIR + "/OBJ" + p.getNumObjectives() + "_" + p.getName() + "_" + rankerName + "_" + runId + ".txt", "UTF-8");
			for(int i=0; i<minEucDist.size(); i++){
				writer.println(i + ", " + minChebDist.get(i) + ", " + avgChebDist.get(i) + ", " + modelChebDist.get(i) + ", " + minEucDist.get(i) + ", " + avgEucDist.get(i) + ", " + modelEucDist.get(i));
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("Error! Cannot write results to file...");
		}
		System.out.println(minEucDist.get(minEucDist.size() - 1) + ", " + avgEucDist.get(avgEucDist.size() - 1));
	}

	private static void runNSGAIIIExperiment(Problem p, ChebyshevRanker decisionMakerRanker, ArrayList<Double> minDist, ArrayList<Double> avgDist, ArrayList<Double> modelDist) {
		PreferenceCollector.getInstance().clear();
		RST_NSGAIII alg;		
		alg = new RST_NSGAIII(p,decisionMakerRanker);
		alg.run();
		
		ExecutionHistory history = ExecutionHistory.getInstance();
		for(int i=0; i < history.getGenerations().size(); i++){
			Population pop = history.getGeneration(i);
			ArrayList <Lambda> lambdas = history.getLambdas(i);
			minDist.add(MyMath.getMinDist(p.getTargetPoint(decisionMakerRanker.getLambda()), pop));
			avgDist.add(MyMath.getAvgDist(p.getTargetPoint(decisionMakerRanker.getLambda()), pop));
			double targetDir[] = decisionMakerRanker.getLambda();
			modelDist.add(lambdas.stream().mapToDouble(lambda -> Geometry.dirDist(targetDir, lambda.getDim())).min().getAsDouble());
		}
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
}