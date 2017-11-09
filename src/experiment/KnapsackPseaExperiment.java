package experiment;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.geneticAlgorithm.operators.impl.crossover.PermutationCrossover;
import algorithm.geneticAlgorithm.operators.impl.mutation.PermutationMutation;
import algorithm.psea.PSEA;
import artificialDM.ADMBuilder;
import artificialDM.AsfDM;
import problems.Problem;
import problems.knapsack.KnapsackProblemBuilder;

public class KnapsackPseaExperiment {
	private static ArrayList <Problem> problems = new ArrayList<>();
	
	public static void main(String [] args){
		
		init();
		final int NUM_RUNS = 5;
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		for(Problem problem : problems){
			//double idealPoint[] = problem.findIdealPoint(new PermutationCrossover(), new PermutationMutation());
			double idealPoint[] = new double[problem.getNumObjectives()];
			ArrayList <AsfDM> AllasfRankers = ADMBuilder.getAsfDms(problem.getNumObjectives(), idealPoint);
			ArrayList <AsfDM> asfRankers = new ArrayList<>();
			asfRankers.add(AllasfRankers.get(7));
			for(AsfDM asfRanker : asfRankers){
//			for(AsfDM asfRanker : AllasfRankers){
				Population refFront = problem.getReferenceFront();
				double[] targetPoint;
				
				double bestVal = Double.MAX_VALUE;
			    double bestObj[] = new double[problem.getNumObjectives()];
			    for(Solution s : refFront.getSolutions()){
			    	if(asfRanker.eval(s.getObjectives()) < bestVal){
			    		bestVal = asfRanker.eval(s.getObjectives());
			    		bestObj = s.getObjectives().clone();
			    	}
			    }
			    targetPoint = bestObj.clone();
				
				ArrayList <Double> asf = new ArrayList<>();
//				for(int k=0; k<NUM_RUNS; k++){
					String runName = "PSEA_" + getTestName(problem, asfRanker); 
					PSEA psea = new PSEA(problem, 
							asfRanker,
							new PermutationCrossover(),
							new PermutationMutation()
							);
					psea.run();
					
					ExecutionHistory hist = ExecutionHistory.getInstance();
					for(int i=0; i < hist.getNumGenerations(); i++){
						asf.add(-hist.getPopulation(i).getSolutions().stream().mapToDouble(s->asfRanker.eval(s)).min().getAsDouble() );
					}
					
					ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
					visData.add(PythonVisualizer.convert(asf.toArray(new Double[0]))); //Plot asf value in every generation
					ArrayList<Double> asfOptimalVal = new ArrayList<>();
					double optimalVal = asfRanker.eval(targetPoint);
					for(int i=0; i<asf.size(); i++){
						asfOptimalVal.add(-optimalVal);
					}
					visData.add(PythonVisualizer.convert(asfOptimalVal.toArray(new Double[0]))); //Plot target point asf value - optimal asf value
					PythonVisualizer.saveResults(1, visData, runName);
					
//					for(int genId = 0; genId < hist.getNumGenerations(); genId+=50){
//						visData.clear();
//						visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
//						visData.add(PythonVisualizer.convert(hist.getPopulation(genId)));
//						ArrayList<double[]> target= new ArrayList<>();
//						target.add(targetPoint);
//						visData.add(target);
//						PythonVisualizer.saveResults(problem.getNumObjectives(), visData, runName + "_" + genId + "gen");
//					}
					
					visData.clear();
					visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
					visData.add(PythonVisualizer.convert(psea.getPopulation()));
					ArrayList<double[]> target= new ArrayList<>();
					target.add(targetPoint);
					visData.add(target);
					PythonVisualizer.saveResults(problem.getNumObjectives(), visData, runName + "_final" );
//				}
				System.out.println(runName + ": " + asf.get(asf.size()-1) + "/" + -asfRanker.eval(targetPoint));
			}
		}
	}

	private static void init() {
		KnapsackProblemBuilder kpb = new KnapsackProblemBuilder();
//		problems.add(kpb.readFile(100, 2));
//		problems.add(kpb.readFile(250, 2));
		problems.add(kpb.readFile(100, 3));
	}
	
	private static String getTestName(Problem problem, AsfDM asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	

