package experiment.continuous;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.SingleObjectiveEA;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.AsfDmBuilder;
import algorithm.evolutionary.operators.CrossoverOperator;
import algorithm.evolutionary.operators.MutationOperator;
import algorithm.evolutionary.operators.SelectionOperator;
import algorithm.evolutionary.operators.impl.crossover.SBX;
import algorithm.evolutionary.operators.impl.mutation.PolynomialMutation;
import algorithm.evolutionary.operators.impl.selection.BinaryTournament;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import experiment.ExperimentRunner;
import problems.ContinousProblem;
import problems.dtlz.DTLZ1;
import utils.comparators.NondominationComparator;
import utils.math.structures.Point;

public class ExperimentSingleObjectiveContinuous {
	public static void main(String args[]) {
		int numObj 										= 8;
		double rho 										= 0.0001;
		int popSize 									= 100;
		int numGen 										= 600;
		int asfDmId										= 1;
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
				(numObj); 
		SelectionOperator so 							= new BinaryTournament( new NondominationComparator<Solution> (p.getOptimizationType()) );
		CrossoverOperator<VectorSolution<Double>> co 	= new SBX(p);
		MutationOperator<VectorSolution<Double>> mo 	= new PolynomialMutation(p);
		Point trueIdeal 								= new Point(numObj);
		AsfDm asfDM 									= AsfDmBuilder.getAsfDm(asfDmId, numObj, trueIdeal, rho);
		
		
		SingleObjectiveEA<VectorSolution<Double>> alg = new SingleObjectiveEA<>(p, popSize, new EA.GeneticOperators<>(so, co, mo), asfDM);
		ExperimentRunner.run(alg, p,  asfDM, numGen, numObj);
	}
}
