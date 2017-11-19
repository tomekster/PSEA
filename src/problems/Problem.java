package problems;

import java.io.Serializable;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.Solution;

public abstract class Problem <S extends Solution> implements Serializable {
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

	public abstract S createSolution();
	
	public Population <S> createPopulation(int size){
		Population <S> population = new Population <S> ();
		for(int i=0; i<size; i++){
			population.addSolution( createSolution() );
		}
		return population;
	}

	  /**
	   * Evaluates a solution
	   *
	   * @param solution The solution to evaluate
	   * @throws org.uma.jmetal.util.JMetalException
	   */
	public abstract void evaluate(S solution);

	public void evaluate(Population <S> pop) {
		for(int i=0; i<pop.size(); i++){
			evaluate(pop.getSolution(i));
		}
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
	
	public abstract Population <Solution> getReferenceFront();
}
