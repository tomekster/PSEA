package experiment.knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.EA.GeneticOperators;
import algorithm.evolutionary.SingleObjectiveEA;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.AsfDmBuilder;
import algorithm.evolutionary.operators.CrossoverOperator;
import algorithm.evolutionary.operators.MutationOperator;
import algorithm.evolutionary.operators.SelectionOperator;
import algorithm.evolutionary.operators.impl.crossover.PermutationCrossover;
import algorithm.evolutionary.operators.impl.mutation.PermutationMutation;
import algorithm.evolutionary.operators.impl.selection.BinaryTournament;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.knapsack.KnapsackProblemBuilder;
import problems.knapsack.KnapsackProblemInstance;
import utils.comparators.NondominationComparator;
import utils.enums.OptimizationType;
import utils.math.structures.Point;

public class ExperimentSingleObjectiveKnapsack {
		public static void main(String args[]) {
		KnapsackProblemBuilder kpb = new KnapsackProblemBuilder();
		
		int numItems = 100;
		int numKnapsacks = 2;
		double rho = 0.0001;
		int popSize = 100;
		int numGenerations = 100;
		
		KnapsackProblemInstance kpi = kpb.readFile(numItems, numKnapsacks);
		Population <Solution> pf = kpi.getReferenceFront();
		double maxProfits[] = new double[numKnapsacks];
		for(Solution s : pf.getSolutions()){
			for(int i=0; i<s.getNumObjectives(); i++){
				maxProfits[i] = Math.max(s.getObjective(i), maxProfits[i]);
			}
		}
		Point ideal = new Point(maxProfits); 
		
		SelectionOperator so = new BinaryTournament( new NondominationComparator<Solution> (OptimizationType.MAXIMIZATION) );
		CrossoverOperator<VectorSolution<Integer>> co = new PermutationCrossover();
		MutationOperator<VectorSolution<Integer>> mo = new PermutationMutation();
		
		EA.GeneticOperators < VectorSolution<Integer> >go = new GeneticOperators<>(so, co, mo);
		
		AsfDm asfDM = AsfDmBuilder.getAsfDm(1, numKnapsacks, ideal, rho);
		
		SingleObjectiveEA< VectorSolution <Integer> > alg = new SingleObjectiveEA<>(kpi , popSize, go, asfDM);
		
		System.out.println(pf);
		
		Solution optimalSolution = kpi.getOptimalAsfDmSolution(asfDM);
		
		ArrayList <Double> bestAsf = new ArrayList<>();
		ArrayList <Double> currentAsf = new ArrayList<>();
		for(int i=0; i<numGenerations; i++) {
			alg.run(1);
			Population <VectorSolution<Integer> > pop = alg.getPopulation();
			for(Solution s : pop.getSolutions()) {
				currentAsf.add(asfDM.eval(s));
			}
			bestAsf.add(Collections.min(currentAsf));
			
			System.out.println(pop);
		}
		
		System.out.println("Best asf in each generation: " + Arrays.toString(bestAsf.toArray()));
		System.out.println("Optimal solution: " + Arrays.toString(optimalSolution.getObjectives()));
		System.out.println("Optimal solution asf: " + asfDM.eval(optimalSolution));
		
		
		System.out.println("Ideal point:" + Arrays.toString(ideal.getDim()));
		System.out.println("AsfLambda: " + Arrays.toString(asfDM.getAsfFunction().getLambda()) );
		
		double obj[] = {1,1};
		Solution s = new Solution(obj);
		System.out.println(asfDM.eval(s));
	}
}
