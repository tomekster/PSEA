package solutionRankers;

import java.util.Comparator;

import core.points.Lambda;

public class LambdaCVRanker implements Comparator <Lambda>{

	@Override
	public int compare(Lambda l1, Lambda l2) {
		return l1.getNumViolations() == l2.getNumViolations() ? 
				//TODO
				Double.compare(l1.getPenalty(), l2.getPenalty()) //Smaller penalty = better
//				Double.compare(MyMath.variance(lambda1.getDim()), MyMath.variance(lambda2.getDim())) //Smaller variance = better
				: 
				Integer.compare(l1.getNumViolations(), l2.getNumViolations()); //Smaller numViolations = better
	}
}
