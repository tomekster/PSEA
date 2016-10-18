package history;

import java.util.ArrayList;

import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.PreferenceCollector;
import utils.Pair;

public class ExecutionHistory {
	
	private int populationSize;
	private int numVariables;
	private int numObjectives;
	
	private Population targetPoints;
	private ArrayList<Population> preferenceGenerations;
	private ArrayList<Population> spreadGenerations;
	private ArrayList< ArrayList<ReferencePoint> > solutionDirectionsGenerations;
	private ArrayList< ArrayList<ReferencePoint> > lambdaGenerations;
	private ArrayList<Solution> bestChebSol;
	private ArrayList<Double> bestChebVal;	
	private PreferenceCollector pc;
	
	public ExecutionHistory(){
		this.preferenceGenerations = new ArrayList<>();
		this.spreadGenerations = new ArrayList<>();
		this.solutionDirectionsGenerations = new ArrayList< ArrayList<ReferencePoint> >();
		this.lambdaGenerations = new ArrayList< ArrayList<ReferencePoint> >();
		this.bestChebSol = new ArrayList <Solution>();
		this.bestChebVal = new ArrayList <Double>();
	}
	
	public Population getTargetPoints() {
		return targetPoints;
	}
	public void setTargetPoints(Population targetPoints) {
		this.targetPoints = targetPoints;
	}
	public ArrayList<Population> getPreferenceGenerations() {
		return preferenceGenerations;
	}
	public Population getPreferenceGeneration(int pos){
		return preferenceGenerations.get(pos);
	}
	public void addPreferenceGeneration(Population pop){
		this.preferenceGenerations.add(pop);
	}
	public ArrayList<Population> getSpreadGenerations() {
		return spreadGenerations;
	}
	public Population getSpreadGeneration(int pos){
		return spreadGenerations.get(pos);
	}
	public void addSpreadGeneration(Population pop){
		this.spreadGenerations.add(pop);
	}
	public ArrayList< ArrayList<ReferencePoint> > getSolutionDirectionsHistory() {
		return solutionDirectionsGenerations;
	}
	public ArrayList<ReferencePoint> getSolutionDirections(int id){
		return solutionDirectionsGenerations.get(id);
	}
	public void addSolutionDirections(ArrayList<ReferencePoint>  solutionDirections){
		this.solutionDirectionsGenerations.add(solutionDirections);
	}

	public ArrayList< ArrayList<ReferencePoint> > getChebyshevDirectionsHistory() {
		return lambdaGenerations;
	}
	public ArrayList<ReferencePoint> getChebyshevDirections(int id){
		return lambdaGenerations.get(id);
	}
	public void addLambdas(ArrayList<ReferencePoint>  chebyshevDirections){
		this.lambdaGenerations.add(chebyshevDirections);
	}
	public double getBestChebVal(int id){
		return bestChebVal.get(id);
	}
	public void addBestChebVal(Pair<Solution, Double> bestChebVal){
		this.bestChebSol.add(bestChebVal.first);
		this.bestChebVal.add(bestChebVal.second);
	}
	
	public PreferenceCollector getPreferenceCollector() {
		return pc;
	}

	public void setPreferenceCollector(PreferenceCollector pc) {
		this.pc = pc;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
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

	public Solution getBestChebSol(int pos) {
		return this.bestChebSol.get(pos);
	}
	public void addBestChebSol(Solution s) {
		this.bestChebSol.add(s);
	}
}
