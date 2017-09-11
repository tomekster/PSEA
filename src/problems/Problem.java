package problems;

import java.io.Serializable;
import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import artificialDM.AsfDM;
import artificialDM.SingleObjectiveDM;
import experiment.PythonVisualizer;
import problems.dtlz.DTLZ4;
import utils.math.Geometry;
import utils.math.MyRandom;

public abstract class Problem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5151466907576488480L;
	private int numVariables = 0;
	private int numObjectives = 0;
	private int numConstraints = 0;
	private String name = "Abstract Problem";
	private double[] lowerBound;
	private double[] upperBound;

	public Problem(int numVariables, int numObjectives, int numConstraints, String name) {
		this.numVariables = numVariables;
		this.numObjectives = numObjectives;
		this.numConstraints = numConstraints;
		this.name = name;
		this.lowerBound = new double[numVariables];
		this.upperBound = new double[numVariables];
		for (int i = 0; i < numVariables; i++) {
			lowerBound[i] = Double.MIN_VALUE;
			upperBound[i] = Double.MAX_VALUE;
		}
		setBoundsOnVariables();
	}

	public Solution createSolution() {
		MyRandom random = MyRandom.getInstance();
		double var[] = new double[numVariables];
		double obj[] = new double[numObjectives];
		for(int i=0; i<numVariables; i++){
			var[i] = lowerBound[i] + (upperBound[i] - lowerBound[i]) * random.nextDouble();
		}
		return new Solution(var,obj);
	}
	
	public Population createPopulation(int size){
		Population population = new Population();
		for(int i=0; i<size; i++){
			population.addSolution( createSolution() );
		}
		evaluate(population);
		return population;
	}

	public abstract void evaluate(Solution solution);

	public void evaluate(Population pop) {
		for(int i=0; i<pop.size(); i++){
			evaluate(pop.getSolution(i));
		}
	}
	
	public double[] getTargetPoint(double[] pointOnLine){
		switch(this.name){
		case "DTLZ1":
			return Geometry.lineCrossDTLZ1HyperplanePoint(pointOnLine);
		case "DTLZ2":
		case "DTLZ3":
		case "DTLZ4":
			return Geometry.lineCrossDTLZ234HyperspherePoint(pointOnLine);
		}
		return null;
	}
	
	public double[] findIdealPoint(){
		double lambdaDirection[] = new double[numObjectives];
		double idealPoint[] = new double[numObjectives];
		
		for(int i=0; i<idealPoint.length; i++){
			idealPoint[i] = Double.MAX_VALUE;
		}
			
		for(int optimizedDim=0; optimizedDim < numObjectives; optimizedDim++){
			SingleObjectiveDM soDM = new SingleObjectiveDM(optimizedDim);
			SingleObjectiveEA so = new SingleObjectiveEA(	
				this,
				soDM
			);
			
			so.run();
			
			//Workaround for inner class error
		    final int dummyOptimizedDim = optimizedDim;
			idealPoint[optimizedDim] = so.getPopulation().getSolutions().stream().mapToDouble(s -> s.getObjective(dummyOptimizedDim)).min().getAsDouble();
		
//			Population finalPop = so.getPopulation();
//			PythonVisualizer pv = new PythonVisualizer();
//			pv.visualise(getReferenceFront(), finalPop);
		}
		return idealPoint;
	}

	// TODO - constrained problems
	public abstract void evaluateConstraints(Solution solution);
	
	public void setBoundsOnVariables(){
		//To be overridden by subclasses
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getLowerBounds() {
		return this.lowerBound;
	}

	public double[] getUpperBounds() {
		return this.upperBound;
	}

	public void setLowerBound(int pos, double val) {
		lowerBound[pos] = val;
	}

	public void setUpperBound(int pos, double val) {
		upperBound[pos] = val;
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

	public int getNumConstraints() {
		return numConstraints;
	}

	public void setNumConstraints(int numConstraints) {
		this.numConstraints = numConstraints;
	}

	public double getLowerBound(int pos) {
		return lowerBound[pos];
	}

	public double getUpperBound(int pos) {
		return upperBound[pos];
	}
	
	public abstract Population getReferenceFront();
}
