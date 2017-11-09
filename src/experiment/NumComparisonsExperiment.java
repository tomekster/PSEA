package experiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.psea.PSEA;
import artificialDM.ADMBuilder;
import artificialDM.ArtificialDM;
import problems.ContinousProblem;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ4;
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
			ArrayList <ArtificialDM> allRankers = new ArrayList<>();
			for(ArtificialDM adm : ADMBuilder.getAsfDms(problem.getNumObjectives(), idealPoint)){
				allRankers.add(adm);
			}
			ArrayList <ArtificialDM> testedRankers = new ArrayList<>();
			
			int numRuns = 5;
			int maxGen = 1500;
			testedRankers.add(allRankers.get(7));
			for(ArtificialDM adm : testedRankers){
				ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
				ArrayList <Double> optPopAsf = new ArrayList<>();
				optPopAsf.add(adm.eval(problem.getTargetPoint(adm)));
				fill(optPopAsf, maxGen);
				for(double o : optPopAsf){
					System.out.println(o + " " + 6);
				}
				for(Pair<Integer, Integer> comp : comparisons){
					System.out.println("(" + comp.first + "," + comp.second +")");
					ArrayList<ArrayList <Double>> median = new ArrayList<>();
					ArrayList <Double> minPopAsf = new ArrayList<>();
					ArrayList <Double> avgPopAsf = new ArrayList<>();
					for(int runId = 1; runId <= numRuns; runId++){
						System.out.println("(" + comp.first + "," + comp.second + ")_"+ runId);
						minPopAsf = new ArrayList<>();
//						avgPopAsf = new ArrayList<>();
						optPopAsf = new ArrayList<>();
//						String runName = getTestName(problem, adm) + "_(" + comp.first + ", " + comp.second + ")"; 
						PSEA psea = new PSEA(problem, 
								adm,
								comp.first, 
								comp.second,
								new SBX(1.0, 30.0, ((ContinousProblem)problem).getLowerBounds(), ((ContinousProblem)problem).getUpperBounds()), 
								new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, ((ContinousProblem)problem).getLowerBounds(), ((ContinousProblem)problem).getUpperBounds()) 
								);
						psea.run();
						ExecutionHistory hist = ExecutionHistory.getInstance();
						
						for(int j=0; j < hist.getPopulations().size(); j++){
							minPopAsf.add(hist.getPopulation(j).getSolutions().stream().mapToDouble(solution->adm.eval(solution.getObjectives())).min().getAsDouble() );
							
						}
						fill(minPopAsf, maxGen);
						median.add(minPopAsf);
					}
					ArrayList <Double> medianMinPopAsf = getArrayOfMedians(median);
					visData.add(PythonVisualizer.convert(medianMinPopAsf.toArray(new Double[0]))); //Plot minAsf value in every generation
				}
				visData.add(PythonVisualizer.convert(optPopAsf.toArray(new Double[0]))); //Plot optimal asf value in every generation
//				"(" + comp.first + "_" + comp.second + ")
				PythonVisualizer.saveResults(1, visData,  "CMP_" + getTestName(problem, adm) + "min_pop_asf");
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

	private static ArrayList<Double> getArrayOfMedians(ArrayList<ArrayList<Double>> median) {
		ArrayList <Double> res = new ArrayList<>();
		for(int i = 0; i < median.get(0).size(); i++){
			ArrayList <Double> medianSet = new ArrayList<>();
			for(int j=0; j < median.size(); j++){
				medianSet.add(median.get(j).get(i));
			}
			res.add(getMedian(medianSet));
		}
		return res;
	}

	private static Double getMedian(ArrayList<Double> medianSet) {
		int m = medianSet.size()/2;
		if(medianSet.size() %2 == 0){
			return medianSet.get(m);
		}
		else{
			return (medianSet.get(m) + medianSet.get(m+1))/2;
		}
	}

	private static void fill(ArrayList<Double> array, int maxGen) {
		while(array.size() <= maxGen){
			array.add(array.get(array.size() - 1));
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
		problems.add(new DTLZ2(5));
		problems.add(new DTLZ4(8));
		
//		int comp[] = {10,20,30};
//		for(int a : comp){
//			for(int b : comp){
//				if(b > a) continue;
//				NumComparisonsExperiment.comparisons.add(new Pair<Integer, Integer>(a, b));
//			}
//		}
		NumComparisonsExperiment.comparisons.add(new Pair<Integer, Integer>(20, 30));
		NumComparisonsExperiment.comparisons.add(new Pair<Integer, Integer>(20, 40));
		NumComparisonsExperiment.comparisons.add(new Pair<Integer, Integer>(40, 20));
	}
	
	private static String getTestName(Problem problem, ArtificialDM asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
