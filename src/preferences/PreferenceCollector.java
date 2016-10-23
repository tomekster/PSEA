package preferences;

import java.util.ArrayList;

import core.points.Solution;

public class PreferenceCollector {
	
	private ArrayList<Comparison> comparisonsList = new ArrayList<>();
	
	public void addComparison(Solution s1, Solution s2) {
		comparisonsList.add(new Comparison(s1,s2));
	}
	
	public ArrayList<Comparison> getComparisons(){
		return comparisonsList;
	}
}
