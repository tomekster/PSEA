package history;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import core.Lambda;
import core.NSGAIII;
import core.Population;
import core.Problem;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import utils.Pair;

public class ExecutionHistory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8450564579213477117L;
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
	private int numGenerations1;
	private int numGenerations2;
	private int numElicitations1;
	private int numElicitations2;
	private int elicitationInterval;
	
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
	private Problem problem;
	
	private boolean lambdasConverged;
	
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
		return generations.get(Integer.min(pos, generations.size()-1));
	}

	public ArrayList< ArrayList<ReferencePoint> > getLambdaDirectionsHistory() {
		return lambdaGenerations;
	}
	public ArrayList<ReferencePoint> getLambdaDirections(int id){
		return lambdaGenerations.get(Integer.min(id,lambdaGenerations.size()-1));
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

	public int getNumGenerations() {
		return this.generations.size();
	}

	public void clear() {
		generations = new ArrayList<>();
		lambdaGenerations = new ArrayList< ArrayList<ReferencePoint> >();
		bestChebSol = new ArrayList <Solution>();
		bestChebVal = new ArrayList <Double>();
	}

	public int getNumGenerations1() {
		return numGenerations1;
	}

	public void setNumGenerations1(int numGenerations1) {
		this.numGenerations1 = numGenerations1;
	}

	public int getNumGenerations2() {
		return numGenerations2;
	}

	public void setNumGenerations2(int numGenerations2) {
		this.numGenerations2 = numGenerations2;
	}

	public int getNumElicitations1() {
		return numElicitations1;
	}

	public void setNumElicitations1(int numElicitations1) {
		this.numElicitations1 = numElicitations1;
	}

	public int getNumElicitations2() {
		return numElicitations2;
	}

	public void setNumElicitations2(int numElicitations2) {
		this.numElicitations2 = numElicitations2;
	}

	public int getElicitationInterval() {
		return elicitationInterval;
	}

	public void setElicitationInterval(int elicitationInterval) {
		this.elicitationInterval = elicitationInterval;
	}

	public void init(Problem problem, NSGAIII nsgaiii, Lambda lambda, ChebyshevRanker decisionMakerRanker, int numGenerations1, int numGenerations2, int numElicitations1, int numElicitations2, int elicitationInterval) {
		clear();
		setProblem(problem);
		setNumVariables(problem.getNumVariables());
		setNumObjectives(problem.getNumObjectives());
		addGeneration(nsgaiii.getPopulation().copy());
		addLambdas(lambda.getLambdas());
		setTargetPoints(problem.getReferenceFront());
		setPreferenceCollector(PreferenceCollector.getInstance());
		setChebyshevRanker(decisionMakerRanker);
		setNumGenerations1(numGenerations1);
		setNumGenerations2(numGenerations2);
		setNumElicitations1(numElicitations1);
		setNumElicitations2(numElicitations2);
		setElicitationInterval(elicitationInterval);
		setLambdasConverged(false);
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public Problem getProblem(){
		return this.problem;
	}
	
	public void update(Population population, Lambda lambda) {
		addGeneration(population.copy());
		ArrayList <ReferencePoint> lambdasCopy = new ArrayList <> (lambda.getLambdas()); 
		addLambdas(lambdasCopy);
		addBestChebVal(getChebyshevRanker().getBestSolutionVal(population));
	}

	public boolean isLambdasConverged() {
		return lambdasConverged;
	}

	public void setLambdasConverged(boolean lambdasConverged) {
		this.lambdasConverged = lambdasConverged;
	}
	
	public static void serialize(String filename){
		try{
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(ExecutionHistory.getInstance());
			out.close();
			fileOut.close();
			System.out.println("Serialized data saved in " + filename);
		} catch(IOException i){
			i.printStackTrace();
		}
	}
	
	public static void deserialize(String filename){
		ExecutionHistory eh = null;
		try{
			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			eh = (ExecutionHistory) in.readObject();
			in.close();
			fileIn.close();
		} catch(IOException i){
			i.printStackTrace();
			return;
		} catch(ClassNotFoundException c){
			System.out.println("ExecutionHistory class not found");
			c.printStackTrace();
			return;
		}
		instance = eh;
	}
}
