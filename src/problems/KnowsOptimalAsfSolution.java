package problems;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.solutions.Solution;
import utils.math.structures.Point;

public interface KnowsOptimalAsfSolution {
	public Solution getOptimalAsfDmSolution(AsfDm dm);
	public Point getTrueIdealPoint();
}
