package problems;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.geneticAlgorithm.solutions.Solution;
import artificialDM.ArtificialDM;
import artificialDM.AsfDM;
import utils.math.Geometry;

public abstract class Problem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5151466907576488480L;
	protected int numVariables = 0;
	protected int numObjectives = 0;
	protected String name = "Abstract Problem";
	protected double[] idealPoint = null;

	public Problem(int numVariables, int numObjectives, int numConstraints, String name) {
		this.numVariables = numVariables;
		this.numObjectives = numObjectives;
		this.name = name;
	}

	public abstract Solution createSolution();
	
	public Population createPopulation(int size){
		Population population = new Population();
		for(int i=0; i<size; i++){
			population.addSolution( createSolution() );
		}
		return population;
	}

	public abstract void evaluate(Solution solution);

	public void evaluate(Population pop) {
		for(int i=0; i<pop.size(); i++){
			evaluate(pop.getSolution(i));
		}
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
	
	public abstract Population getReferenceFront();
}
