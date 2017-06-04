package core;

import java.util.Arrays;

import core.points.Solution;
import history.ExecutionHistory;
import solutionRankers.ChebyshevRanker;
import utils.MyMath;

public class Evaluator {
	public static void evaluateRun(Problem prob, ChebyshevRanker dmr, Population res) {
		double targetPoint[] = prob.getTargetPoint(dmr.getDirection());
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
			ExecutionHistory.getInstance().setFinalMinDist(MyMath.getMinDist(targetPoint, res));
			ExecutionHistory.getInstance().setFinalAvgDist(MyMath.getAvgDist(targetPoint, res));
		}
		
		ExecutionHistory.getInstance().setLambdasConverged(Lambda.getInstance().converged());
	}
}
