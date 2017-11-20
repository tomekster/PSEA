package algorithm.evolutionary;

import java.util.ArrayList;

import algorithm.evolutionary.operators.CrossoverOperator;
import algorithm.evolutionary.operators.MutationOperator;
import algorithm.evolutionary.operators.SelectionOperator;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import problems.Problem;

/**
 * Abstract class implementing general scheme of every EvolutionaryAlgorithm.
 * Contains information about problem that is solved, current population, and 
 * genetic operators used (crossover, mutation, selection).
 * @author Tomasz
 *
 */
public abstract class EA <S extends Solution>{
	protected Problem <S> problem;
	protected Population <S> population;
	protected SelectionOperator selectionOperator;
	protected CrossoverOperator <S> crossoverOperator;
	protected MutationOperator <S> mutationOperator;
	protected int generation;
	protected int popSize;
	
	public EA(Problem <S> problem, int popSize, GeneticOperators<S> go){
		this.generation = 0;
		this.problem = problem;
		this.popSize = popSize;
		// Standard genetic operators used in evolutionary algorithms
		this.selectionOperator = go.getSelectionOperator();
		this.crossoverOperator = go.getCrossoverOperator();
		this.mutationOperator = go.getMutationOperator();
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
		Population <S> offspring = createOffspring(population);
				
		Population <S> combinedPopulation = new Population <>();

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
	protected Population <S> createOffspring(Population <S> population) {
		Population <S> offspring = new Population<>();
		Population <S> matingPopulation = new Population<>();
		while (matingPopulation.size() < population.size()) {
			matingPopulation.addSolution((S) selectionOperator.execute(population)); //Selecion operator returns deep copy of solution
		}
		for (int i = 0; i < population.size(); i += 2) {
			ArrayList<S> parents = new ArrayList<S>(2);
			parents.add(matingPopulation.getSolution(i));
			parents.add(matingPopulation.getSolution(i + 1));
			ArrayList<S> children = crossoverOperator.execute(parents);

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
	protected abstract Population <S> selectNewPopulation(Population <S> pop);
	
	public static class GeneticOperators <S extends Solution>{
		private final SelectionOperator so;
		private final CrossoverOperator<S> co;
		private final MutationOperator<S> mo;
		
		public GeneticOperators(SelectionOperator so, CrossoverOperator<S> co, MutationOperator<S> mo){
			this.so = so;
			this.co = co;
			this.mo = mo;
		}
		
		public SelectionOperator getSelectionOperator(){
			return this.so;
		}
		
		public CrossoverOperator <S> getCrossoverOperator(){
			return this.co;
		}
		
		public MutationOperator <S> getMutationOperator(){
			return this.mo;
		}
	}
	
	public Population <S> getPopulation(){
		return population;
	}

	public SelectionOperator getSelectionOperator() {
		return selectionOperator;
	}

	public void setSelectionOperator(SelectionOperator selectionOperator) {
		this.selectionOperator = selectionOperator;
	}

	public CrossoverOperator <S> getCrossoverOperator() {
		return crossoverOperator;
	}

	public void setCrossoverOperator(CrossoverOperator <S> crossoverOperator) {
		this.crossoverOperator = crossoverOperator;
	}

	public MutationOperator <S> getMutationOperator() {
		return mutationOperator;
	}

	public void setMutationOperator(MutationOperator <S> mutationOperator) {
		this.mutationOperator = mutationOperator;
	}
	
	public int getPopSize(){
		return popSize;
	}
	
	public void setPopSize(int popSize){
		this.popSize = popSize;
	}

}
