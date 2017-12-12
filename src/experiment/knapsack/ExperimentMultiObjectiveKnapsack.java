package experiment.knapsack;

import algorithm.evolutionary.EA.GeneticOperators;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.AsfDmBuilder;
import algorithm.evolutionary.operators.CrossoverOperator;
import algorithm.evolutionary.operators.MutationOperator;
import algorithm.evolutionary.operators.SelectionOperator;
import algorithm.evolutionary.operators.impl.crossover.PermutationCrossover;
import algorithm.evolutionary.operators.impl.mutation.PermutationMutation;
import algorithm.evolutionary.operators.impl.selection.BinaryTournament;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.implementations.psea.PSEA;
import algorithm.implementations.psea.PSEABuilder;
import experiment.ExperimentRunner;
import problems.knapsack.KnapsackProblemBuilder;
import problems.knapsack.KnapsackProblemInstance;
import utils.SOOIdealPointFinder;
import utils.comparators.NondominationComparator;
import utils.enums.OptimizationType;
import utils.math.structures.Point;

public class ExperimentMultiObjectiveKnapsack {
	public static void main(String args[]) {
		KnapsackProblemBuilder kpb = new KnapsackProblemBuilder();
		
		int numItems 			= 100;
		int numObj 				= 3;
		double rho 				= 0.0001;
		int idealFinderPopSize 	= 100;
		int numIdealFinderGen 	= 100;
		int asfDmId				= 1;
		
		KnapsackProblemInstance p = kpb.readFile(numItems, numObj);
		
		SelectionOperator so = new BinaryTournament( new NondominationComparator<Solution> (OptimizationType.MAXIMIZATION) );
		CrossoverOperator<VectorSolution<Integer>> co = new PermutationCrossover();
		MutationOperator<VectorSolution<Integer>> mo = new PermutationMutation();
		
		//Point idealPoint = SOOIdealPointFinder.findIdealPoint(p, numIdealFinderGen, idealFinderPopSize);
//		double ideal[] = {4248.0, 3989.0};
		double ideal[] = {4027.0, 4119.0, 3903.0};
		Point idealPoint = new Point(ideal);
		
		AsfDm asfDM = AsfDmBuilder.getAsfDm(asfDmId, numObj, idealPoint, rho);
		
		PSEABuilder<VectorSolution<Integer>> builder = new PSEABuilder<>(p, asfDM, new GeneticOperators<>(so, co, mo));
		
		
		builder.setMaxExploitGenerations(800);
		
		
		PSEA<VectorSolution<Integer>>alg = new PSEA<>(builder);
		
		ExperimentRunner.run(alg, p, asfDM, numIdealFinderGen, numObj);
		
		
	}
}
