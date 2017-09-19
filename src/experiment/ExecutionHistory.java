package experiment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solution.DoubleSolution;
import algorithm.nsgaiii.NSGAIII;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import algorithm.psea.preferences.ASFBundle;
import algorithm.psea.preferences.PreferenceCollector;
import artificialDM.ArtificialDM;
import artificialDM.ArtificialDM;
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
		this.populations = new ArrayList<>();
		this.asfBundles = new ArrayList<ASFBundle>();
		this.hyperplanePoints = new ArrayList< ArrayList<ReferencePoint> >();
		this.bestAdmSol = new ArrayList <DoubleSolution>();
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
	private ArrayList<Population> populations;
	private ArrayList<ASFBundle> asfBundles;
	private ArrayList <ArrayList<ReferencePoint> > hyperplanePoints;
	private ArrayList<DoubleSolution> bestAdmSol;
	private ArrayList<Double> bestChebVal;	
	private PreferenceCollector pc;
	private ArtificialDM artificialDecisionMaker;
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
	public ArrayList<Population> getPopulations() {
		return populations;
	}
	public void addGeneration(Population pop){
		this.populations.add(pop);
	}
	public Population getPopulation(int pos){
		return populations.get(pos);
	}

	public ArrayList< ASFBundle > getASFbundles() {
		return asfBundles;
	}
	public ASFBundle getAsfBundle(int id){
		return asfBundles.get(id);
	}
	public void addAsfBundle(ASFBundle  asfBundle){
		this.asfBundles.add(asfBundle);
	}
	public double getBestChebVal(int id){
		return bestChebVal.get(id);
	}
	public void addBestAdmSol(DoubleSolution sol){
		this.bestAdmSol.add(sol);
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

	public DoubleSolution getBestChebSol(int pos) {
		return this.bestAdmSol.get(pos);
	}
	public void addBestChebSol(DoubleSolution s) {
		this.bestAdmSol.add(s);
	}
	
	public void setADM(ArtificialDM artificialDecisionMaker){
		this.artificialDecisionMaker = artificialDecisionMaker;
	}
	
	public ArtificialDM getADM(){
		return this.artificialDecisionMaker;
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
		return this.populations.size();
	}

	public void clear() {
		populations = new ArrayList<>();
		asfBundles = new ArrayList< ASFBundle >();
		bestAdmSol = new ArrayList <DoubleSolution>();
		bestChebVal = new ArrayList <Double>();
		hyperplanePoints.clear();
	}

	public void init(Problem problem, NSGAIII nsgaiii, ASFBundle asfBundle, ArtificialDM adm) {
		clear();
		setPopulationSize(nsgaiii.getPopulation().size());
		setProblem(problem);
		setNumVariables(problem.getNumVariables());
		setNumObjectives(problem.getNumObjectives());
		addGeneration(nsgaiii.getPopulation().copy());
		addAsfBundle(asfBundle);
		addHyperplanePoints(nsgaiii.getHyperplane());
		setTargetPoints(problem.getReferenceFront());
		setPreferenceCollector(PreferenceCollector.getInstance());
		setADM(adm);
		setAsfBundleConverged(false);
		setNumPrefModels(asfBundle.size());
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public Problem getProblem(){
		return this.problem;
	}
	
	public void update(Population population, ASFBundle asfBundle, Hyperplane hp) {
		addGeneration(population.copy());
		ASFBundle asfBundleCopy = asfBundle.copy(); 
		addAsfBundle(asfBundleCopy);
		addBestAdmSol(getADM().getBestSolutionVal(population));
		addHyperplanePoints(hp);
	}

	public boolean isLambdasConverged() {
		return lambdasConverged;
	}

	public void setAsfBundleConverged(boolean lambdasConverged) {
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

	public void setNumPrefModels(int numLambdas) {
		this.numLambdas = numLambdas;
	}

	public ArrayList<ReferencePoint> getHyperplanePoints(int id) {
		return hyperplanePoints.get(id);
	}

	public void addHyperplanePoints(Hyperplane hyperplane) {
		this.hyperplanePoints.add( (ArrayList<ReferencePoint>)hyperplane.getReferencePoints().clone());
	}
}
