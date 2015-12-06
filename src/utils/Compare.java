package utils;

import core.Solution;
import exception.IncomparableSolutionsException;

public class Compare {

	public static double EPS = 1E-10;

	/**
	 * 
	 * @param a
	 * @param b
	 * @return 0 if abs(a-b) < 1E-20 ; -1 if a > b ; 1 if a < b
	 */
	public static int compareDouble(double a, double b) {
		if (Math.abs(a - b) < EPS)
			return 0;
		else if (a > b)
			return -1;
		else
			return 1;
	}

	/**
	 * 
	 * @param solution1
	 * @param solution2
	 * @return 0 if neither of solutions dominates other ; -1 if solution1
	 *         dominates solution2 ; 1 if solution2 dominates solution1
	 */
	public static int compareDominance(Solution solution1, Solution solution2) {
		
		if(solution1.getNumVariables() != solution2.getNumVariables()){
			try {
				throw new IncomparableSolutionsException();
			} catch (IncomparableSolutionsException e) {
				e.printStackTrace();
			}
		}
		
		boolean firstDominates = false, secondDominates = false;
		int flag;
		for (int pos = 0; pos < solution1.getNumVariables(); pos++) {
			flag = compareDouble(solution1.getVariable(pos), solution2.getVariable(pos));
			if (flag == -1)
				secondDominates = true;
			if (flag == 1)
				firstDominates = true;
		}

		if (firstDominates && !secondDominates)
			return -1;
		else if (!firstDominates && secondDominates)
			return 1;
		else
			return 0;
	}
}
