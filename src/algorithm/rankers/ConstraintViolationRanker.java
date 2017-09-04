package algorithm.rankers;

import java.util.Comparator;

import algorithm.psea.AsfPreferenceModel;

public class ConstraintViolationRanker implements Comparator <AsfPreferenceModel>{

	@Override
	public int compare(AsfPreferenceModel l1, AsfPreferenceModel l2) {
		return l1.getNumViolations() == l2.getNumViolations() ? 
				Double.compare(l1.getPenalty(), l2.getPenalty()) //Smaller penalty = better
				: 
				Integer.compare(l1.getNumViolations(), l2.getNumViolations()); //Smaller numViolations = better
	}
}
