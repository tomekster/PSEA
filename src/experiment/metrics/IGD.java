package experiment.metrics;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import utils.math.Geometry;

public class IGD {

	public static double execute(Population targetParetoFront, Population nonDominatedFront){
		double res = 0;
		for(Solution s : targetParetoFront.getSolutions()){
			res += minDist(s, nonDominatedFront);
		}
		return res/targetParetoFront.size();
	}

	//Finds distance between targetSolution and closes points from Population pop 
	private static double minDist(Solution targetSolution, Population pop) {
		double minDist = Double.MAX_VALUE;
		for(Solution curSolution: pop.getSolutions()){
			double curDist = Geometry.euclideanDistance(targetSolution.getObjectives(), curSolution.getObjectives());
			minDist = Double.min(curDist, minDist);
		}
		return minDist;
	}
	
}
