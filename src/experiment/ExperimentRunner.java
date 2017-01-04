package experiment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import core.Population;
import core.Problem;
import core.RST_NSGAIII;
import core.points.Solution;
import history.ExecutionHistory;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import utils.Pair;

public class ExperimentRunner {
	private static ArrayList<Problem> problems = new ArrayList<Problem>();
	private static HashMap<Pair<String, Integer>, Integer> numGenerationsMap = new HashMap<>();
	private static HashMap<Integer, Integer> popSizeMap = new HashMap<>();
	private static ArrayList<Integer> decisionMakerRankers = new ArrayList<Integer>();
	public static void main(String[] args) {
		int numRuns = 5;
		initExecutionData();
		for(Integer rankerId : decisionMakerRankers){
			for (Problem p : problems) {
				for (int runId = 1; runId <= numRuns; runId++) {
					System.out.println("Run " + runId + "/" + numRuns );
					ChebyshevRanker decisionMakerRanker = null;
					if(rankerId == 0){
						decisionMakerRanker = ChebyshevRankerBuilder.getCentralChebyshevRanker(p.getNumObjectives());
					} else if(rankerId == 1){
						decisionMakerRanker = ChebyshevRankerBuilder.getMinXZChebyshevRanker(p.getNumObjectives());
					}
					runNSGAIIIExperiment(p, runId, decisionMakerRanker);
					//runSingleObjectiveEA(p, runId);
				}
			}
		}
	}

	private static void runNSGAIIIExperiment(Problem p, int runId, ChebyshevRanker decisionMakerRanker) {
		//NSGAIII alg = new NSGAIII(p, numGenerationsMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())) 1000, true, 25);
		RST_NSGAIII alg = new RST_NSGAIII(p, numGenerationsMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())), 20, decisionMakerRanker);
		alg.run();
		
		ExecutionHistory history = ExecutionHistory.getInstance();
		alg.evaluateFinalResult(history.getGeneration(alg.getGeneration()));
		System.out.println(p.getName() + " " + p.getNumObjectives() + " " + p.getNumVariables());
		System.out.println("Final min: " + history.getFinalMinDist());
		System.out.println("Fonal avg: " + history.getFinalAvgDist());
		//saveHistory(alg.getHistory(), "RST_NSGAIII_" + p.getName() + '_' + p.getNumObjectives() + '_' + runId, false);
	}

//	private static void runSingleObjectiveEA(Problem p, int runId) {
//		int numObj = p.getNumObjectives();
//		SingleObjectiveEA soea = new SingleObjectiveEA(p,
//				/*numGenerationsMap.get(new Pair<String, Integer>(p.getName(), numObj))*/ 1000, popSizeMap.get(numObj),
//				ChebyshevRankerBuilder.getCentralChebyshevRanker(numObj));
//		soea.run();
//		saveHistory(soea.getHistory(), "SingleCrit_" + p.getName() + '_' + numObj + '_' + runId, true);
//	}

	private static void initExecutionData() {
		decisionMakerRankers.add(0);
		decisionMakerRankers.add(1);
		int numObjectives[] = {3, 5, 8, 10, 15 };
		for (int no : numObjectives) {
			problems.add(new DTLZ1(no));
			problems.add(new DTLZ2(no)); 
			problems.add(new DTLZ3(no));
			problems.add(new DTLZ4(no));

//			problems.add(new WFG6(no));
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
		
		popSizeMap.put(3, 92);
		popSizeMap.put(5, 212);
		popSizeMap.put(8, 156);
		popSizeMap.put(10, 276);
		popSizeMap.put(15, 136);
	}

	private static void saveHistory(ExecutionHistory history, String filename, boolean soea) {
		Path file = Paths.get("experimentResults/" + filename);
		ArrayList<String> lines = new ArrayList<String>();

		// Add data description
		lines.add(history.getGenerations().size() + " " + history.getPopulationSize() + " "
				+ history.getNumVariables() + " "
				+ history.getNumObjectives() + " ");
		
		//saveSolutions(lines, history);
		
		//Single Objective Evolutionary Algorithm does not use Solution Dirs and Chebyshev Dirs
		if(!soea){
			//saveSolutionDirs(lines, history);
			//saveChebyshevDirs(lines, history);
		}
		
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveSolutions(ArrayList<String> lines, ExecutionHistory history){
		StringBuffer sb = new StringBuffer();
		for (int generationId = 0; generationId < history.getGenerations().size(); generationId++) {
			Population p = history.getGeneration(generationId);
			for (int j = 0; j < p.size(); j++) {
				Solution s = p.getSolution(j);
				for (double var : s.getVariables()) {
					sb.append(var);
					sb.append(" ");
				}
				sb.append("\n");
				for (double obj : s.getObjectives()) {
					sb.append(obj);
					sb.append(" ");
				}
				sb.append("\n");
			}
		}
		lines.add(sb.toString());
	}
}