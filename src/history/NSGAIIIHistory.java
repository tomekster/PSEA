package history;

import java.util.ArrayList;

import core.Population;
import core.hyperplane.ReferencePoint;
import preferences.PreferenceCollector;

public class NSGAIIIHistory {
	
	private int numGenerations;
	private int populationSize;
	private int numSolutionDirections;
	private int numVariables;
	private int numObjectives;
	
	private Population targetPoints;
	private ArrayList<Population> generations;
	private ArrayList< ArrayList<ReferencePoint> > solutionDirectionsHistory;
	private ArrayList< ArrayList<ReferencePoint> > chebyshevDirectionsHistory;
	private ArrayList<Double> bestChebVal;
	private PreferenceCollector pc;
	
	public NSGAIIIHistory(int numGenerations){
		this.generations = new ArrayList<>(numGenerations);
		this.solutionDirectionsHistory = new ArrayList< ArrayList<ReferencePoint> >();
		this.chebyshevDirectionsHistory = new ArrayList< ArrayList<ReferencePoint> >();
		this.bestChebVal = new ArrayList <Double>();
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
	public Population getGeneration(int pos){
		return generations.get(pos);
	}
	public void addGeneration(Population pop){
		this.generations.add(pop);
	}
	public ArrayList< ArrayList<ReferencePoint> > getSolutionDirectionsHistory() {
		return solutionDirectionsHistory;
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
	public ArrayList<ReferencePoint> getChebyshevDirections(int id){
		return chebyshevDirectionsHistory.get(id);
	}
	public void addChebyshevDirections(ArrayList<ReferencePoint>  chebyshevDirections){
		this.chebyshevDirectionsHistory.add(chebyshevDirections);
	}
	public double getBestChebVal(int id){
		return bestChebVal.get(id);
	}
	public void addBestChebVal(double  bestChebVal){
		this.bestChebVal.add(bestChebVal);
	}
	
	public PreferenceCollector getPreferenceCollector() {
		return pc;
	}

	public void setPreferenceCollector(PreferenceCollector pc) {
		this.pc = pc;
	}

	public int getNumGenerations() {
		return numGenerations;
	}

	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getNumSolutionDirections() {
		return numSolutionDirections;
	}

	public void setNumSolutionDirections(int numSolutionDirections) {
		this.numSolutionDirections = numSolutionDirections;
	}

	public int getNumVariables() {
		return numVariables;
	}

	public void setNumVariables(int numVariables) {
		this.numVariables = numVariables;
	}

	public int getNumObjectives() {
		return numObjectives;
	}

	public void setNumObjectives(int numObjectives) {
		this.numObjectives = numObjectives;
	}
}
