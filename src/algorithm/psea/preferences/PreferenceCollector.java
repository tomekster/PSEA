package algorithm.psea.preferences;

import java.io.Serializable;
import java.util.ArrayList;

import algorithm.geneticAlgorithm.Solution;

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
}
