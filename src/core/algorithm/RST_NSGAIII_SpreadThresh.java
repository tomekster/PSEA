package core.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;

import core.Problem;
import experiment.ExecutionParameters;
import history.ExecutionHistory;
import preferences.Elicitator;
import solutionRankers.ChebyshevRanker;

public class RST_NSGAIII_SpreadThresh extends RST_NSGAIII{

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII_SpreadThresh.class.getName());
	
	private double spreadThreshold;
	
	public RST_NSGAIII_SpreadThresh(Problem problem, ExecutionParameters ep, ChebyshevRanker cr) {
		super(problem, cr, ep.getNumLambdas(), ep.getElicitationInterval(), ep.getNumElicitations1(), ep.getNumElicitations2());
		this.spreadThreshold = ep.getSpreadThreshold();
	}

	/**
	 * Version with set percentage of reference lines coverage instead of number of exploration generations 
	 */
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII with SpreadThreshold.");
		
		while( nsgaiii.getHyperplane().getNumNiched() < nsgaiii.getHyperplane().getReferencePoints().size() * spreadThreshold){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			ExecutionHistory.getInstance().update(population, lambda);
		}
		System.out.println("SPREAD REACHED GEN: " + generation);
		
		//Elicitate while population is well spread
		for(int t=0; t<numElic1 * elicitationInterval; t++){
			generation++;
			nsgaiii.nextGeneration();
			this.population = nsgaiii.getPopulation();
			if(t % elicitationInterval == 0){
				Elicitator.elicitateN(1, population, DMranker, lambda);
			}
			ExecutionHistory.getInstance().update(population, lambda);
		}
		
		//Guide evolution with generated model
		for(int t=0 ; t < 100 + 50 * problem.getNumObjectives(); t++){
			generation++;
			nextGeneration();
			if(t < numElic2 * elicitationInterval && t % elicitationInterval == 0){
				Elicitator.elicitateN(1, population, DMranker, lambda);
			}
			ExecutionHistory.getInstance().update(population, lambda);
		}
	}
}
