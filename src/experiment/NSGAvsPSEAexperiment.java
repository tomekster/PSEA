package experiment;

import java.util.ArrayList;
import java.util.Arrays;

import org.jzy3d.maths.Histogram;

import algorithm.nsgaiii.NSGAIII;
import algorithm.psea.PSEA;
import algorithm.rankers.AsfRanker;
import algorithm.rankers.AsfRankerBuilder;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ4;

public class NSGAvsPSEAexperiment {
	private static ArrayList <Problem> problems = new ArrayList<>();
	
	public static void main(String [] args){
		
		init();
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		for(Problem problem : problems){
			double idealPoint[] = problem.findIdealPoint();
			System.out.println(Arrays.toString(idealPoint));
			ArrayList <AsfRanker> allAsfRankers = AsfRankerBuilder.getExperimentalRankers(problem.getNumObjectives(), idealPoint);
			ArrayList <AsfRanker> asfRankers = new ArrayList<>();
			
			asfRankers.add(allAsfRankers.get(3));
			asfRankers.add(allAsfRankers.get(5));
			asfRankers.add(allAsfRankers.get(7));
			asfRankers.add(allAsfRankers.get(8));
			
			for(AsfRanker asfRanker : asfRankers){
				ArrayList<Double> minNsgaAsf = new ArrayList<>();
				ArrayList<Double> avgNsgaAsf = new ArrayList<>();
				ArrayList<Double> minPseaAsf = new ArrayList<>();
				ArrayList<Double> avgPseaAsf = new ArrayList<>();
			
				int NUM_RUNS = 3;
				String runName = getTestName(problem, asfRanker); 
				for(int runId = 0; runId < NUM_RUNS; runId++){
					PSEA psea = new PSEA(problem, asfRanker);
					psea.run();

					ExecutionHistory hist = ExecutionHistory.getInstance();
					
					NSGAIII nsgaiii = new NSGAIII(problem);
					for(int i=0; i < hist.getNumGenerations(); i++){
//						System.out.println(nsgaiii.getPopulation().getSolutions());
//						for(Solution s : nsgaiii.getPopulation().getSolutions()){
//							System.out.println(asfRanker.eval(s.getObjectives()));
//						}
//						System.out.println(nsgaiii.getPopulation().getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).min().getAsDouble());
						if(i >= minNsgaAsf.size()) minNsgaAsf.add(nsgaiii.getPopulation().getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).min().getAsDouble() /NUM_RUNS);
						else minNsgaAsf.set(i, minNsgaAsf.get(i) + nsgaiii.getPopulation().getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).min().getAsDouble() /NUM_RUNS);
						
						if(i >= avgNsgaAsf.size()) avgNsgaAsf.add(nsgaiii.getPopulation().getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).sum() / nsgaiii.getPopulation().size() /NUM_RUNS);
						else avgNsgaAsf.set(i, avgNsgaAsf.get(i) + nsgaiii.getPopulation().getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).sum() / nsgaiii.getPopulation().size() /NUM_RUNS);
						
						if(i >= minPseaAsf.size()) minPseaAsf.add(hist.getPopulation(i).getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).min().getAsDouble() /NUM_RUNS);
						else minPseaAsf.set(i, minPseaAsf.get(i) + hist.getPopulation(i).getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).min().getAsDouble() /NUM_RUNS);
						
						if(i >= avgPseaAsf.size()) avgPseaAsf.add(hist.getPopulation(i).getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).sum() / nsgaiii.getPopulation().size() /NUM_RUNS);
						else avgPseaAsf.set(i, avgPseaAsf.get(i) + hist.getPopulation(i).getSolutions().stream().mapToDouble(solution -> asfRanker.eval(solution.getObjectives())).sum() / nsgaiii.getPopulation().size() /NUM_RUNS);
						nsgaiii.nextGeneration();
					}
				}
				
				ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
				visData.add(PythonVisualizer.convert(minNsgaAsf.toArray(new Double[0])));
				visData.add(PythonVisualizer.convert(avgNsgaAsf.toArray(new Double[0])));
				visData.add(PythonVisualizer.convert(minPseaAsf.toArray(new Double[0])));
				visData.add(PythonVisualizer.convert(avgPseaAsf.toArray(new Double[0])));
				PythonVisualizer.saveResults(1, visData, runName);
			}
		}
	}

	private static void init() {
		//Initialize problems
//		int dim[] = {3,5,8,10,15};
//		for(int d : dim){
//			problems.add(new DTLZ1(d));
//			problems.add(new DTLZ2(d));
//			problems.add(new DTLZ3(d));
//			problems.add(new DTLZ4(d));
//		}
		problems.add(new DTLZ1(3));
		problems.add(new DTLZ1(5));
		problems.add(new DTLZ1(8));
		problems.add(new DTLZ4(3));
		problems.add(new DTLZ4(5));
		problems.add(new DTLZ4(8));
	}
	
	private static String getTestName(Problem problem, AsfRanker asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
