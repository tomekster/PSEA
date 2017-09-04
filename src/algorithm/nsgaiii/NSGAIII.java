package algorithm.nsgaiii;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.EA;
import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.rankers.NonDominationRanker;
import problems.Problem;

public class NSGAIII extends EA {
	
	// Hyperplane is one of core objects used in NSGA-III algorithm. It is responsible 
	// for keeping solutions uniformly spread among objective space.
	private Hyperplane hyperplane;
	private int populationSize;

	public NSGAIII(Problem problem, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator) {
		super(  problem, crossoverOperator, mutationOperator, selectionOperator);
		this.problem = problem;
		this.hyperplane = new Hyperplane(problem.getNumObjectives());
		
		// Size of population depends on Hyperplane because the number of solutions in NSGA-III algorithm 
		// should be as close to the number of Reference Points on Hyperplane as possible
		this.populationSize = hyperplane.getReferencePoints().size();
		this.populationSize += this.populationSize % 2;
		this.population = problem.createPopulation(populationSize);
	}

	/**
	 * New population is obtained by applying non-dominated-sort to current population and including all solutions belonging 
	 * to first p-1 fronts, such that the number of solutions in those p-1 fronts is less or equal to popSize, and the total
	 * number of solutions in first p fronts is greater then popSize.
	 * 
	 *  If number of solutions in those p-1 fronts (allButLastFront) is less then popSize, the remaining k solutions are chosen
	 *  from p-th front using EnvironmentalSelection procedure.
	 */
	@Override
	public Population selectNewPopulation(Population pop) {
		assert pop.size() == 2*populationSize;
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(pop);
		
		Population allButLastFront = new Population();
		Population lastFront = null;
		
		for (Population front : fronts) {
			if (allButLastFront.size() + front.size() > populationSize) {
				lastFront = front;
				break;
			}
			allButLastFront.addSolutions(front);
		}
			
		assert allButLastFront.size() <= populationSize;
		assert allButLastFront.size() + lastFront.size() >= populationSize;
		
		Population res;
		if (allButLastFront.size() == populationSize) {
			res = allButLastFront;
		} else {
			res = chooseSolutionsFromLastFront(allButLastFront, lastFront);
		}
		return res;
	}

	private Population chooseSolutionsFromLastFront(Population allButLastFront, Population lastFront) {
		int K = populationSize - allButLastFront.size();
		Population kPoints = EnvironmentalSelection.selectKPoints(problem.getNumObjectives(), allButLastFront, lastFront, K, hyperplane);
		assert kPoints.size() == K; 
		assert K + allButLastFront.size() == populationSize; 
		allButLastFront.addSolutions(kPoints);
		return allButLastFront.copy();
	}

	public Hyperplane getHyperplane() {
		return hyperplane;
	}
	
	public void setHyperplane(Hyperplane hyperplane){
		this.hyperplane = hyperplane;
	}
}
