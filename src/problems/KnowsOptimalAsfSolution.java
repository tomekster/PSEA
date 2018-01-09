package problems;

import algorithm.evolutionary.interactive.artificialDM.ReferencePointDm;
import algorithm.evolutionary.solutions.Solution;
import utils.math.structures.Point;

public interface KnowsOptimalAsfSolution {
	public Solution getOptimalSolution(ReferencePointDm dm);
	public Point getTrueIdealPoint();
}
