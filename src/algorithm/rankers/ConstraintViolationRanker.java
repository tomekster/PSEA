package algorithm.rankers;

import java.util.Comparator;

import algorithm.evolutionary.interactive.artificialDM.implementations.AsfDM;

public class ConstraintViolationRanker implements Comparator <AsfDM>{

	@Override
	public int compare(AsfDM dm1, AsfDM dm2) {
		return dm1.getNumViolations() == dm2.getNumViolations() ? 
				Double.compare(dm1.getPenalty(), dm2.getPenalty()) //Smaller penalty = better
				: 
				Integer.compare(dm1.getNumViolations(), dm2.getNumViolations()); //Smaller numViolations = better
	}
}
