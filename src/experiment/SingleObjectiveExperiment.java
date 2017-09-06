package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.rankers.AsfRanker;
import algorithm.rankers.AsfRankerBuilder;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import utils.math.Geometry;
import utils.math.structures.Pair;

public class SingleObjectiveExperiment {
	private static final double DEFAULT_NUM_GENERATIONS = 1500;
	private static ArrayList <Problem> problems = new ArrayList<>();
	
	private static HashMap<Pair<String, Integer>, Integer> numGenMap = new HashMap<>(); //(Problem, numObj) -> numGen
	
	public static void main(String [] args){
		
		init();
		final int NUM_RUNS = 5;
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		for(Problem problem : problems){
			double idealPoint[] = problem.findIdealPoint();
			System.out.println(Arrays.toString(idealPoint));
			ArrayList <AsfRanker> asfRankers = AsfRankerBuilder.getExperimentalRankers(problem.getNumObjectives(), idealPoint);
			for(AsfRanker asfRanker : asfRankers){
				double[] targetPoint = problem.getTargetPoint(Geometry.invert(asfRanker.getLambda()));
				ArrayList <Double> asf = new ArrayList<>();
//				for(int k=0; k<NUM_RUNS; k++){
					String runName = "SO_" + getTestName(problem, asfRanker); 
					SingleObjectiveEA so= new SingleObjectiveEA(problem, asfRanker);
					
					for(int i=0; i < DEFAULT_NUM_GENERATIONS; i++){
						so.nextGeneration();
						asf.add(so.getPopulation().getSolutions().stream().mapToDouble(s->asfRanker.eval(s)).min().getAsDouble() );
					}
					
					ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
					visData.add(PythonVisualizer.convert(asf.toArray(new Double[0]))); //Plot asf value in every generation
					ArrayList<Double> asfOptimalVal = new ArrayList<>();
					double optimalVal = asfRanker.eval(targetPoint);
					for(int i=0; i<asf.size(); i++){
						asfOptimalVal.add(optimalVal);
					}
					visData.add(PythonVisualizer.convert(asfOptimalVal.toArray(new Double[0]))); //Plot target point asf value - optimal asf value
					PythonVisualizer.saveResults(1, visData, runName);
					
					visData.clear();
					visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
					visData.add(PythonVisualizer.convert(so.getPopulation()));
					ArrayList<double[]> target= new ArrayList<>();
					target.add(targetPoint);
					visData.add(target);
					PythonVisualizer.saveResults(problem.getNumObjectives(), visData, runName + "_vis");
//				}
				System.out.println(runName + ": " + asf.get(asf.size()-1) + "/" + asfRanker.eval(targetPoint));
			}
		}
	}

	private static void init() {
		//Initialize problems
		int dim[] = {3,5,8,10,15};
		for(int d : dim){
			problems.add(new DTLZ1(d));
			problems.add(new DTLZ2(d));
			problems.add(new DTLZ3(d));
			problems.add(new DTLZ4(d));
		}
 
		//Initialize number of generations for every problem
		numGenMap.put(new Pair<String, Integer>("DTLZ1", 3) , 400);
		numGenMap.put(new Pair<String, Integer>("DTLZ1", 5) , 600);
		numGenMap.put(new Pair<String, Integer>("DTLZ1", 8) , 750);
		numGenMap.put(new Pair<String, Integer>("DTLZ1", 10) , 1000);
		numGenMap.put(new Pair<String, Integer>("DTLZ1", 15) , 1500);
		
		numGenMap.put(new Pair<String, Integer>("DTLZ2", 3) , 250);
		numGenMap.put(new Pair<String, Integer>("DTLZ2", 5) , 350);
		numGenMap.put(new Pair<String, Integer>("DTLZ2", 8) , 500);
		numGenMap.put(new Pair<String, Integer>("DTLZ2", 10) , 750);
		numGenMap.put(new Pair<String, Integer>("DTLZ2", 15) , 1000);
		
		numGenMap.put(new Pair<String, Integer>("DTLZ3", 3) , 1000);
		numGenMap.put(new Pair<String, Integer>("DTLZ3", 5) , 1000);
		numGenMap.put(new Pair<String, Integer>("DTLZ3", 8) , 1000);
		numGenMap.put(new Pair<String, Integer>("DTLZ3", 10) , 1500);
		numGenMap.put(new Pair<String, Integer>("DTLZ3", 15) , 2000);
		
		numGenMap.put(new Pair<String, Integer>("DTLZ4", 3) , 600);
		numGenMap.put(new Pair<String, Integer>("DTLZ4", 5) , 1000);
		numGenMap.put(new Pair<String, Integer>("DTLZ4", 8) , 1250);
		numGenMap.put(new Pair<String, Integer>("DTLZ4", 10) , 2000);
		numGenMap.put(new Pair<String, Integer>("DTLZ4", 15) , 3000);
	}
	
	private static int getNumGen(Problem problem){
		return numGenMap.get(new Pair<String, Integer>(problem.getName(), problem.getNumObjectives()));
	}
	
	private static String getTestName(Problem problem, AsfRanker asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
