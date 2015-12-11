package igd;

import core.Population;
import core.Solution;
import utils.Geometry;

public class IGD {

	public static double execute(Population referenceParetoFront, Population nonDominatedFront){
		double res = 0;
		for(Solution s : referenceParetoFront.getSolutions()){
			res += minDist(s, nonDominatedFront);
		}
		return res/referenceParetoFront.size();
	}

	//Finds distance between reference Solution s rp and closes points from Population pop 
	private static double minDist(Solution solution, Population pop) {
		double minDist = Double.MAX_VALUE;
		for(Solution s : pop.getSolutions()){
			minDist = Double.min(minDist, Geometry.euclideanDistance(solution.getObjectives(), s.getObjectives()));
		}
		return minDist;
	}
	
}
