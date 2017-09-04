package experiment;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.nsgaiii.NSGAIII;
import algorithm.rankers.NonDominationRanker;
import experiment.metrics.IGD;
import problems.Problem;
import problems.wfg.*;

public class NSGAIIITest {
	
	private static final double DEFAULT_NUM_GENERATIONS = 350;
	
	public static void main(String [] args){
		
		/* TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		 */
		Problem problem = new WFG3();
		NSGAIII nsgaiii = new NSGAIII(	problem, 
				new SBX(problem),
				new PolynomialMutation(problem),
				new BinaryTournament(new NonDominationRanker())
				);
		for(int i=0; i < DEFAULT_NUM_GENERATIONS; i++){
			nsgaiii.nextGeneration();
		}
		Population finalPop = nsgaiii.getPopulation();
		Population firstFront = NonDominationRanker.sortPopulation(finalPop).get(0);
		double igd = IGD.execute(problem.getReferenceFront(), firstFront);
		PythonVisualizer pv = new PythonVisualizer();
		pv.visualise(problem.getReferenceFront(), firstFront);
		System.out.println("IGD = " + igd);
	}
}