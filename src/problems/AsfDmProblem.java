package problems;

import algorithm.evolutionary.interactive.artificialDM.implementations.AsfDM;
import algorithm.evolutionary.solutions.VectorSolution;

public interface AsfDmProblem {
	public VectorSolution <? extends Number> getOptimalAsfDmSolution(AsfDM dm);
}
