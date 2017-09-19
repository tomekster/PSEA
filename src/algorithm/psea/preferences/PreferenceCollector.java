package algorithm.psea.preferences;

import java.io.Serializable;
import java.util.ArrayList;

import algorithm.geneticAlgorithm.solution.DoubleSolution;
import algorithm.geneticAlgorithm.solution.Solution;
import artificialDM.AsfDM;

public class PreferenceCollector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6118069891989560804L;
	private static PreferenceCollector instance = null;

	protected PreferenceCollector() {
		// Exists only to defeat instantiation.
	}

	public static PreferenceCollector getInstance() {
		if (instance == null) {
			instance = new PreferenceCollector();
		}
		return instance;
	}

	private ArrayList<Comparison> comparisonsList = new ArrayList<>();

	public void addComparison(Solution s1, Solution s2) {
		//comparisonsList.add(new Comparison(s1,s2, RACS.findMinEps(s1, s2), RACS.findMaxEps(s1, s2)));
		comparisonsList.add(new Comparison(s1,s2, 0,0));
	}

	public ArrayList<Comparison> getComparisons() {
		return comparisonsList;
	}

	@Override
	public String toString() {
		String res = "";
		for (Comparison c : this.comparisonsList) {
			res += c + "\n";
		}
		return res;
	}
	
	public void clear(){
		this.comparisonsList.clear();
	}
	
	/**
	 * Checks if chebyshev's function with given lambdaPoint can reproduce all comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public int evaluateDM(AsfDM dm) {
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PreferenceCollector.getInstance().getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = dm.eval(better);
			double b = dm.eval(worse);

			double eps = b-a;
			if(a >= b){
				numViolations++;
				double newPenalty = penalty * (1-eps);
				assert newPenalty >= penalty;
				penalty = newPenalty;
			} else if(a < b){
				double newReward = reward * (1+eps);
				assert newReward >= reward;
				reward = newReward;
			}
		}
		
		dm.setReward(reward);
		dm.setPenalty(penalty);
		dm.setNumViolations(numViolations);
		return numViolations;
	}
}
