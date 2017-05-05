package solutionRankers;

import java.util.Comparator;

import core.points.ReferencePoint;

public class LambdaCVRanker implements Comparator <ReferencePoint>{

	@Override
	public int compare(ReferencePoint lambda1, ReferencePoint lambda2) {
		return lambda1.getNumViolations() == lambda2.getNumViolations() ? 
				//TODO
				Double.compare(lambda1.getPenalty(), lambda2.getPenalty()) //Smaller penalty = better
//				Double.compare(MyMath.variance(lambda1.getDim()), MyMath.variance(lambda2.getDim())) //Smaller variance = better
				: 
				Integer.compare(lambda1.getNumViolations(), lambda2.getNumViolations()); //Smaller numViolations = better
	}
}
