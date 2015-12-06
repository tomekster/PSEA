package core;

import java.util.ArrayList;

import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import utils.Comparator;
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

		ArrayList<Population> fronts = NonDominatedSort.execute(combinedPopulation);

		Population nextPopulation = new Population();

		int lastFrontId = 0;
		do {
			for (Solution s : fronts.get(lastFrontId).getSolutions()) {
				nextPopulation.addSolution(s);
			}
			lastFrontId++;
		} while (nextPopulation.size() < populationSize);
		lastFrontId--;
		Population lastFront = fronts.get(lastFrontId);

		if (nextPopulation.size() == populationSize) {
			population = nextPopulation.copy();
		} else {
				for(int i=0; i < lastFrontId ; i++){
					for(Solution s : fronts.get(i).getSolutions()){
						nextPopulation.addSolution(s.copy());
					}
				}
				int K = populationSize - nextPopulation.size();
		}
		
		normalize(nextPopulation, this.problem.getNumVariables());

		return population;
	}

	private void normalize(Population population, int numVariables) {
		Comparator cp = new Comparator();
		double z_min[] = new double[numVariables];
		for(int j=0; j<numVariables; j++){
			double min = Double.MAX_VALUE;
			for(Solution s : population.getSolutions()){
				min = cp.min(s.getVariable(j), min);
			}
			z_min[j] = min;
		}
		
		for(Solution s : population.getSolutions()){
			for(int j=0; j < s.getNumVariables(); j++){
				s.setVariable(j, s.getVariable(j) - z_min[j]);				
			}
		}
		
		Population extremePoints = computeExtremePoints(population, numVariables);
		
	}

	private Population computeExtremePoints(Population population, int numVariables) {
		Population extremePoints = new Population();
		for(int i=0; i<numVariables; i++){
			double min = Double.MAX_VALUE;
			Solution minSolution = null;
			for(Solution s : population.getSolutions()){
				double asf = ASF(s,i);
				if(asf < min){
					min = asf;
					minSolution = s;
				}
			}
			extremePoints.addSolution(minSolution);
		}
		return extremePoints;
	}

	private double ASF(Solution s, int i) {
		double res = Double.MAX_VALUE;
		double cur;
		for(int j=0; j<s.getNumVariables(); j++){
			if(j==i){
				cur = s.getVariable(j);
			} else{
				cur = s.getVariable(j) * 1000000;
			}
			res = Double.min(res, cur);
		}
		return res;
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
