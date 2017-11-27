package problems;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.solutions.Solution;

public interface AsfDmProblem {
	public Solution getOptimalAsfDmSolution(AsfDm dm);
}
