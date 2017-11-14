package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.geneticAlgorithm.operators.impl.crossover.PermutationCrossover;
import algorithm.geneticAlgorithm.operators.impl.mutation.PermutationMutation;
import algorithm.geneticAlgorithm.solutions.VectorSolution;
import algorithm.nsgaiii.NSGAIII;
import artificialDM.AsfDM;
import artificialDM.ADMBuilder;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import problems.knapsack.KnapsackProblemBuilder;
import utils.math.Geometry;
import utils.math.structures.Pair;

public class NSGAIIIAsfExperiment {
	private static final double DEFAULT_NUM_GENERATIONS = 1500;
	private static ArrayList <Problem> problems = new ArrayList<>();
		
	public static void main(String [] args){
		
		init();
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		for(Problem problem : problems){
			//double idealPoint[] = problem.findIdealPoint();
			double idealPoint[] = new double[problem.getNumObjectives()];
			System.out.println(Arrays.toString(idealPoint));
			ArrayList <AsfDM> asfRankers = ADMBuilder.getAsfDms(problem.getNumObjectives(), idealPoint);
			for(AsfDM asfRanker : asfRankers.subList(7, 8)){
				Population refFront = problem.getReferenceFront();
				double[] targetPoint;
				double bestVal = Double.MAX_VALUE;
			    double bestObj[] = new double[problem.getNumObjectives()];
			    for(VectorSolution s : refFront.getSolutions()){
			    	if(asfRanker.eval(s.getObjectives()) < bestVal){
			    		bestVal = asfRanker.eval(s.getObjectives());
			    		bestObj = s.getObjectives().clone();
			    	}
			    }
			    targetPoint = bestObj.clone();
				ArrayList <Double> asf = new ArrayList<>();
//				for(int k=0; k<NUM_RUNS; k++){
					String runName = "NSGAIII_" + getTestName(problem, asfRanker); 
					NSGAIII nsgaiii = new NSGAIII(problem);
					
					for(int i=0; i < DEFAULT_NUM_GENERATIONS; i++){
						nsgaiii.nextGeneration();
						asf.add(nsgaiii.getPopulation().getSolutions().stream().mapToDouble(s->asfRanker.eval(s)).min().getAsDouble() );
					}
					
					ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
					visData.add(PythonVisualizer.convert(asf.toArray(new Double[0]))); //Plot asf value in every generation
					PythonVisualizer.saveResults(1, visData, runName);
//				}
				System.out.println(runName + ": " + asf.get(asf.size()-1) + "/" + asfRanker.eval(targetPoint));
			}
		}
	}

	private static void init() {
		problems.add(new DTLZ1(3));
		problems.add(new DTLZ2(5));
		problems.add(new DTLZ4(8));
	}

	private static String getTestName(Problem problem, AsfDM asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
