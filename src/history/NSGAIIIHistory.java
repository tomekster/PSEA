package history;

import java.util.ArrayList;

import core.Population;
import core.hyperplane.ReferencePoint;
import preferences.PreferenceCollector;

public class NSGAIIIHistory {
	
	private Population targetPoints;
	private ArrayList<Population> generations;
	private ArrayList< ArrayList<ReferencePoint> > referencePointsHistory;
	private PreferenceCollector pc;
	
	public NSGAIIIHistory(int numGenerations){
		this.generations = new ArrayList<>(numGenerations);
		this.referencePointsHistory = new ArrayList< ArrayList<ReferencePoint> >();
	}
	
	public Population getTargetPoints() {
		return targetPoints;
	}
	public void setTargetPoints(Population targetPoints) {
		this.targetPoints = targetPoints;
	}
	public ArrayList<Population> getGenerations() {
		return generations;
	}
	public void setGenerations(ArrayList<Population> generations) {
		this.generations = generations;
	}
	public Population getGeneration(int pos){
		return generations.get(pos);
	}
	public void addGeneration(Population pop){
		this.generations.add(pop);
	}

	public ArrayList< ArrayList<ReferencePoint> > getReferencePointsHistory() {
		return referencePointsHistory;
	}

	public void setReferencePointsHistory(ArrayList< ArrayList<ReferencePoint> > referencePoints) {
		this.referencePointsHistory = referencePoints;
	}
	public ArrayList<ReferencePoint> getReferencePoints(int id){
		return referencePointsHistory.get(id);
	}
	public void addReferencePoints(ArrayList<ReferencePoint>  referencePoints){
		this.referencePointsHistory.add(referencePoints);
	}

	public PreferenceCollector getPreferenceCollector() {
		return pc;
	}

	public void setPreferenceCollector(PreferenceCollector pc) {
		this.pc = pc;
	}
	
}
