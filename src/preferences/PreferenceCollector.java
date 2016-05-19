package preferences;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import core.Solution;

public class PreferenceCollector {
	
	private ArrayList<Comparison> preferenceList = new ArrayList<>();

	public void addComparison(Solution s1, Solution s2) {
		preferenceList.add(new Comparison(s1,s2));
	}
	
	public ArrayList<Comparison> getComparisons(){
		return preferenceList;
	}
	
}
