package core;

import java.util.Arrays;

import core.points.Solution;
import history.ExecutionHistory;
import solutionRankers.ChebyshevRanker;
import utils.Geometry;
import utils.MyMath;

public class Evaluator {
	public static void evaluateRun(Problem prob, ChebyshevRanker dmr, Population res) {
		String pname = prob.getName();
		double targetPoint[] = {};

		switch(pname){
			case "DTLZ1":
				targetPoint = Geometry.lineCrossDTLZ1HyperplanePoint(dmr.getLambda());
				break;
			case "DTLZ2":
			case "DTLZ3":
			case "DTLZ4":
				targetPoint = Geometry.lineCrossDTLZ234HyperspherePoint(dmr.getLambda());
				break;
		}
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
			
			System.out.println("OBJ" + i + ": " + min + ", " + sum/res.getSolutions().size() + ", ");
		}
		
		if(targetPoint.length > 0){
			ExecutionHistory.getInstance().setFinalMinDist(MyMath.getMinDist(targetPoint, res));
			ExecutionHistory.getInstance().setFinalAvgDist(MyMath.getAvgDist(targetPoint, res));
		}
		
		ExecutionHistory.getInstance().setLambdasConverged(Lambda.getInstance().converged());
	}
}
