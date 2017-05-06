package experiment;

import java.util.ArrayList;
import java.util.HashMap;

import core.Evaluator;
import core.Population;
import core.Problem;
import core.algorithm.RST_NSGAIII;
import history.ExecutionHistory;
import igd.IGD;
import preferences.PreferenceCollector;
import problems.wfg.*;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import solutionRankers.NonDominationRanker;
import utils.NSGAIIIRandom;
import utils.Pair;
import utils.WfgPythonVisualizer;

public class ExperimentRunner {
	private static ArrayList<Problem> problems = new ArrayList<Problem>();
	private static HashMap<Pair<String, Integer>, Integer> numGenerationsMap = new HashMap<>();
	
	private static HashMap<Pair<String, Integer>, Integer> numExplorGenMap = new HashMap<>();
	private static HashMap<Pair<String, Integer>, Integer> numExploitGenMap = new HashMap<>();
	private static HashMap<Pair<String, Integer>, Integer> numElicitations1Map = new HashMap<>();
	private static HashMap<Pair<String, Integer>, Integer> numElicitations2Map = new HashMap<>();
	private static HashMap<Pair<String, Integer>, Integer> elicitationsIntervalMap = new HashMap<>();
	private static HashMap<Pair<String, Integer>, Integer> numLambdasMap = new HashMap<>();
	private static HashMap<Pair<String, Integer>, Double>  spreadThresholdMap = new HashMap<>();
	
	private static HashMap<Integer, Integer> popSizeMap = new HashMap<>();
	private static ArrayList<Integer> decisionMakerRankers = new ArrayList<Integer>();
	public static void main(String[] args) {
		int numRuns = 1;
		initExecutionData();
		for(Integer rankerId : decisionMakerRankers){
			for (Problem p : problems) {
				for (int runId = 1; runId <= numRuns; runId++) {
					System.out.println("Run " + runId + "/" + numRuns );
					ChebyshevRanker decisionMakerRanker = null;
					if(rankerId == 0){
						decisionMakerRanker = ChebyshevRankerBuilder.get1CentralChebyshevRanker(p.getNumObjectives());
					} else if(rankerId == 1){
						decisionMakerRanker = ChebyshevRankerBuilder.getMinXZChebyshevRanker(p.getNumObjectives());
					}
					runNSGAIIIExperiment(p, runId, decisionMakerRanker);
//					ExecutionHistory.serialize("/home/tomasz/Desktop/experiment/" + p.getName() + "_" + p.getNumObjectives() + "_" + runId + "_" + rankerId + "_" + NSGAIIIRandom.getInstance().nextInt() + ".ser");
				}
			}
		}
	}

	private static void runNSGAIIIExperiment(Problem p, int runId, ChebyshevRanker decisionMakerRanker) {
		//NSGAIII alg = new NSGAIII(p, numGenerationsMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())) 1000, true, 25);
		PreferenceCollector.getInstance().clear();
		RST_NSGAIII alg = new RST_NSGAIII(p, 
				numExplorGenMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				numExploitGenMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				numElicitations1Map.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				numElicitations2Map.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				elicitationsIntervalMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				decisionMakerRanker,
				numLambdasMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())),
				spreadThresholdMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())));
		alg.run();
		
		ExecutionHistory history = ExecutionHistory.getInstance();
		Evaluator.evaluateRun(p, decisionMakerRanker, alg.getPopulation());
		System.out.println(p.getName() + " " + p.getNumObjectives() + " " + p.getNumVariables());
		System.out.println("Final min: " + history.getFinalMinDist());
		System.out.println("Fonal avg: " + history.getFinalAvgDist());
		
		Population finalPop = history.getGeneration(history.getGenerations().size()-1);
		
		Population firstFront = NonDominationRanker.sortPopulation(finalPop).get(0);		
		WfgPythonVisualizer pv = new WfgPythonVisualizer();
