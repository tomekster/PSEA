package history;

import java.util.ArrayList;

import core.Population;
import core.hyperplane.ReferencePoint;
import preferences.PreferenceCollector;

public class NSGAIIIHistory {
	
	private Population targetPoints;
	private ArrayList<Population> generations;
	private ArrayList< ArrayList<ReferencePoint> > solutionDirectionsHistory;
	private ArrayList< ArrayList<ReferencePoint> > chebyshevDirectionsHistory;
	private PreferenceCollector pc;
	private ArrayList<Population> chebRanking;
	
	public NSGAIIIHistory(int numGenerations){
		this.generations = new ArrayList<>(numGenerations);
		this.solutionDirectionsHistory = new ArrayList< ArrayList<ReferencePoint> >();
		this.chebyshevDirectionsHistory = new ArrayList< ArrayList<ReferencePoint> >();
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

	public ArrayList< ArrayList<ReferencePoint> > getSolutionDirectionsHistory() {
		return solutionDirectionsHistory;
	}
	public void setSolutionDirectionsHistory(ArrayList< ArrayList<ReferencePoint> > referencePoints) {
		this.solutionDirectionsHistory = referencePoints;
	}
	public ArrayList<ReferencePoint> getSolutionDirections(int id){
		return solutionDirectionsHistory.get(id);
	}
	public void addSolutionDirections(ArrayList<ReferencePoint>  solutionDirections){
		this.solutionDirectionsHistory.add(solutionDirections);
	}

	public ArrayList< ArrayList<ReferencePoint> > getChebyshevDirectionsHistory() {
		return chebyshevDirectionsHistory;
	}
	public void setChebyshevDirectionsHistory(ArrayList< ArrayList<ReferencePoint> > referencePoints) {
		this.chebyshevDirectionsHistory = referencePoints;
	}
	public ArrayList<ReferencePoint> getChebyshevDirections(int id){
		return chebyshevDirectionsHistory.get(id);
	}
	public void addChebyshevDirections(ArrayList<ReferencePoint>  chebyshevDirections){
		this.chebyshevDirectionsHistory.add(chebyshevDirections);
	}
	
	public PreferenceCollector getPreferenceCollector() {
		return pc;
	}

	public void setPreferenceCollector(PreferenceCollector pc) {
		this.pc = pc;
	}
}
