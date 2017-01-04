package solutionRankers;

import java.util.ArrayList;
import java.util.Comparator;

import core.Population;
import core.points.Solution;

public class NonDominationRanker implements Comparator <Solution>{

	public ArrayList<Population> sortPopulation(Population population) {
		ArrayList<Population> fronts = new ArrayList<Population>();
		ArrayList<Integer> front = new ArrayList<Integer>();
		ArrayList<Integer> nextFront = new ArrayList<Integer>();
		Population Q = new Population();

		// dominated[i] = list of solutions that dominate solution i
		ArrayList<ArrayList<Integer>> dominatedBySolutions = new ArrayList<ArrayList<Integer>>();
		// dominationCount[i] = number of solutions dominating solution i
		int dominationCount[] = new int[population.size()];

		for (int i = 0; i < population.size(); i++) {
			dominatedBySolutions.add(new ArrayList<Integer>());
			dominationCount[i] = 0;
		}

		for (int i = 0; i < population.size(); i++) {
			Solution p = population.getSolution(i);
			for (int j = 0; j < population.size(); j++) {
				if(i==j){
					continue;
				}
				Solution q = population.getSolution(j);
				int flag = compare(p, q);
				if (flag == -1) {
					dominatedBySolutions.get(i).add(j);
				} else if (flag == 1) {
					dominationCount[i]++;
				}

			}
			if (dominationCount[i] == 0) {
				nextFront.add(i);
				Q.addSolution(population.getSolution(i));
			}
		}

		while (!Q.empty()) {
			fronts.add(Q);
			front = nextFront;
			nextFront = new ArrayList<Integer>();
			Q = new Population();
			for (int i : front) {
				for (int q : dominatedBySolutions.get(i)) {
					dominationCount[q]--;
					if (dominationCount[q] == 0) {
						nextFront.add(q);
						Q.addSolution(population.getSolution(q));
					}
				}
			}
		}
		
		for(Solution s : fronts.get(0).getSolutions()){
			s.setDominated(false);
		}
		
		for(int i=1; i<fronts.size(); i++){
			for(Solution s : fronts.get(i).getSolutions()){
				s.setDominated(true);
			}
		}

		return fronts;
	}
	
	/**
	 * Finds dominating solution assuming minimization problem
	 * 
	 * @param solution1
	 * @param solution2
	 * @return 0 if neither of solutions dominates other ; -1 if solution1
	 *         dominates solution2 ; 1 if solution2 dominates solution1
	 */
	@Override
	public int compare(Solution solution1, Solution solution2) {

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
}
