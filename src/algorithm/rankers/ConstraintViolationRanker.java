package algorithm.rankers;

import java.util.Comparator;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;

public class ConstraintViolationRanker implements Comparator <AsfDm>{

	@Override
	public int compare(AsfDm dm1, AsfDm dm2) {
		return dm1.getNumViolations() == dm2.getNumViolations() ? 
				Double.compare(dm1.getPenalty(), dm2.getPenalty()) //Smaller penalty = better
				: 
				Integer.compare(dm1.getNumViolations(), dm2.getNumViolations()); //Smaller numViolations = better
	}
}
