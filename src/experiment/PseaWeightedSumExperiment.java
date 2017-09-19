package experiment;

import java.util.ArrayList;
import java.util.Arrays;

import algorithm.psea.PSEA;
import artificialDM.ADMBuilder;
import artificialDM.ArtificialDM;
import artificialDM.WeightedSumDM;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;

public class PseaWeightedSumExperiment {
	private static ArrayList <Problem> problems = new ArrayList<>();
	
	public static void main(String [] args){
		
		init();
		final int NUM_RUNS = 5;
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		for(Problem problem : problems){
			double idealPoint[] = problem.findIdealPoint();
			System.out.println(Arrays.toString(idealPoint));
			ArrayList <WeightedSumDM> wsRankers = ADMBuilder.getWsDms(problem.getNumObjectives());
			for(WeightedSumDM wsDM : wsRankers){
				double[] targetPoint = problem.getTargetWSPoint(wsDM.getWeights());
				System.out.println("Weights: " + Arrays.toString(wsDM.getWeights()));
				System.out.println("TargetPoint: " + Arrays.toString(targetPoint));
				ArrayList <Double> ws = new ArrayList<>();
//				for(int k=0; k<NUM_RUNS; k++){
					String runName = getTestName(problem, wsDM); 
					PSEA psea = new PSEA(problem,  wsDM, 40, 20);
					psea.run();
					
					ExecutionHistory hist = ExecutionHistory.getInstance();
					for(int i=0; i < hist.getNumGenerations(); i++){
						ws.add(hist.getPopulation(i).getSolutions().stream().mapToDouble(s->wsDM.eval(s)).min().getAsDouble() );
						if(ws.get(ws.size()-1) < 0){
							System.out.println(wsDM.getWeights());
							System.out.println(ws.get(ws.size()-1));
						}
					}
					
					ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
					visData.add(PythonVisualizer.convert(ws.toArray(new Double[0]))); //Plot best asf value in every generation
					ArrayList<Double> wsOptimalVal = new ArrayList<>();
					double optimalVal = wsDM.eval(targetPoint);
					System.out.println("OptimalVal: " + optimalVal);
					for(int i=0; i<ws.size(); i++){
						wsOptimalVal.add(optimalVal);
					}
					visData.add(PythonVisualizer.convert(wsOptimalVal.toArray(new Double[0]))); //Plot target point asf value - optimal asf value
					PythonVisualizer.saveResults(1, visData, runName);
					
					visData.clear();
					visData.add(PythonVisualizer.convert(psea.getPopulation()));
					visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
					ArrayList<double[]> target= new ArrayList<>();
					target.add(targetPoint);
					visData.add(target);
					PythonVisualizer.saveResults(problem.getNumObjectives(), visData, runName + "_final" );
//				}
				System.out.println(runName + ": " + ws.get(ws.size()-1) + "/" + wsDM.eval(targetPoint));
			}
		}
	}

	private static void init() {
		//Initialize problems
		int dim[] = {3,5,8};
		for(int d : dim){
//			problems.add(new DTLZ1(d));
			problems.add(new DTLZ2(d));
//			problems.add(new DTLZ3(d));
//			problems.add(new DTLZ4(d));
		}
	}
	
	private static String getTestName(Problem problem, ArtificialDM asfRanker){
		return "PSEA_WS_" + problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
