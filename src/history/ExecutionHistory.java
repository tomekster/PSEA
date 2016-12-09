package history;

import java.util.ArrayList;

import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import utils.Pair;

public class ExecutionHistory {
	private static ExecutionHistory instance = null;

	protected ExecutionHistory(){
		// Exists only to defeat instantiation.
		this.generations = new ArrayList<>();
		this.lambdaGenerations = new ArrayList< ArrayList<ReferencePoint> >();
		this.bestChebSol = new ArrayList <Solution>();
		this.bestChebVal = new ArrayList <Double>();
	}

	public static ExecutionHistory getInstance() {
		if (instance == null) {
			instance = new ExecutionHistory();
		}
		return instance;
	}
	
	private int populationSize;
	private int numVariables;
	private int numObjectives;
	
	private Population targetPoints;
	private ArrayList<Population> generations;
	private ArrayList< ArrayList<ReferencePoint> > lambdaGenerations;
	private ArrayList<Solution> bestChebSol;
	private ArrayList<Double> bestChebVal;	
	private PreferenceCollector pc;
	private ChebyshevRanker chebyshevRanker;
	private double finalMinDist;
	private double finalAvgDist;
	private int secondPhaseId;
	
	public Population getTargetPoints() {
		return targetPoints;
	}
	public void setTargetPoints(Population targetPoints) {
		this.targetPoints = targetPoints;
	}
	public ArrayList<Population> getGenerations() {
		return generations;
	}
	public void addGeneration(Population pop){
		this.generations.add(pop);
	}
	public Population getGeneration(int pos){
		return generations.get(pos);
	}

	public ArrayList< ArrayList<ReferencePoint> > getLambdaDirectionsHistory() {
		return lambdaGenerations;
	}
	public ArrayList<ReferencePoint> getLambdaDirections(int id){
		return lambdaGenerations.get(id);
	}
	public void addLambdas(ArrayList<ReferencePoint>  lambdaDirections){
		this.lambdaGenerations.add(lambdaDirections);
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
	
	public void setChebyshevRanker(ChebyshevRanker chebRank){
		this.chebyshevRanker = chebRank;
	}
	
	public ChebyshevRanker getChebyshevRanker(){
		return this.chebyshevRanker;
	}

	public void setFinalMinDist(double minDist) {
		this.finalMinDist = minDist;
	}

	public void setFinalAvgDist(double avgDist) {
		this.finalAvgDist = avgDist;
	}
	
	public double getFinalMinDist(){
		return this.finalMinDist;
	}
	
	public double getFinalAvgDist(){
		return this.finalAvgDist;
	}

	public void setSecondPhaseId(int id) {
		this.secondPhaseId = id;
	}
	
	public int getSecondPhaseId() {
		return this.secondPhaseId;
	}
}
