package utils;

import java.util.ArrayList;

import core.Front;
import core.Population;
import core.Solution;

public class NonDominatedSort {

	public static ArrayList<Front> execute(Population population) {
		ArrayList<Front> fronts = new ArrayList<Front>();
		ArrayList<ArrayList<Integer>> dominated = new ArrayList<ArrayList<Integer>>();
		;
		int dominationCount[] = new int[population.size()];;
		int rank[] = new int[population.size()];
		
		for (int i = 0; i < population.size(); i++) {
			dominated.add(new ArrayList<Integer>());
			dominationCount[i] = 0;
		}

		for (int i = 0; i < population.size(); i++) {
			Solution p = population.getSolution(i);
			for (int j = 0; j < population.size(); j++) {
				Solution q = population.getSolution(j);
				Comparator cp  = new Comparator();
				int flag = cp.compareDominance(p, q);
				if (flag == -1) {
					dominated.get(i).add(j);
				} else if (flag == 1) {
					dominationCount[i]++;
				}

				if (dominationCount[i] == 0) {
					rank[i] = 0;
					fronts.get(0).addSolution(p);
				}
			}
		}
		
		int frontId = 0;
		while(fronts.get(frontId).size() != 0){
			Front Q = new Front();
			for (int i = 0; i < population.size(); i++) {
				for (int q : dominated.get(i)) {
					dominationCount[q]--;
					if(dominationCount[q] == 0){
						rank[q] = i+1;
						Q.addSolution(population.getSolution(q));
					}
				}
			}
			frontId++;
			fronts.set(frontId, Q.copy());
		}

	return fronts;
}

}
