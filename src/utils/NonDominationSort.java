package utils;

import java.util.ArrayList;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import utils.comparators.NondominationComparator;
import utils.enums.OptimizationType;

public abstract class NonDominationSort {

	/**
	 * This method divides a given population into disjoint sets F_1, F_2, ..., F_n, called non-dominated-fronts, or simply fronts. 
	 * The first front consists of solutions that are nondominated in the original population. 
	 * The second one consists of solutions that become nondominated after removing first front from original population. 
	 * The third one consists of nondominated solutions from set (P \ F1) \ F2, and so on. 
	 * @param population
	 * @return ArrayList of Populations representing non-dominated-fronts F_1, F_2, ..., F_n
	 */
	public static <S extends Solution> ArrayList<Population <S>> sortPopulation(Population <S> population, OptimizationType optimizationType) {
		ArrayList<Population <S>> fronts = new ArrayList<>();
		ArrayList<Integer> front = new ArrayList<Integer>();
		ArrayList<Integer> nextFront = new ArrayList<Integer>();
		Population <S> Q = new Population <>();
		NondominationComparator <Solution> NDC = new NondominationComparator<>(optimizationType);
		
		// dominatedBySolutions[i] = list of solutions that dominate solution i
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
				int flag = NDC.compare(p, q);
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

		while (!Q.isEmpty()) {
			fronts.add(Q);
			front = nextFront;
			nextFront = new ArrayList<Integer>();
			Q = new Population <S>();
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
}
