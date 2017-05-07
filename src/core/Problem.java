package core;

import java.io.Serializable;

import core.algorithm.SingleObjectiveEA;
import core.points.Solution;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import problems.dtlz.DTLZ4;
import solutionRankers.ChebyshevRanker;
import utils.Geometry;
import utils.NSGAIIIRandom;
import utils.PythonVisualizer;

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
		NSGAIIIRandom random = NSGAIIIRandom.getInstance();
		double var[] = new double[numVariables];
		double obj[] = new double[numObjectives];
		for(int i=0; i<numVariables; i++){
			var[i] = lowerBound[i] + (upperBound[i] - lowerBound[i]) * random.nextDouble();
		}
		Solution s = new Solution(var,obj);
		return s;
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
		for(Solution p : pop.getSolutions()){
			Solution s = (Solution) p;
			evaluate(s);
		}
	}
	
	public double[] getTargetPoint(double[] direction){
		switch(this.name){
		case "DTLZ1":
			return Geometry.lineCrossDTLZ1HyperplanePoint(direction);
		case "DTLZ2":
		case "DTLZ3":
		case "DTLZ4":
			return Geometry.lineCrossDTLZ234HyperspherePoint(direction);
		}
		return null;
	}
	
	public double[] findIdealPoint(){
		double lambda[] = new double[numObjectives];
		double idealPoint[] = new double[numObjectives];
		
		for(int i=0; i<idealPoint.length; i++){
			idealPoint[i] = Double.MAX_VALUE;
		}
			
		for(int optimizedDim=0; optimizedDim < numObjectives; optimizedDim++){
			for(int i=0; i<lambda.length; i++){
				lambda[i] = 0;
			}
			lambda[optimizedDim] = 1;
			ChebyshevRanker cr = new ChebyshevRanker(lambda);
			int numGenerations = 100;
			
			SingleObjectiveEA so = new SingleObjectiveEA(	
				this,
				cr,
				90 //population size
			);
			for(int i=0; i < numGenerations; i++){
				so.nextGeneration();
			}
			//Workaround for inner class error
		    final int dummyOptimizedDim = optimizedDim;
			idealPoint[optimizedDim] = so.getPopulation().getSolutions().stream().mapToDouble(s -> s.getObjective(dummyOptimizedDim)).min().getAsDouble();
		
//			Population finalPop = so.getPopulation();
//			PythonVisualizer pv = new PythonVisualizer();
//			pv.visualise(getReferenceFront(), finalPop);
		}
		return idealPoint;
	}

	// TODO
	public abstract void evaluateConstraints(Solution solution);
	
	public void setBoundsOnVariables(){
		//To be overriden by subclasses
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double[] getLowerBound() {
		return this.lowerBound;
	}

	public double[] getUpperBound() {
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
