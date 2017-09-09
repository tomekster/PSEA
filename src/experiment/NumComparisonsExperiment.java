package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import algorithm.psea.AsfPreferenceModel;
import algorithm.psea.PSEA;
import artificialDM.AsfDM;
import artificialDM.AsfDMBuilder;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ4;
import utils.math.Geometry;
import utils.math.structures.Pair;

public class NumComparisonsExperiment {
	private static ArrayList <Problem> problems = new ArrayList<>();
	private static ArrayList <Pair<Integer, Integer>> comparisons = new ArrayList<>();
	
	public static void main(String [] args){
		
		init();
		// TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		ArrayList <Pair<Double, String>> finalRes = new ArrayList<>();
		
		for(Problem problem : problems){
			double idealPoint[] = problem.findIdealPoint();
			System.out.println(Arrays.toString(idealPoint));
			ArrayList <AsfDM> allAsfRankers = AsfDMBuilder.getExperimentalRankers(problem.getNumObjectives(), idealPoint);
			ArrayList <AsfDM> asfRankers = new ArrayList<>();
			asfRankers.add(allAsfRankers.get(0));
			asfRankers.add(allAsfRankers.get(5));
			asfRankers.add(allAsfRankers.get(8));
			
			for(AsfDM asfRanker : asfRankers){
				for(Pair<Integer, Integer> comp : comparisons){
					ArrayList <Double> minModelDist = new ArrayList<>();
					ArrayList <Double> avgModelDist = new ArrayList<>();
					ArrayList <Double> minPopDist = new ArrayList<>();
					ArrayList <Double> avgPopDist = new ArrayList<>();
					ArrayList <Double> minPopAsf = new ArrayList<>();
					ArrayList <Double> optPopAsf = new ArrayList<>();
					String runName = getTestName(problem, asfRanker) + "_(" + comp.first + ", " + comp.second + ")"; 
					PSEA psea = new PSEA(problem, asfRanker, comp.first, comp.second);
					psea.run();
					
					ExecutionHistory hist = ExecutionHistory.getInstance();
					for(int i=0; i < hist.getASFbundles().size(); i++){
						minModelDist.add(hist.getAsfPreferenceModels(i).stream().mapToDouble(model->Geometry.euclideanDistance(model.getLambda(), asfRanker.getLambda())).min().getAsDouble() );
						avgModelDist.add(hist.getAsfPreferenceModels(i).stream().mapToDouble(model->Geometry.euclideanDistance(model.getLambda(), asfRanker.getLambda())).sum() / hist.getAsfPreferenceModels(i).size() );
					}				

					double targetSolution[] = problem.getTargetPoint(Geometry.invert(asfRanker.getLambda()));
					for(int i=0; i < hist.getPopulations().size(); i++){
						minPopDist.add(hist.getPopulation(i).getSolutions().stream().mapToDouble(solution->Geometry.euclideanDistance(solution.getObjectives(), targetSolution)).min().getAsDouble() );
						avgPopDist.add(hist.getPopulation(i).getSolutions().stream().mapToDouble(solution->Geometry.euclideanDistance(solution.getObjectives(), targetSolution)).sum() / hist.getPopulationSize());
					}
					
					for(int i=0; i < hist.getPopulations().size(); i++){
						minPopAsf.add(hist.getPopulation(i).getSolutions().stream().mapToDouble(solution->asfRanker.eval(solution.getObjectives())).min().getAsDouble() );
						optPopAsf.add(asfRanker.eval(targetSolution));
					}
					finalRes.add(new Pair<Double, String>(Collections.min(minModelDist), problem.getName()));
			
					ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
					visData.add(PythonVisualizer.convert(minModelDist.toArray(new Double[0]))); //Plot minAsf value in every generation
					visData.add(PythonVisualizer.convert(avgModelDist.toArray(new Double[0]))); //Plot avgAsf value in every generation
					PythonVisualizer.saveResults(1, visData, runName + "model_dist");
					
					visData.clear();
					visData.add(PythonVisualizer.convert(minPopDist.toArray(new Double[0]))); //Plot minAsf value in every generation
					visData.add(PythonVisualizer.convert(avgPopDist.toArray(new Double[0]))); //Plot avgAsf value in every generation
					PythonVisualizer.saveResults(1, visData, runName + "pop_dist");
					
					visData.clear();
					visData.add(PythonVisualizer.convert(minPopAsf.toArray(new Double[0]))); //Plot minAsf value in every generation
					visData.add(PythonVisualizer.convert(optPopAsf.toArray(new Double[0]))); //Plot avgAsf value in every generation
					PythonVisualizer.saveResults(1, visData, runName + "pop_asf");
					
					visData.clear();
					visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
					
					ArrayList <AsfPreferenceModel> models = hist.getASFbundles().get(hist.getASFbundles().size()-1);
					ArrayList <double[]> points = new ArrayList<>();
					for(AsfPreferenceModel model : models){
						points.add(model.getLambda());
					}
					visData.add(points);
					ArrayList<double[]> targetLambda= new ArrayList<>();
					targetLambda.add(asfRanker.getLambda());
					visData.add(targetLambda);
					PythonVisualizer.saveResults(problem.getNumObjectives(), visData, runName + "_model_vis");
				}
			}
		}
		
		
		Collections.sort(finalRes, new Comparator<Object>(){
			@Override
			public int compare(Object o1, Object o2) {
				Pair<Double, String> p1 = (Pair<Double, String>) o1;
				Pair<Double, String> p2 = (Pair<Double, String>) o2;
				return Double.compare(p1.first, p2.first);
			}
		});
		
		for(Pair<Double, String> p : finalRes){
			System.out.println(p.first + " " + p.second);
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
		
		int comp[] = {30,40};
		for(int a : comp){
			for(int b : comp){
				NumComparisonsExperiment.comparisons.add(new Pair<Integer, Integer>(a, b));
			}
		}
	}
	
	private static String getTestName(Problem problem, AsfDM asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
