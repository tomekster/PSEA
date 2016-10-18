package experiment;

import core.EA;
import core.Population;
import core.Problem;
import core.points.Solution;
import solutionRankers.ChebyshevRanker;
import utils.Pair;

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
	protected Pair<Solution, Double> evaluateGeneration(Population pop) {
		return cr.getBestSolutionVal(pop);
	}

}
