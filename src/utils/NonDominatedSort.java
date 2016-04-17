package utils;

import java.util.ArrayList;

import core.Population;
import core.Solution;

public class NonDominatedSort {

	public static ArrayList<Population> execute(Population population) {
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
				MyComparator cp = new MyComparator();
				int flag = cp.compareDominance(p, q);
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

		return fronts;
	}

}
