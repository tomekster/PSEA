package experiment.knapsack;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.SingleObjectiveEA;
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
import experiment.ExperimentRunner;
import problems.knapsack.KnapsackProblemBuilder;
import problems.knapsack.KnapsackProblemInstance;
import utils.comparators.NondominationComparator;
import utils.math.structures.Point;

public class ExperimentSingleObjectiveKnapsack {
	public static void main(String args[]) {
		
		int numItems									= 100;
		int numObj										= 3;
		double rho 										= 0.0001;
		int popSize 									= 152;
		int numGen 										= 800;
		int decidentId									= 1;
		
		KnapsackProblemBuilder kpb			 			= new KnapsackProblemBuilder();
		KnapsackProblemInstance p 						= kpb.readFile(numItems, numObj);
		SelectionOperator so 							= new BinaryTournament( new NondominationComparator<Solution> (p.getOptimizationType()) );
		CrossoverOperator<VectorSolution<Integer>> co 	= new PermutationCrossover();
		MutationOperator<VectorSolution<Integer>> mo 	= new PermutationMutation();
		
		double maxProfits[] = new double[numObj];
		for(Solution s : p.getReferenceFront().getSolutions()){
			for(int i=0; i<s.getNumObjectives(); i++){
				maxProfits[i] = Math.max(s.getObjective(i), maxProfits[i]);
			}
		}
		double ideal[] = {4027.0, 4119.0, 3903.0};
		Point idealPoint = new Point(ideal);
		AsfDm asfDM = AsfDmBuilder.getAsfDm(decidentId, numObj,  idealPoint, rho);
		//AsfDm asfDM = AsfDmBuilder.getAsfDm(decidentId, numObj,  new Point(maxProfits), rho);
		
		SingleObjectiveEA<VectorSolution<Integer>> alg = new SingleObjectiveEA<>(p, popSize, new EA.GeneticOperators<>(so, co, mo), asfDM);
		
		ExperimentRunner.run(alg, p, asfDM, numGen, numObj);
	}
}
