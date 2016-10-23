package solutionRankers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import core.points.ReferencePoint;
import utils.NSGAIIIRandom;

public class LambdaCVRanker implements Comparator <ReferencePoint>{
	public static void sortLambdasByCV(ArrayList <ReferencePoint> lambdas) {
		
		Collections.sort(lambdas, new Comparator<ReferencePoint>() {
			@Override
			public int compare(ReferencePoint o1, ReferencePoint o2) {
				if (o1.getNumViolations() == o2.getNumViolations()) { // Constraint violation: smaller = better
					return Double.compare(o1.getPenalty(), o2.getPenalty()); // Penalty: smaller = better
				}
				return Integer.compare(o1.getNumViolations(), o2.getNumViolations());
			}
		});
	}

	@Override
	public int compare(ReferencePoint lambda1, ReferencePoint lambda2) {
		return lambda1.getNumViolations() == lambda2.getNumVariables() ? 
				//TODO
				//Double.compare(lambda2.getReward()/lambda2.getPenalty(), lambda1.getReward()/lambda1.getPenalty()) //Bigger reward = better
				(NSGAIIIRandom.getInstance().nextDouble() < 0.5 ? -1 : 1)
				: 
				Integer.compare(lambda1.getNumViolations(), lambda2.getNumViolations()); //Smaller num violations = better
					
	}
}
