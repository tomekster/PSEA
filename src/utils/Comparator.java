package utils;

import core.Solution;
import exception.IncomparableSolutionsException;

public class Comparator {

	public static double EPS = 1E-10;

	/**
	 * Finds dominating solution assuming minimalization problem
	 * 
	 * @param solution1
	 * @param solution2
	 * @return 0 if neither of solutions dominates other ; -1 if solution1
	 *         dominates solution2 ; 1 if solution2 dominates solution1
	 */
	public int compareDominance(Solution solution1, Solution solution2) {

		if (solution1.getNumVariables() != solution2.getNumVariables()) {
			try {
				throw new IncomparableSolutionsException();
			} catch (IncomparableSolutionsException e) {
				e.printStackTrace();
			}
		}

		boolean firstDominates = false, secondDominates = false;
		int flag;
		for (int pos = 0; pos < solution1.getNumVariables(); pos++) {
			flag = Double.compare(solution1.getVariable(pos), solution2.getVariable(pos));
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

	public double min(double a, double b) {
		return Double.compare(a, b) < 0 ? a : b;
	}
}
