package utils;

import javax.management.RuntimeErrorException;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.SingleObjectiveEA;
import algorithm.evolutionary.operators.CrossoverOperator;
import algorithm.evolutionary.operators.MutationOperator;
import algorithm.evolutionary.operators.SelectionOperator;
import algorithm.evolutionary.operators.impl.crossover.PermutationCrossover;
import algorithm.evolutionary.operators.impl.crossover.SBX;
import algorithm.evolutionary.operators.impl.mutation.PermutationMutation;
import algorithm.evolutionary.operators.impl.mutation.PolynomialMutation;
import algorithm.evolutionary.operators.impl.selection.BinaryTournament;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import problems.PermutationProblem;
import problems.Problem;
import problems.knapsack.KnapsackProblemInstance;
import utils.comparators.NondominationComparator;
import utils.comparators.SingleObjectiveComparator;
import utils.enums.OptimizationType;
import utils.math.structures.Point;

public class SOOIdealPointFinder {
	
	public static Point findIdealPoint(Problem <? extends Solution> problem, int numGen, int popSize){
		SelectionOperator so = new BinaryTournament(new NondominationComparator<>(problem.getOptimizationType()));
		if(problem instanceof ContinousProblem) {
			ContinousProblem cp = (ContinousProblem) problem;
			CrossoverOperator <VectorSolution<Double>> co = new SBX(1.0, 30.0, cp.getLowerBounds(), cp.getUpperBounds());
			MutationOperator <VectorSolution<Double>> mo = new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, cp.getLowerBounds(), cp.getUpperBounds());
			return findIdealPoint(cp, popSize, new EA.GeneticOperators<VectorSolution<Double>>(so, co, mo), numGen);
		}
		else if(problem instanceof KnapsackProblemInstance) {
			PermutationProblem pp = (PermutationProblem) problem;
			CrossoverOperator <VectorSolution<Integer>> co = new PermutationCrossover();
			MutationOperator <VectorSolution<Integer>> mo = new PermutationMutation();
			return findIdealPoint(pp, popSize, new EA.GeneticOperators<VectorSolution<Integer>> (so, co, mo), numGen);
		}
		else {
			throw new RuntimeErrorException(new Error(), "Unknow problem type!");
		}
	}
	
	public static <S extends Solution> Point findIdealPoint(Problem <S> p, int popSize, EA.GeneticOperators <S> go, int numGen){
		double idealPoint[] = new double[p.getNumObjectives()];
		
		for(int i=0; i<idealPoint.length; i++){
			idealPoint[i] = p.getOptimizationType() == OptimizationType.MINIMIZATION ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		}
			
		for(int optObj=0; optObj < p.getNumObjectives(); optObj++){
			SingleObjectiveEA <S> alg= new SingleObjectiveEA <>(p, popSize, go, new SingleObjectiveComparator(optObj, p.getOptimizationType()));
			alg.run(numGen);
			
			if(p.getOptimizationType() == OptimizationType.MINIMIZATION){
				idealPoint[optObj] = alg.getPopulation().minObjectiveVal(optObj);
			}
			else if(p.getOptimizationType() == OptimizationType.MAXIMIZATION){
				idealPoint[optObj] = alg.getPopulation().maxObjectiveVal(optObj);
			}
		}
		return new Point(idealPoint);
	}
}
