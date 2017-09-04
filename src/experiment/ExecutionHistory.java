package experiment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.NSGAIII;
import algorithm.nsgaiii.ReferencePoint;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.psea.AsfPreferenceModel;
import algorithm.psea.preferences.ASFBundle;
import algorithm.psea.preferences.PreferenceCollector;
import algorithm.rankers.AsfRanker;
import problems.Problem;
import utils.math.structures.Pair;

public class ExecutionHistory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8450564579213477117L;
	private static ExecutionHistory instance = null;

	protected ExecutionHistory(){
		// Exists only to defeat instantiation.
		this.generations = new ArrayList<>();
		this.lambdas = new ArrayList< ArrayList<AsfPreferenceModel> >();
		this.hyperplanePoints = new ArrayList< ArrayList<ReferencePoint> >();
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
	private int numLambdas;
	
	private Population targetPoints;
	private ArrayList<Population> generations;
	private ArrayList< ArrayList<AsfPreferenceModel> > lambdas;
	private ArrayList <ArrayList<ReferencePoint> > hyperplanePoints;
	private ArrayList<Solution> bestChebSol;
	private ArrayList<Double> bestChebVal;	
	private PreferenceCollector pc;
	private AsfRanker chebyshevRanker;
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
		return generations.get(pos);
	}

	public ArrayList< ArrayList<AsfPreferenceModel> > getLambdasHistory() {
		return lambdas;
	}
	public ArrayList<AsfPreferenceModel> getLambdas(int id){
		return lambdas.get(id);
	}
	public void addLambdas(ArrayList<AsfPreferenceModel>  lambdas){
		this.lambdas.add(lambdas);
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
	
	public void setChebyshevRanker(AsfRanker chebRank){
		this.chebyshevRanker = chebRank;
	}
	
	public AsfRanker getChebyshevRanker(){
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
		lambdas = new ArrayList< ArrayList<AsfPreferenceModel> >();
		bestChebSol = new ArrayList <Solution>();
		bestChebVal = new ArrayList <Double>();
	}

	public void init(Problem problem, NSGAIII nsgaiii, ASFBundle lambda, AsfRanker decisionMakerRanker) {
		clear();
		setPopulationSize(nsgaiii.getPopulation().size());
		setProblem(problem);
		setNumVariables(problem.getNumVariables());
		setNumObjectives(problem.getNumObjectives());
		addGeneration(nsgaiii.getPopulation().copy());
		addLambdas(lambda.getLambdas());
		addHyperplanePoints(nsgaiii.getHyperplane());
		setTargetPoints(problem.getReferenceFront());
		setPreferenceCollector(PreferenceCollector.getInstance());
		setChebyshevRanker(decisionMakerRanker);
		setLambdasConverged(false);
		setNumLambdas(lambda.getNumLambdas());
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public Problem getProblem(){
		return this.problem;
	}
	
	public void update(Population population, ASFBundle lambda, Hyperplane hp) {
		addGeneration(population.copy());
		ArrayList <AsfPreferenceModel> lambdasCopy = new ArrayList <> (lambda.getLambdas()); 
		addLambdas(lambdasCopy);
		addBestChebVal(getChebyshevRanker().getBestSolutionVal(population));
		addHyperplanePoints(hp);
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

	public int getNumLambdas() {
		return numLambdas;
	}

	public void setNumLambdas(int numLambdas) {
		this.numLambdas = numLambdas;
	}

	public ArrayList<ReferencePoint> getHyperplanePoints(int id) {
		return hyperplanePoints.get(id);
	}

	public void addHyperplanePoints(Hyperplane hyperplane) {
		this.hyperplanePoints.add( (ArrayList<ReferencePoint>)hyperplane.getReferencePoints().clone());
	}
}
