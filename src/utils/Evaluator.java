package utils;

import java.util.Arrays;

import algorithm.evolutionary.interactive.artificialDM.implementations.AsfDM;
import algorithm.evolutionary.interactive.preferenceModels.implementations.ASFBundle;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.VectorSolution;
import experiment.ExecutionHistory;
import problems.Problem;
import utils.math.Geometry;

public class Evaluator {
	public static void evaluateAsfDMRun(Problem problem, AsfDM asfRanker, Population res, ASFBundle asfBundle) {
		double targetPoint[] = problem.getTargetAsfPoint(Geometry.invert(asfRanker.getLambda()));
		System.out.println("TargetPoint: " + Arrays.toString(targetPoint));
		System.out.println("Final population range: ");
		for(int i=0; i< problem.getNumObjectives(); i++){
			double min = Double.MAX_VALUE, sum = 0, max = -Double.MAX_VALUE;
			for(VectorSolution s : res.getSolutions()){
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
		
		ExecutionHistory.getInstance().setAsfBundleConverged(asfBundle.converged());
	}
}