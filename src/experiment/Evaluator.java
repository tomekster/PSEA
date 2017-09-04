package experiment;

import java.util.Arrays;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.psea.preferences.ASFBundle;
import algorithm.rankers.AsfRanker;
import problems.Problem;
import utils.math.Geometry;

public class Evaluator {
	public static void evaluateRun(Problem prob, AsfRanker dmr, Population res) {
		double targetPoint[] = prob.getTargetPoint(dmr.getLambda());
		System.out.println("TargetPoint: " + Arrays.toString(targetPoint));
		System.out.println("Final population range: ");
		for(int i=0; i< prob.getNumObjectives(); i++){
			double min = Double.MAX_VALUE, sum = 0, max = -Double.MAX_VALUE;
			for(Solution s : res.getSolutions()){
				double o = s.getObjective(i);
				min = Double.min(min, o);
				max = Double.max(max, o);
				sum += o;
			}
			System.out.println("OBJ" + i + ": " + min + ", " + sum/res.getSolutions().size() + ", " + max);
		}
		
		if(targetPoint.length > 0){
			ExecutionHistory.getInstance().setFinalMinDist(Geometry.getMinDist(targetPoint, res));
			ExecutionHistory.getInstance().setFinalAvgDist(Geometry.getAvgDist(targetPoint, res));
		}
		
		ExecutionHistory.getInstance().setLambdasConverged(ASFBundle.getInstance().converged());
	}
}
