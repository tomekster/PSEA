package experiment.continuous;

import algorithm.evolutionary.EA.GeneticOperators;
import algorithm.evolutionary.interactive.artificialDM.LpDm;
import algorithm.evolutionary.operators.CrossoverOperator;
import algorithm.evolutionary.operators.MutationOperator;
import algorithm.evolutionary.operators.SelectionOperator;
import algorithm.evolutionary.operators.impl.crossover.SBX;
import algorithm.evolutionary.operators.impl.mutation.PolynomialMutation;
import algorithm.evolutionary.operators.impl.selection.BinaryTournament;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.implementations.psea.PSEA;
import algorithm.implementations.psea.PSEABuilder;
import experiment.ExperimentRunner;
import problems.ContinousProblem;
import problems.dtlz.DTLZ1;
import utils.SOOIdealPointFinder;
import utils.comparators.NondominationComparator;
import utils.math.structures.Point;

public class ExperimentMultiObjectiveContinuous {
	public static void main(String args[]) {
		
		int numObj 				= 2;
		double rho 				= 0.0001;
		int idealFinderPopSize 	= 100;
		int numGen 				= 100;
		int asfDmId				= 4;
		
		ContinousProblem p = new 
				DTLZ1
//				DTLZ2
//				DTLZ3
//				DTLZ4
				
//				WFG1
//				WFG2
//				WFG3
//				WFG4
//				WFG5
//				WFG6
//				WFG7
//				WFG8
//				WFG9
//				ZDT3
				(numObj);
				
		
		SelectionOperator so = new BinaryTournament( new NondominationComparator<Solution> (p.getOptimizationType()) );
		CrossoverOperator<VectorSolution<Double>> co = new SBX(p);
		MutationOperator<VectorSolution<Double>> mo = new PolynomialMutation(p);
		
		Point idealPoint = SOOIdealPointFinder.findIdealPoint(p, numGen, idealFinderPopSize);
		//AsfDm asfDM = AsfDmBuilder.getAsfDm(asfDmId, numObj, idealPoint, rho);
		LpDm simulatedDm = new LpDm(6, idealPoint);
		PSEABuilder<VectorSolution<Double>> builder = new PSEABuilder<>(p, simulatedDm, new GeneticOperators<>(so, co, mo));
		builder.setMaxExploitGenerations(1500);
		PSEA<VectorSolution<Double>>alg = new PSEA<>(builder);
		
		ExperimentRunner.run(alg, p, simulatedDm, numGen, numObj);
	}
}
