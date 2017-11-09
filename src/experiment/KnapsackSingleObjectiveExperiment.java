package experiment;

import java.util.ArrayList;
import java.util.Arrays;

import algorithm.geneticAlgorithm.SingleObjectiveEA;
import artificialDM.AsfDM;
import artificialDM.ADMBuilder;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;
import utils.math.Geometry;

public class KnapsackSingleObjectiveExperiment {
	private static final double DEFAULT_NUM_GENERATIONS = 1500;
	private static ArrayList <Problem> problems = new ArrayList<>();
		
	public static void main(String [] args){
		
		init();
		for(Problem problem : problems){
			double idealPoint[] = problem.findIdealPoint();
			System.out.println(Arrays.toString(idealPoint));
			ArrayList <AsfDM> asfRankers = ADMBuilder.getAsfDms(problem.getNumObjectives(), idealPoint);
			for(AsfDM asfRanker : asfRankers){
				double[] targetPoint = problem.getTargetAsfPoint(Geometry.invert(asfRanker.getLambda()));
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
		int dim[] = {3,5,8};
		for(int d : dim){
			problems.add(new DTLZ1(d));
			problems.add(new DTLZ2(d));
			problems.add(new DTLZ3(d));
			problems.add(new DTLZ4(d));
		}
 

	}

	private static String getTestName(Problem problem, AsfDM asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
