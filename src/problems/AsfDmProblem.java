package problems;

import algorithm.geneticAlgorithm.solutions.VectorSolution;
import artificialDM.AsfDM;

public interface AsfDmProblem {
	public VectorSolution <? extends Number> getOptimalAsfDmSolution(AsfDM dm);
}