//		pv.visualise(history.getProblem().getReferenceFront(), firstFront);
//		pv.visualise(history.getProblem().getReferenceFront(), finalPop);
		
		
		//saveHistory(alg.getHistory(), "RST_NSGAIII_" + p.getName() + '_' + p.getNumObjectives() + '_' + runId, false);
	}

	private static void initExecutionData() {
		decisionMakerRankers.add(0);
		decisionMakerRankers.add(1);
		int numObjectives[] = {2};
		for (int no : numObjectives) {
//			problems.add(new DTLZ1(no));
//			problems.add(new DTLZ2(no)); 
//			problems.add(new DTLZ3(no));
//			problems.add(new DTLZ4(no));

//			problems.add(new WFG1(no));
			problems.add(new WFG6(3));
//			problems.add(new WFG7(no));
		}

		// Map from <ProblemName, NumObjectives> to NumGenerations
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ1", 3), 400);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ1", 5), 600);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ1", 8), 750);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ1", 10), 1000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ1", 15), 1500);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ2", 3), 250);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ2", 5), 350);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ2", 8), 500);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ2", 10), 750);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ2", 15), 1000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ3", 3), 1000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ3", 5), 1000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ3", 8), 1000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ3", 10), 1500);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ3", 15), 2000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ4", 3), 600);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ4", 5), 1000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ4", 8), 1250);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ4", 10), 2000);
		numGenerationsMap.put(new Pair<String, Integer>("DTLZ4", 15), 3000);
		numGenerationsMap.put(new Pair<String, Integer>("WFG1", 3), 400);
		numGenerationsMap.put(new Pair<String, Integer>("WFG1", 5), 750);
		numGenerationsMap.put(new Pair<String, Integer>("WFG1", 8), 1500);
		numGenerationsMap.put(new Pair<String, Integer>("WFG6", 3), 400);
		numGenerationsMap.put(new Pair<String, Integer>("WFG6", 5), 750);
		numGenerationsMap.put(new Pair<String, Integer>("WFG6", 8), 1500);
		numGenerationsMap.put(new Pair<String, Integer>("WFG6", 10), 2000);
		numGenerationsMap.put(new Pair<String, Integer>("WFG6", 15), 3000);
		numGenerationsMap.put(new Pair<String, Integer>("WFG7", 3), 400);
		numGenerationsMap.put(new Pair<String, Integer>("WFG7", 5), 750);
		numGenerationsMap.put(new Pair<String, Integer>("WFG7", 8), 1500);
		numGenerationsMap.put(new Pair<String, Integer>("WFG7", 10), 2000);
		numGenerationsMap.put(new Pair<String, Integer>("WFG7", 15), 3000);
		
		
		numExplorGenMap.put(new Pair<String, Integer>("DTLZ1", 2), 100);
		numExplorGenMap.put(new Pair<String, Integer>("DTLZ1", 3), 150);
		numExplorGenMap.put(new Pair<String, Integer>("DTLZ1", 5), 200);
		numExplorGenMap.put(new Pair<String, Integer>("DTLZ1", 8), 300);
		numExploitGenMap.put(new Pair<String, Integer>("DTLZ1", 2), 100);
		numExploitGenMap.put(new Pair<String, Integer>("DTLZ1", 3), 150);
		numExploitGenMap.put(new Pair<String, Integer>("DTLZ1", 5), 200);
		numExploitGenMap.put(new Pair<String, Integer>("DTLZ1", 8), 300);
		numElicitations1Map.put(new Pair<String, Integer>("DTLZ1", 2), 50);
		numElicitations1Map.put(new Pair<String, Integer>("DTLZ1", 3), 50);
		numElicitations1Map.put(new Pair<String, Integer>("DTLZ1", 5), 50);
		numElicitations1Map.put(new Pair<String, Integer>("DTLZ1", 8), 50);
		numElicitations2Map.put(new Pair<String, Integer>("DTLZ1", 3), 30);
		numElicitations2Map.put(new Pair<String, Integer>("DTLZ1", 5), 30);
		numElicitations2Map.put(new Pair<String, Integer>("DTLZ1", 8), 30);
		elicitationsIntervalMap.put(new Pair<String, Integer>("DTLZ1", 2), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("DTLZ1", 3), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("DTLZ1", 5), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("DTLZ1", 8), 1);
		
		numExplorGenMap.put(new Pair<String, Integer>("WFG1", 2), 100);
		numExplorGenMap.put(new Pair<String, Integer>("WFG1", 3), 150);
		numExplorGenMap.put(new Pair<String, Integer>("WFG1", 5), 200);
		numExplorGenMap.put(new Pair<String, Integer>("WFG1", 8), 300);
		numExploitGenMap.put(new Pair<String, Integer>("WFG1", 2), 100);
		numExploitGenMap.put(new Pair<String, Integer>("WFG1", 3), 150);
		numExploitGenMap.put(new Pair<String, Integer>("WFG1", 5), 200);
		numExploitGenMap.put(new Pair<String, Integer>("WFG1", 8), 300);
		numElicitations1Map.put(new Pair<String, Integer>("WFG1", 2), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG1", 3), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG1", 5), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG1", 8), 50);
		numElicitations2Map.put(new Pair<String, Integer>("WFG1", 2), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG1", 3), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG1", 5), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG1", 8), 30);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG1", 2), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG1", 3), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG1", 5), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG1", 8), 1);
		
		numExplorGenMap.put(new Pair<String, Integer>("WFG2", 2), 100);
		numExplorGenMap.put(new Pair<String, Integer>("WFG2", 3), 150);
		numExplorGenMap.put(new Pair<String, Integer>("WFG2", 5), 200);
		numExplorGenMap.put(new Pair<String, Integer>("WFG2", 8), 300);
		numExploitGenMap.put(new Pair<String, Integer>("WFG2", 2), 100);
		numExploitGenMap.put(new Pair<String, Integer>("WFG2", 3), 150);
		numExploitGenMap.put(new Pair<String, Integer>("WFG2", 5), 200);
		numExploitGenMap.put(new Pair<String, Integer>("WFG2", 8), 300);
		numElicitations1Map.put(new Pair<String, Integer>("WFG2", 2), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG2", 3), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG2", 5), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG2", 8), 50);
		numElicitations2Map.put(new Pair<String, Integer>("WFG2", 2), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG2", 3), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG2", 5), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG2", 8), 30);
		numLambdasMap.put(new Pair<String, Integer>("WFG2", 2), 50);
		numLambdasMap.put(new Pair<String, Integer>("WFG2", 3), 50);
		numLambdasMap.put(new Pair<String, Integer>("WFG2", 5), 50);
		numLambdasMap.put(new Pair<String, Integer>("WFG2", 8), 50);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG2", 2), 0.9);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG2", 3), 0.9);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG2", 5), 0.9);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG2", 8), 0.9);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG2", 2), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG2", 3), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG2", 5), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG2", 8), 1);
		
		numExplorGenMap.put(new Pair<String, Integer>("WFG6", 2), 100);
		numExplorGenMap.put(new Pair<String, Integer>("WFG6", 3), 150);
		numExplorGenMap.put(new Pair<String, Integer>("WFG6", 5), 200);
		numExplorGenMap.put(new Pair<String, Integer>("WFG6", 8), 300);
		numExploitGenMap.put(new Pair<String, Integer>("WFG6", 2), 100);
		numExploitGenMap.put(new Pair<String, Integer>("WFG6", 3), 150);
		numExploitGenMap.put(new Pair<String, Integer>("WFG6", 5), 200);
		numExploitGenMap.put(new Pair<String, Integer>("WFG6", 8), 300);
		numElicitations1Map.put(new Pair<String, Integer>("WFG6", 2), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG6", 3), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG6", 5), 50);
		numElicitations1Map.put(new Pair<String, Integer>("WFG6", 8), 50);
		numElicitations2Map.put(new Pair<String, Integer>("WFG6", 2), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG6", 3), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG6", 5), 30);
		numElicitations2Map.put(new Pair<String, Integer>("WFG6", 8), 30);
		numLambdasMap.put(new Pair<String, Integer>("WFG6", 2), 50);
		numLambdasMap.put(new Pair<String, Integer>("WFG6", 3), 50);
		numLambdasMap.put(new Pair<String, Integer>("WFG6", 5), 50);
		numLambdasMap.put(new Pair<String, Integer>("WFG6", 8), 50);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG6", 2), 0.9);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG6", 3), 0.9);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG6", 5), 0.9);
		spreadThresholdMap.put(new Pair<String, Integer>("WFG6", 8), 0.9);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG6", 2), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG6", 3), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG6", 5), 1);
		elicitationsIntervalMap.put(new Pair<String, Integer>("WFG6", 8), 1);
		
		popSizeMap.put(2, 92);
		popSizeMap.put(3, 92);
		popSizeMap.put(5, 212);
		popSizeMap.put(8, 156);
		popSizeMap.put(10, 276);
		popSizeMap.put(15, 136);
	}
}