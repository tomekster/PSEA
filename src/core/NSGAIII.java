package core;

import java.util.ArrayList;

import core.hyperplane.ReferencePoint;
import history.NSGAIIIHistory;
import igd.IGD;
import igd.ReferenceFrontGenerator;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import utils.NonDominatedSort;

public class NSGAIII implements Runnable {

	private Problem problem;
	private Population population;
	private int populationSize, numGenerations;

	private SelectionOperator selectionOperator;
	private CrossoverOperator crossoverOperator;
	private MutationOperator mutationOperator;
	private NicheCountSelection nicheCountSelection;
	private NSGAIIIHistory history;

	public NSGAIII(Problem problem, int numGenerations) {
		this.problem = problem;
		this.numGenerations = numGenerations;
		this.nicheCountSelection = new NicheCountSelection(problem.getNumObjectives());
		this.populationSize = nicheCountSelection.getPopulationSize();
		
		this.population = createInitialPopulation();
		
		this.selectionOperator = new BinaryTournament();
		this.crossoverOperator = new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound());
		this.mutationOperator = new PolynomialMutation(1.0 / problem.getNumVariables(), 20, problem.getLowerBound(),
				problem.getUpperBound());
		this.history = new NSGAIIIHistory(numGenerations);
		problem.evaluate(population);
		history.setInitialPopulation(population.copy());
		history.setReferencePoints(nicheCountSelection.getHyperplane().getReferencePoints());
	}
	
	public double judgeResult(Population result){
		ArrayList <ReferencePoint> referencePoints = nicheCountSelection.getHyperplane().getReferencePoints();
		double igd = IGD.execute(ReferenceFrontGenerator.generate(referencePoints, problem), result);
		return igd;
	}

	public void run() {
		for(int i = 0; i < numGenerations; i++){
			System.out.println("GENERATION: " + (i+1));
//			System.out.println("GENERATION: " + i);
			nextGeneration();
			history.addGeneration(population.copy());
		}
	}	


	public Population nextGeneration() {
		// System.out.println("POPULATION: " + population.size());
		// System.out.println(population);
		Population offspring = createOffspring(population);
		// System.out.println("OFFSPRING: " + offspring.size());
		// System.out.println(offspring);
		Population combinedPopulation = new Population();

		combinedPopulation.addSolutions(population);
		combinedPopulation.addSolutions(offspring);

		problem.evaluate(combinedPopulation);

		// System.out.println("COMBINED POPULATION: " +
		// combinedPopulation.size());
		// System.out.println(combinedPopulation);

		ArrayList<Population> fronts = NonDominatedSort.execute(combinedPopulation);

		Population allFronts = new Population();
		Population allButLastFront = new Population();
		Population lastFront = null;

		for (Population front : fronts) {
			if (allButLastFront.size() + front.size() >= populationSize) {
				lastFront = front;
				break;
			}

			for (Solution s : front.getSolutions()) {
				allButLastFront.addSolution(s.copy());
			}
		}

		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);

		// System.out.println("ALL SOLUTIONS: " + allFronts.size());
		// System.out.println(allFronts);
		// System.out.println("ALL BUT LAST FRONT: " + allButLastFront.size());
		// System.out.println(allButLastFront);
		// System.out.println("LAST FORNT: " + lastFront.size());
		// System.out.println(lastFront);

		if (allFronts.size() == populationSize) {
			population = allFronts.copy();
		} else {
			population = new Population();
			int K = populationSize - allButLastFront.size();
			Population kPoints = nicheCountSelection.selectKPoints(allFronts, allButLastFront, lastFront, K);
			population.addSolutions(allButLastFront.copy());
			// System.out.println("K_POINTS: " + kPoints);
			population.addSolutions(kPoints.copy());
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
