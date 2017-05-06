package experiment;

import core.Population;
import core.Problem;
import core.algorithm.NSGAIII;
import igd.IGD;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import problems.dtlz.*;
import problems.wfg.*;
import solutionRankers.NonDominationRanker;
import utils.WfgPythonVisualizer;

public class NSGAIIITest {
	
	public static void main(String [] args){
		
		/*
		 * WFG1 - something goes wrong here - obtained front looks weird
		 * WFG3 - ideal point has some negative coordinates TODO
		 * WFG8 - difficult problem
		 */
		Problem problem = new WFG3();
		int numGenerations = 350;
		NSGAIII nsgaiii = new NSGAIII(	problem, 
				new BinaryTournament(new NonDominationRanker()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		for(int i=0; i < numGenerations; i++){
			nsgaiii.nextGeneration();
		}
		Population finalPop = nsgaiii.getPopulation();
		Population firstFront = NonDominationRanker.sortPopulation(finalPop).get(0);
		double igd = IGD.execute(problem.getReferenceFront(), firstFront);
		WfgPythonVisualizer pv = new WfgPythonVisualizer();
		pv.visualise(problem.getReferenceFront(), firstFront);
		System.out.println("IGD = " + igd);
	}
}