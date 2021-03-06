package core.algorithm;

import java.util.ArrayList;

import core.Population;
import core.Problem;
import core.points.Solution;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;

public abstract class EA {
	protected Population population;
	protected SelectionOperator selectionOperator;
	protected CrossoverOperator crossoverOperator;
	protected MutationOperator mutationOperator;
	protected Problem problem;
	
	protected EA(Problem problem, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, MutationOperator mutationOperator){
		this.problem = problem;
		// Standard genetic operators used in evolutionary algorithms
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
	}

	public void nextGeneration() {
		assert population.size() % 2 == 0;
		Population offspring = createOffspring(population);
		Population combinedPopulation = new Population();

		combinedPopulation.addSolutions(population);
		combinedPopulation.addSolutions(offspring);

		population = selectNewPopulation(combinedPopulation);
		problem.evaluate(population);
	}


	protected Population createOffspring(Population population) {
		Population offspring = new Population();
		Population matingPopulation = new Population();
		while (matingPopulation.size() < population.size()) {
			matingPopulation.addSolution(selectionOperator.execute(population));
		}
		for (int i = 0; i < population.size(); i += 2) {
			ArrayList<Solution> parents = new ArrayList<Solution>(2);
			parents.add(matingPopulation.getSolution(i));
			parents.add(matingPopulation.getSolution(i + 1));
			ArrayList<Solution> children = crossoverOperator.execute(parents);

			mutationOperator.execute(children.get(0));
			mutationOperator.execute(children.get(1));

			offspring.addSolution(children.get(0));
			offspring.addSolution(children.get(1));
		}
		return offspring;
	}

	protected abstract Population selectNewPopulation(Population pop);
	
	public Population getPopulation(){
		return population;
	}

	public SelectionOperator getSelectionOperator() {
		return selectionOperator;
	}

	public void setSelectionOperator(SelectionOperator selectionOperator) {
		this.selectionOperator = selectionOperator;
	}

	public CrossoverOperator getCrossoverOperator() {
		return crossoverOperator;
	}

	public void setCrossoverOperator(CrossoverOperator crossoverOperator) {
		this.crossoverOperator = crossoverOperator;
	}

	public MutationOperator getMutationOperator() {
		return mutationOperator;
	}

	public void setMutationOperator(MutationOperator mutationOperator) {
		this.mutationOperator = mutationOperator;
	}

}
