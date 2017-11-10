	package algorithm.geneticAlgorithm;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.geneticAlgorithm.solutions.Solution;
import problems.Problem;

/**
 * Abstract class implementing general scheme of every EvolutionaryAlgorithm.
 * Contains information about problem that is solved, current population, and 
 * genetic operators used (crossover, mutation, selection).
 * @author Tomasz
 *
 */
public abstract class EA {
	protected Problem problem;
	protected Population population;
	protected SelectionOperator selectionOperator;
	protected CrossoverOperator crossoverOperator;
	protected MutationOperator mutationOperator;
	protected int generation;
	
	protected EA(Problem problem, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, MutationOperator mutationOperator){
		this.generation = 0;
		this.problem = problem;
		// Standard genetic operators used in evolutionary algorithms
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
	}

	/**
	 * This methods replaces current population with a new one. 
	 * First new offspring is created using operators provided on EA instantiation.
	 * Next method-specific selection procedure is used to select |population| solutions 
	 * from combined set consisting of current population and obtained offspring.
	 * During this procedure every solution is evaluated twice to make sure that 
	 * values of all objectives are up-to-date when selection method is executed and when
	 * procedure ends.
	 */
	public void nextGeneration() {
		problem.evaluate(population);
		generation++;
		assert population.size() % 2 == 0;
		Population offspring = createOffspring(population);
				
		Population combinedPopulation = new Population();

		combinedPopulation.addSolutions(population);
		combinedPopulation.addSolutions(offspring);

		problem.evaluate(combinedPopulation);
		population = selectNewPopulation(combinedPopulation);
		problem.evaluate(population);
	}


	/**
	 * Return offspring obtained from given population using operators provided on EA instantiation. 
	 * Returned offspring is of the same size as provided population.
	 * @param population of solutions
	 * @return offspring
	 */
	protected Population createOffspring(Population population) {
		Population offspring = new Population();
		Population matingPopulation = new Population();
		while (matingPopulation.size() < population.size()) {
			matingPopulation.addSolution(selectionOperator.execute(population)); //Selecion operator returns deep copy of solution
		}
		for (int i = 0; i < population.size(); i += 2) {
			ArrayList<Solution> parents = new ArrayList<Solution>(2);
			parents.add(matingPopulation.getSolution(i));
			parents.add(matingPopulation.getSolution(i + 1));
			ArrayList<Solution> children = crossoverOperator.execute(parents);

			for(int j=0; j<3; j++){
				mutationOperator.execute(children.get(0));
				mutationOperator.execute(children.get(1));
			}
			
			offspring.addSolution(children.get(0));
			offspring.addSolution(children.get(1));
		}
		return offspring;
	}

	/**
	 * This method is used to generate a population for next generation of evolutionary algorithm.
	 * In every generation current population and offspring are merged into single set, therefore
	 * a method is needed to choose which part of this set should be kept as a new population.
	 *  
	 * @param pop - combined current population and offspring generated in current generation
	 * @return population of selected solutions -> shallow copy! <-
	 */
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
