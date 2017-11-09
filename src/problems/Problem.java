package problems;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.geneticAlgorithm.Solution;
import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import artificialDM.ArtificialDM;
import artificialDM.AsfDM;
import artificialDM.DMType;
import artificialDM.SingleObjectiveDM;
import artificialDM.WeightedSumDM;
import experiment.PythonVisualizer;
import problems.dtlz.DTLZ4;
import problems.knapsack.Knapsack;
import problems.knapsack.KnapsackItem;
import utils.math.Geometry;
import utils.math.MyRandom;

public abstract class Problem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5151466907576488480L;
	protected int numVariables = 0;
	protected int numObjectives = 0;
	protected int numConstraints = 0;
	protected String name = "Abstract Problem";
	protected double[] idealPoint = null;

	public Problem(int numVariables, int numObjectives, int numConstraints, String name) {
		this.numVariables = numVariables;
		this.numObjectives = numObjectives;
		this.numConstraints = numConstraints;
		this.name = name;
	}

	public abstract Solution createSolution();
	
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
	
	public double[] getTargetAsfPoint(double[] pointOnLine){
		switch(this.name){
		case "DTLZ1":
			return Geometry.lineCrossDTLZ1HyperplanePoint(pointOnLine);
		case "DTLZ2":
		case "DTLZ3":
		case "DTLZ4":
		case "WFG6":
		case "WFG7":
			return Geometry.lineCrossDTLZ234HyperspherePoint(pointOnLine);
		}
		if(this.name.contains("knap")){
			try(BufferedReader br = new BufferedReader(new FileReader(Paths.get("/home/tomasz/Dropbox/experiments/knapsack/reference_front", "pareto_front_"+numVariables+"_"+numObjectives).toFile()))) {
			    double bestVal = Double.MAX_VALUE;
			    double bestObj[] = new double[numObjectives];
			    double obj[];
			    while(true){
			    	String line = br.readLine();
			    	if(line ==null) break;
			    	String vals[] = line.trim().split(" ");
			    	obj= new double[vals.length];
			    	for(int i=0; i<vals.length; i++){
			    		obj[i] = -Integer.parseInt(vals[i]);
			    	}
			    	
			    	AsfDM dm = new AsfDM(this.idealPoint, Geometry.invert(pointOnLine));
			    	if(dm.eval(obj) < bestVal){
			    		bestVal = dm.eval(obj);
			    		bestObj = obj.clone();
			    	}
			    }
			    return bestObj;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public double[] getTargetWSPoint(double[] weights){
		double res[] = new double[weights.length];
		int minAt = 0;
		for (int i = 0; i < weights.length; i++) {
		    minAt = weights[i] < weights[minAt] ? i : minAt;
		}
		
		switch(this.name){
		case "DTLZ1":
			res[minAt] = 0.5;
			return res;
		case "DTLZ2":
		case "DTLZ3":
		case "DTLZ4":
			res[minAt] = 1;
			return res;
		}
		return null;
	}
	
	public double[] findIdealPoint(){
		CrossoverOperator co = new SBX(1.0, 30.0, ((ContinousProblem)this).getLowerBounds(), ((ContinousProblem)this).getUpperBounds());
		MutationOperator mo = new PolynomialMutation(1.0 / this.getNumVariables(), 20.0, ((ContinousProblem)this).getLowerBounds(), ((ContinousProblem)this).getUpperBounds());
		return findIdealPoint(co , mo);
	}
	
	public double[] findIdealPoint(CrossoverOperator co, MutationOperator mo){
		if(this.idealPoint != null) return this.idealPoint.clone();
		double idealPoint[] = new double[numObjectives];
		
		for(int i=0; i<idealPoint.length; i++){
			idealPoint[i] = Double.MAX_VALUE;
		}
			
		for(int optimizedDim=0; optimizedDim < numObjectives; optimizedDim++){
			SingleObjectiveDM soDM = new SingleObjectiveDM(optimizedDim);
			SingleObjectiveEA so = new SingleObjectiveEA(	
				this,
				new BinaryTournament(soDM),
				co,
				mo,
				soDM
			);
			
			so.run();
			
			//Workaround for inner class error
		    final int dummyOptimizedDim = optimizedDim;
			idealPoint[optimizedDim] = so.getPopulation().getSolutions().stream().mapToDouble(s -> s.getObjective(dummyOptimizedDim)).min().getAsDouble();
		}
		this.idealPoint = idealPoint;
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
	
	public abstract Population getReferenceFront();

	public double[] getTargetPoint(ArtificialDM adm) {
		if(adm.getType() == DMType.ASF){
			return getTargetAsfPoint( Geometry.invert(((AsfDM) adm).getLambda()) );
		} 
		else if(adm.getType() == DMType.WS){
			return getTargetWSPoint( ((WeightedSumDM)adm).getWeights());
		}
		else{
			throw new EnumConstantNotPresentException(DMType.class, "Decision maker type");
		}
	}
}
