package core;

import java.util.ArrayList;

import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import utils.NonDominatedSort;

public class NSGAIII {

	Problem problem;
	Population population;
	int populationSize;
	int numGenerations;

	SelectionOperator selectionOperator = new BinaryTournament();
	CrossoverOperator crossoverOperator = new SBX(1.0, 30.0);
	MutationOperator mutationOperator = new PolynomialMutation(0.0, 0.0);

	public NSGAIII(Problem problem, int populationSize) {
		this.problem = problem;
		this.population = createInitialPopulation();
	}

	public Population run() {
		Population offspring = createOffspring(population);
		Population combinedPopulation = new Population();

		for (Solution s : population.getSolutions())
			combinedPopulation.addSolution(s);
		for (Solution s : offspring.getSolutions())
			combinedPopulation.addSolution(s);

		ArrayList<Front> fronts = NonDominatedSort.execute(combinedPopulation);

		Population nextPopulation = new Population();

		int lastFrontId = 0;
		do {
			for (Solution s : fronts.get(lastFrontId).getSolutions()) {
				nextPopulation.addSolution(s);
			}
		} while (nextPopulation.size() < populationSize);

		Front lastFront = fronts.get(lastFrontId);

		if (nextPopulation.size() == populationSize) {
			population = nextPopulation.copy();
		} else {
				for(int i=0; i < lastFrontId; i++){
					for(Solution s : fronts.get(i).getSolutions()){
						nextPopulation.addSolution(s.copy());
					}
				}
				
				int K = populationSize - nextPopulation.size();
				
		}

		return population;
	}

	private Population createOffspring(Population population) {
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

	private Population createInitialPopulation() {
		Population population = new Population();
		for (int i = 0; i < populationSize; i++) {
			population.addSolution(problem.createSolution());
		}
		return population;
	}

}
