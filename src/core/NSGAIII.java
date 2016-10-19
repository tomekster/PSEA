package core;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.Hyperplane;
import core.points.Solution;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import solutionRankers.NonDominationRanker;
import utils.DegeneratedMatrixException;

public class NSGAIII extends EA {

	private final static Logger LOGGER = Logger.getLogger(NSGAIII.class.getName());

	private Hyperplane hyperplane;
	private Problem problem;
	private int populationSize;

	public NSGAIII(Problem problem, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
		super(  selectionOperator, crossoverOperator, mutationOperator);
		this.problem = problem;
		
		// Hyperplane is one of basic constructs used in NSGA-III algorithm. It is responsible 
		// for keeping solutions uniformly spread among objective space. 
		// In modified NSGA-III it is used to store information about directions which are interesting 
		// from DM's point of view, based on preference information elicitated during algorithm run 
		this.hyperplane = new Hyperplane(problem.getNumObjectives());
		
		// Number of solutions in every generation. Depends on Hyperplane because number 
		// of solutions in population should be close to number of Reference Points on Hyperplane
		this.populationSize = hyperplane.getReferencePoints().size();
		this.populationSize += this.populationSize % 2;
		this.population = problem.createPopulation(populationSize);
	}

	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives.");
		nextGeneration();
	}

	@Override
	public Population selectNewPopulation(Population pop) {
		assert pop.size() == 2*populationSize;
		problem.evaluate(pop);
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(pop);

		Population allFronts = new Population();
		Population allButLastFront = new Population();
		Population lastFront = null;
		
		for (Population front : fronts) {
			if (allButLastFront.size() + front.size() >= populationSize) {
				lastFront = front;
				break;
			}
			allButLastFront.addSolutions(front);
		}

		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);
			
		assert allFronts.size() >= populationSize;
		assert allButLastFront.size() <= populationSize;
		assert allButLastFront.size() + lastFront.size() >= populationSize;
		
		Population res;
		if (allFronts.size() == populationSize) {
			res = allFronts.copy();
		} else {
			res = new Population();
			Population kPoints = new Population();
			int K = populationSize - allButLastFront.size();
			try {
				kPoints = NicheCountSelection.selectKPoints(problem.getNumObjectives(), allFronts, allButLastFront, lastFront, K, hyperplane);
			} catch (DegeneratedMatrixException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.addSolutions(allButLastFront.copy());
			res.addSolutions(kPoints.copy());
		}
			
		return res;
	}
}
