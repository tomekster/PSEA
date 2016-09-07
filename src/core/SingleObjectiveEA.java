package core;

import solutionRankers.ChebyshevRanker;

public class SingleObjectiveEA extends EA {

	ChebyshevRanker cr;
	
	public SingleObjectiveEA(Problem problem, int numGenerations, int populationSize, ChebyshevRanker cr) {
		super(problem, numGenerations, populationSize);
		this.cr = cr;
	}

	@Override
	protected Population selectNewPopulation(Population pop) {
		Population res = new Population();
		Population sortedPop = cr.sortPopulation(pop);
		for(int i=0; i < this.getPopulationSize(); i++){
			res.addSolution(sortedPop.getSolution(i));
		}
		return res; 
	}

	@Override
	protected double evaluateGeneration(Population pop) {
		return cr.getMinChebVal(pop);
	}

}
