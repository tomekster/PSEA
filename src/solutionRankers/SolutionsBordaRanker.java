package solutionRankers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import core.Lambda;
import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
import utils.Geometry;
import utils.Pair;

public class SolutionsBordaRanker implements Comparator<Solution>{
	
	public Population sortSolutions(Population pop) {
		HashMap<Solution, Integer> bordaPointsMap = getBordaPointsForSolutions(pop);
		
		ArrayList<Pair<Solution, Integer>> pairs = new ArrayList<Pair<Solution, Integer>>();

		for (Solution s : bordaPointsMap.keySet()) {
			pairs.add(new Pair<Solution, Integer>(s, bordaPointsMap.get(s)));
		}

		Collections.sort(pairs, new Comparator<Pair<Solution, Integer>>() {
			@Override
			public int compare(final Pair<Solution, Integer> o1, final Pair<Solution, Integer> o2) {
				return Integer.compare(o2.second, o1.second); // Sort DESC by Borda points
			}
		});

		Population res = new Population();
		for (int i = 0; i < pairs.size(); i++) {
			res.addSolution(pairs.get(i).first.copy());
		}
		return res;
	}

	private HashMap<Solution, Integer> getBordaPointsForSolutions(Population pop) {
		HashMap<Solution, Integer> bordaPointsMap = new HashMap<>();
		for (ReferencePoint lambda : Lambda.getInstance().getLambdas()) {
			ArrayList<Solution> ranking = buildSolutionsRanking(lambda, pop);
			assert ranking.size() == pop.size();
			for (int i = 0; i < ranking.size(); i++) {
				Solution s = ranking.get(i);
				if (!bordaPointsMap.containsKey(s)) {
					bordaPointsMap.put(s, 0);
				}
				bordaPointsMap.put(s, bordaPointsMap.get(s) + (ranking.size() - i)/(lambda.getNumViolations() + 1));
			}
		}
		return bordaPointsMap;
	}

	public static ArrayList<Solution> buildSolutionsRanking(ReferencePoint lambda, Population pop) {
		ArrayList<Pair<Solution, Double>> solutionValuePairs = new ArrayList<Pair<Solution, Double>>();
		for (Solution s : pop.getSolutions()) {
			double chebyshevValue = ChebyshevRanker.eval(s, null, Geometry.invert(lambda.getDim()), 0);
			solutionValuePairs.add(new Pair<Solution, Double>(s, chebyshevValue));
		}
		Collections.sort(solutionValuePairs, new Comparator<Pair<Solution, Double>>() {
			@Override
			public int compare(final Pair<Solution, Double> o1, final Pair<Solution, Double> o2) {
				// Sort pairs by Chebyshev Function value ascending (Decreasing quality)
				return Double.compare(o1.second, o2.second);
			}
		});

		ArrayList<Solution> ranking = new ArrayList<Solution>();
		for (Pair<Solution, Double> p : solutionValuePairs) {
			ranking.add(p.first);
		}
		assert ranking.size() == pop.size();
		return ranking;
	}

	@Override
	public int compare(Solution s1, Solution s2) {
		int v1=0, v2=0;
		for(ReferencePoint lambda : Lambda.getInstance().getLambdas()){
			int cmp = ChebyshevRanker.compareSolutions(s1, s2, null, lambda.getDim(), .0);
			if(cmp < 0) v1++;
			else if(cmp >0) v2++;
		}
		return Integer.compare(v1, v2);
	}
}
