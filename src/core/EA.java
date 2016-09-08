package core;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import history.NSGAIIIHistory;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import utils.Pair;

public abstract class EA implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(EA.class.getName());

	protected Problem problem;
	protected Population population;
	protected int populationSize, numGenerations;

	protected SelectionOperator selectionOperator;
	protected CrossoverOperator crossoverOperator;
	protected MutationOperator mutationOperator;
	protected NSGAIIIHistory history;

	protected EA(Problem problem, int numGenerations, int populationSize) {
		// Defines problem that NSGAIII will solve
		this.problem = problem;

		this.populationSize = populationSize;
		this.population = createInitialPopulation();

		// Standard genetic operations used in evolutionary algorithms
		this.selectionOperator = new BinaryTournament();
		this.crossoverOperator = new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound());
		this.mutationOperator = new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(),
				problem.getUpperBound());

		// Parameters of algorithm execution
		this.numGenerations = numGenerations;
		
		// Structure for storing intermediate state of algorithm for further
		// analysis, display, etc.
		this.history = new NSGAIIIHistory(numGenerations);
		history.setNumGenerations(numGenerations);
		history.setPopulationSize(populationSize);
		history.setNumVariables(problem.getNumVariables());
		history.setNumObjectives(problem.getNumObjectives());
		history.addGeneration(population.copy());
	}

	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running EA for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations + " generations with popultion size " + this.populationSize + ".");

		for (int generation = 0; generation < numGenerations; generation++) {
			nextGeneration();
			problem.evaluate(population);
			history.addGeneration(population.copy());
			history.addBestChebVal(evaluateGeneration(population));
		}
	}

	protected Population createInitialPopulation() {
		Population population = new Population();
		for (int i = 0; i < populationSize; i++) {
			population.addSolution(problem.createSolution());
		}
		problem.evaluate(population);
		return population;
	}

	public Population nextGeneration() {
		Population offspring = createOffspring(population);
		Population combinedPopulation = new Population();

		combinedPopulation.addSolutions(population);
		combinedPopulation.addSolutions(offspring);

		problem.evaluate(combinedPopulation);
		population = selectNewPopulation(combinedPopulation);
		return population;
	}

	protected abstract Population selectNewPopulation(Population pop);

	protected Population createOffspring(Population population) {
		Population offspring = new Population();
		Population matingPopulation = new Population();

		while (matingPopulation.size() < populationSize) {
			matingPopulation.addSolution(selectionOperator.execute(population));
		}

		for (int i = 0; i < populationSize; i += 2) {
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

	protected abstract Pair<Solution, Double> evaluateGeneration(Population pop);

	public int getNumGenerations() {
		return numGenerations;
	}

	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
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

	public Problem getProblem() {
		return problem;
	}

	public Population getPopulation() {
		return population;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public NSGAIIIHistory getHistory() {
		return history;
	}

	public void setHistory(NSGAIIIHistory history) {
		this.history = history;
	}
}
