package experiment.metrics;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solution.DoubleSolution;
import utils.math.Geometry;

public class IGD {
	public static double execute(Population targetParetoFront, Population nonDominatedFront){
		double sum = 0;
		for(DoubleSolution s : targetParetoFront.getSolutions()){
			sum += nonDominatedFront.getSolutions().stream().mapToDouble(sol -> Geometry.euclideanDistance(s.getObjectives(), sol.getObjectives())).min().getAsDouble();
		}
		return sum/targetParetoFront.size();
	}	
}
