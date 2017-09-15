package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import algorithm.geneticAlgorithm.Population;
import algorithm.nsgaiii.NSGAIII;
import algorithm.rankers.NonDominationRanker;
import experiment.metrics.IGD;
import problems.Problem;
import problems.wfg.WFG1;
import problems.wfg.WFG2;
import problems.wfg.WFG3;
import problems.wfg.WFG4;
import problems.wfg.WFG5;
import problems.wfg.WFG6;
import problems.wfg.WFG7;
import problems.wfg.WFG8;
import problems.wfg.WFG9;
import utils.math.structures.Pair;

public class NSGAIIIExperiment {
	
	private static final double DEFAULT_NUM_GENERATIONS = 400;
	private static ArrayList <Problem> problems = new ArrayList<>();
	private static HashMap<Pair<String, Integer>, Integer> numGenMap = new HashMap<>(); //(Problem, numObj) -> numGen
	
	private static ArrayList <Double> best = new ArrayList<>();
	private static ArrayList <Double> median = new ArrayList<>();
	private static ArrayList <Double> worse = new ArrayList<>();
	
	public static void main(String [] args){
		
		init();
		final int NUM_RUNS = 1;
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		for(Problem problem : problems){
			ArrayList <Double> igd = new ArrayList<>();
			for(int k=0; k<NUM_RUNS; k++){
				int numObj = problem.getNumObjectives();
				String runName = getTestName(problem) + "_run" + k; 
				
				Population target = problem.getReferenceFront();
				Population firstFront = null;
				NSGAIII nsgaiii = new NSGAIII(problem);
				
				for(int i=0; i < getNumGen(problem); i++){
					nsgaiii.nextGeneration();
//					problem.evaluate(nsgaiii.getPopulation());
				}
				firstFront = NonDominationRanker.sortPopulation(nsgaiii.getPopulation()).get(0);
				igd.add(IGD.execute(target, firstFront));
				
				System.out.println(runName + " " + igd.get(igd.size()-1) );
				
				ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
				visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
				//visData.add(PythonVisualizer.convert(firstFront));					
				PythonVisualizer.saveResults(numObj, visData, runName);
			}
			Collections.sort(igd);
			best.add(igd.get(0));
			if(NUM_RUNS % 2 == 0){
				median.add((igd.get(NUM_RUNS/2) + igd.get(NUM_RUNS/2 - 1))/2);
			}
			else{
				median.add(igd.get(NUM_RUNS/2));
			}
			worse.add(igd.get(igd.size() - 1));
			System.out.println(Arrays.toString(igd.toArray(new Double[0])));
		}
		
		for(int i=0; i<problems.size(); i++){
			System.out.println(getTestName(problems.get(i)) + " " + best.get(i) + " " + median.get(i) + " " + worse.get(i));
		}
	}

	private static void init() {
		//Initialize problems
		int dim[] = {3,5,8,10,15};
		for(int d : dim){
//			problems.add(new DTLZ1(d));
//			problems.add(new DTLZ2(d));
//			problems.add(new DTLZ3(d));
//			problems.add(new DTLZ4(d));
//			problems.add(new WFG1(d));
//			problems.add(new WFG2(d));
//			problems.add(new WFG3(d));
//			problems.add(new WFG4(d));
//			problems.add(new WFG5(d));
			problems.add(new WFG6(d));
			problems.add(new WFG7(d));
//			problems.add(new WFG8(d));
//			problems.add(new WFG9(d));
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
		
		ArrayList <String> wfgProblemNames = new ArrayList<>();
		for(int i=1; i<= 9; i++){
			wfgProblemNames.add("WFG" + i);
		}
		int numObj[] = {3,5,8,10,15};
		int numGen[] = {400,750,1500,2000,3000};
		
		for(String problemName : wfgProblemNames){
			for(int i = 0; i<5; i++){
				numGenMap.put(new Pair<String, Integer>(problemName, numObj[i]) , numGen[i]);
			}
		}
	}
	
	private static int getNumGen(Problem problem){
		return numGenMap.get(new Pair<String, Integer>(problem.getName(), problem.getNumObjectives()));
	}
	
	private static String getTestName(Problem problem){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + getNumGen(problem) + "gen";
	}
}