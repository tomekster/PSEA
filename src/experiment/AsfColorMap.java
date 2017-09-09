package experiment;

import java.util.ArrayList;

import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import artificialDM.AsfDM;
import artificialDM.AsfDMBuilder;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ2;
import problems.dtlz.DTLZ3;
import problems.dtlz.DTLZ4;

public class AsfColorMap {
	private static ArrayList <Problem> problems = new ArrayList<>();
	
	public static void main(String [] args){
		init();
		for(Problem problem : problems){
			double idealPoint[] = problem.findIdealPoint();
			ArrayList <AsfDM> asfRankers = AsfDMBuilder.getExperimentalRankers(problem.getNumObjectives(), idealPoint);
			for(AsfDM asfRanker : asfRankers){
				Hyperplane h = new Hyperplane(3);
				ArrayList <Integer> partitions = new ArrayList<>();
				partitions.add(50);
				h.generateReferencePoints(partitions);

				String runName = "AsfColorMap_" + getTestName(problem, asfRanker); 	
				ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
				ArrayList <double[]> points = new ArrayList<>();
				for(ReferencePoint rp : h.getReferencePoints()){
					double target[] = problem.getTargetPoint(rp.getDim());
					double point[] = new double[rp.getNumDimensions() + 1];
					for(int i=0; i<rp.getNumDimensions(); i++){
						point[i] = target[i];
						point[rp.getNumDimensions()] = asfRanker.eval(rp.getDim());
					}
					points.add(point);
				}
				visData.add(points);
				PythonVisualizer.colorMap(3, visData, runName);
			}
		}
	}

	private static void init() {
		//Initialize problems
		int dim[] = {3};
		for(int d : dim){
			problems.add(new DTLZ1(d));
			problems.add(new DTLZ2(d));
//			problems.add(new DTLZ3(d));
//			problems.add(new DTLZ4(d));
		}
	}
	
	private static String getTestName(Problem problem, AsfDM asfRanker){
		return problem.getName() + "_" + problem.getNumObjectives() + "obj_" + asfRanker.getName();
	}
	
}
	
	
