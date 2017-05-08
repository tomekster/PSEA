package core.algorithm;

import java.util.logging.Level;
import java.util.logging.Logger;

import core.Problem;
import experiment.ExecutionParameters;
import history.ExecutionHistory;
import preferences.Elicitator;
import solutionRankers.ChebyshevRanker;

public class RST_NSGAIII_FixedNumGen extends RST_NSGAIII{
	private int numGenerations1;
	private int numGenerations2;
	
	public RST_NSGAIII_FixedNumGen(Problem problem, int numExplor, int numExploit, int numElic1, int numElic2, int elicInter, ChebyshevRanker cr, int numLambdas) {
		super(problem, cr, numLambdas, elicInter, numElic1, numElic2);
		this.numGenerations1 = numExplor;
		this.numGenerations2 = numExploit;
		
		ExecutionHistory.getInstance().setNumGenerations1(numExplor);
		ExecutionHistory.getInstance().setNumGenerations2(numExploit);
		ExecutionHistory.getInstance().setNumElicitations1(numElic1);
		ExecutionHistory.getInstance().setNumElicitations2(numElic2);
	}
	
	public RST_NSGAIII_FixedNumGen(Problem problem, ExecutionParameters ep, ChebyshevRanker cr) {
		this(problem, ep.getNumExplorationGenerations(), ep.getNumExploitationGenerations(), ep.getNumElicitations1(), ep.getNumElicitations2(), ep.getElicitationInterval(), cr, ep.getNumLambdas());
	}

	private final static Logger LOGGER = Logger.getLogger(RST_NSGAIII_FixedNumGen.class.getName());
	
	/**
	 * Version with set number of exploration generations and exploitation generations
	 */
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII with fixed number generations");
			
		for(generation = 0; generation < numGenerations1 + numGenerations2; generation++){
			if(generation < numGenerations1){
				nsgaiii.nextGeneration();
				this.population = nsgaiii.getPopulation();
			}
			else{
				nextGeneration();
			}
			
			if(generation > numGenerations1 - elicitationInterval * numElic1
					&& generation < numGenerations1 + elicitationInterval * numElic2
					&& generation % elicitationInterval == 0){
				Elicitator.elicitateN(1, population, DMranker, lambda);
			}
			
			problem.evaluate(population);
			ExecutionHistory.getInstance().update(population, lambda);
		}
	}
}
