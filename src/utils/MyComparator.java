package utils;

import java.util.Comparator;

import core.Solution;
import core.hyperplane.Association;
import core.hyperplane.ReferencePoint;

public class MyComparator {

	public static double EPS = 1E-9;

	/**
	 * Finds dominating solution assuming minimalization problem
	 * 
	 * @param solution1
	 * @param solution2
	 * @return 0 if neither of solutions dominates other ; -1 if solution1
	 *         dominates solution2 ; 1 if solution2 dominates solution1
	 */
	public int compareDominance(Solution solution1, Solution solution2) {

		if (solution1.getNumObjectives() != solution2.getNumObjectives()) {
			throw new RuntimeException("Incomparable solutions. Different number of dimensions");
		}

		boolean firstDominates = false, secondDominates = false;
		int flag;
		for (int pos = 0; pos < solution1.getNumObjectives(); pos++) {
			flag = Double.compare(solution1.getObjective(pos), solution2.getObjective(pos));
			if (flag == 1)
				secondDominates = true;
			if (flag == -1)
				firstDominates = true;
		}

		if (firstDominates && !secondDominates)
			return -1;
		else if (!firstDominates && secondDominates)
			return 1;
		else
			return 0;
	}
	
	
	public static Comparator <Association> associationComparator = new Comparator <Association>(){
		@Override
		public int compare(Association o1, Association o2) {
			return Double.compare(o1.getDist(), o2.getDist());
		}
	};
	
	public static Comparator <ReferencePoint> referencePointComparator = new Comparator <ReferencePoint>(){
		@Override
		public int compare(ReferencePoint o1, ReferencePoint o2) {
			return Double.compare(o1.getNicheCount(), o2.getNicheCount());
		}
	};
}
