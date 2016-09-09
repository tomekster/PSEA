package experiment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import core.NSGAIII;
import core.Population;
import core.Problem;
import core.SingleObjectiveEA;
import core.Solution;
import core.hyperplane.ReferencePoint;
import history.NSGAIIIHistory;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import solutionRankers.ChebyshevRankerBuilder;
import utils.Pair;

public class ExperimentRunner {
	private static ArrayList<Problem> problems = new ArrayList<Problem>();
	private static HashMap<Pair<String, Integer>, Integer> numGenerationsMap = new HashMap<>();
	private static HashMap<Integer, Integer> popSizeMap = new HashMap<>();
	
	public static void main(String[] args) {
		int numRuns = 5;
		initExecutionData();
		for (Problem p : problems) {
			for (int runId = 1; runId <= numRuns; runId++) {
				System.out.println("Run " + runId + "/" + numRuns );
				runNSGAIIIExperiment(p, runId);
				runSingleObjectiveEA(p, runId);
			}
		}
	}

	private static void runNSGAIIIExperiment(Problem p, int runId) {
		NSGAIII alg = new NSGAIII(p, numGenerationsMap.get(new Pair<String, Integer>(p.getName(), p.getNumObjectives())), true, 25, 0);
		alg.run();
		saveHistory(alg.getHistory(), "NSGAIII_" + p.getName() + '_' + p.getNumObjectives() + '_' + runId, false);
	}

	private static void runSingleObjectiveEA(Problem p, int runId) {
		int numObj = p.getNumObjectives();
		SingleObjectiveEA soea = new SingleObjectiveEA(p,
				numGenerationsMap.get(new Pair<String, Integer>(p.getName(), numObj)), popSizeMap.get(numObj),
				ChebyshevRankerBuilder.getCentralChebyshevRanker(numObj));
		soea.run();
		saveHistory(soea.getHistory(), "SingleCrit_" + p.getName() + '_' + numObj + '_' + runId, true);
	}

	private static void initExecutionData() {
		int numObjectives[] = { /*3, 5,*/ 8, 10, 15  };
		for (int no : numObjectives) {
			problems.add(new DTLZ1(no));
			problems.add(new DTLZ2(no)); 
			problems.add(new DTLZ3(no));
			problems.add(new DTLZ4(no));
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
		
		popSizeMap.put(3, 92);
		popSizeMap.put(5, 212);
		popSizeMap.put(8, 156);
		popSizeMap.put(10, 276);
		popSizeMap.put(15, 136);
	}

	private static void saveHistory(NSGAIIIHistory history, String filename, boolean soea) {
		Path file = Paths.get("experimentResults/" + filename);
		ArrayList<String> lines = new ArrayList<String>();

		// Add data description
		lines.add(history.getNumGenerations() + " " + history.getPopulationSize() + " "
				+ history.getNumSolutionDirections()+ " "
				+ history.getNumVariables() + " "
				+ history.getNumObjectives() + " "
				+ history.getRACSCount().size());
		
		//saveSolutions(lines, history);
		
		//Single Objective Evolutionary Algorithm does not use Solution Dirs and Chebyshev Dirs
		if(!soea){
			//saveSolutionDirs(lines, history);
			//saveChebyshevDirs(lines, history);
		}
		
		saveBestChebVal(lines, history);
		saveBestChebSol(lines, history);
		saveRacsCount(lines, history);
		
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveSolutions(ArrayList<String> lines, NSGAIIIHistory history){
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
	
	private static void saveSolutionDirs(ArrayList<String> lines, NSGAIIIHistory history) {
		StringBuffer sb = new StringBuffer();
		for (int generationId = 0; generationId < history.getGenerations().size(); generationId++) {
			// Add solution directions
			ArrayList<ReferencePoint> solDirs = history.getSolutionDirections(generationId);
			for (int j = 0; j < solDirs.size(); j++) {
				ReferencePoint solDir = solDirs.get(j);
				for (double dim : solDir.getDim()) {
					sb.append(dim);
					sb.append(" ");
				}
				sb.append("\n");
			}
		}
		lines.add(sb.toString());
	}

	private static void saveChebyshevDirs(ArrayList<String> lines, NSGAIIIHistory history) {
		StringBuffer sb = new StringBuffer();
		for (int generationId = 0; generationId < history.getGenerations().size(); generationId++) {
			ArrayList<ReferencePoint> chebDirs = history.getSolutionDirections(generationId);
			for (int j = 0; j < chebDirs.size(); j++) {
				ReferencePoint chebDir = chebDirs.get(j);
				for (double dim : chebDir.getDim()) {
					sb.append(dim);
					sb.append(" ");
				}
				sb.append("\n");
			}
		}
		lines.add(sb.toString());
	}

	private static void saveBestChebVal(ArrayList<String> lines, NSGAIIIHistory history) {
		StringBuffer sb = new StringBuffer();
		for (int generationId = 0; generationId < history.getNumGenerations(); generationId++) {
			sb.append(history.getBestChebVal(generationId));
			sb.append("\n");
		}
		lines.add(sb.toString());
	}
	
	private static void saveBestChebSol(ArrayList<String> lines, NSGAIIIHistory history) {
		StringBuffer sb = new StringBuffer();
		for (int generationId = 0; generationId < history.getNumGenerations(); generationId++) {
			sb.append(history.getBestChebSol(generationId));
			sb.append("\n");
		}
		lines.add(sb.toString());
	}
	
	private static void saveRacsCount(ArrayList<String> lines, NSGAIIIHistory history) {
		StringBuffer sb = new StringBuffer();
		for (int racsCount : history.getRACSCount()) {
			sb.append(racsCount);
			sb.append("\n");
		}
		lines.add(sb.toString());
	}
}