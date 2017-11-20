package algorithm.implementations.nsgaiii;

import java.util.ArrayList;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.nsgaiii.hyperplane.Hyperplane;
import problems.Problem;
import utils.NonDominationSort;

public class NSGAIII <S extends Solution> extends EA <S> {
	
	// Hyperplane is one of core objects used in NSGA-III algorithm. It is responsible 
	// for keeping solutions uniformly spread among objective space.
	private Hyperplane hyperplane;
	private int populationSize;

	public NSGAIII(Problem <S> problem, int popSize, EA.GeneticOperators<S> go) {
		super(problem, popSize, go);
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
	public Population <S> selectNewPopulation(Population <S> pop) {
		assert pop.size() == 2*populationSize;
		ArrayList<Population <S>> fronts = (ArrayList<Population<S>>) NonDominationSort.sortPopulation(pop, problem.getOptimizationType());
		
		Population <S> allButLastFront = new Population<>();
		Population <S> lastFront = new Population<>();
		
		for (Population <S> front : fronts) {
			if (allButLastFront.size() + front.size() > populationSize) {
				lastFront = front;
				break;
			}
			allButLastFront.addSolutions(front);
		}
			
		assert allButLastFront.size() <= populationSize;
		assert allButLastFront.size() + lastFront.size() > populationSize;
		
		if (allButLastFront.size() == populationSize) {
			return allButLastFront;
		} else {
			return chooseSolutionsFromLastFront(allButLastFront, lastFront);
		}
	}

	private Population <S> chooseSolutionsFromLastFront(Population <S> allButLastFront, Population <S> lastFront) {
		int K = populationSize - allButLastFront.size();
		Population <S> kPoints = EnvironmentalSelection.selectKPoints(problem.getNumObjectives(), allButLastFront, lastFront, K, hyperplane);
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
