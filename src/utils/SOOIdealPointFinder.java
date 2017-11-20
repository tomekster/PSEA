package utils;

import algorithm.evolutionary.SingleObjectiveEA;
import algorithm.evolutionary.operators.impl.crossover.SBX;
import algorithm.evolutionary.operators.impl.mutation.PolynomialMutation;
import algorithm.evolutionary.operators.impl.selection.BinaryTournament;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import problems.ContinousProblem;
import problems.Problem;

public class SOOIdealPointFinder {
	
	public static double[] findIdealPoint(ContinousProblem problem){
		SelectionOperator so = new BinaryTournament();
		CrossoverOperator <VectorSolution<Double>> co = new SBX(1.0, 30.0, problem.getLowerBounds(), problem.getUpperBounds());
		MutationOperator <VectorSolution<Double>> mo = new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBounds(), problem.getUpperBounds());
		return findIdealPoint(problem, so, co , mo);
	}
	
	public static <S extends Solution> double[] findIdealPoint(Problem <S> p, SelectionOperator so, CrossoverOperator <S> co, MutationOperator <S> mo){
		double idealPoint[] = new double[p.getNumObjectives()];
		
		for(int i=0; i<idealPoint.length; i++){
			idealPoint[i] = Double.MAX_VALUE;
		}
			
		for(int optimizedDim=0; optimizedDim < p.getNumObjectives(); optimizedDim++){
			SingleObjectiveEA so = new SingleObjectiveEA(	
				this,
				
				co,
				mo,
				soDM
			);
			
			so.run();
			
			//Workaround for inner class error
		    final int dummyOptimizedDim = optimizedDim;
			idealPoint[optimizedDim] = so.getPopulation().getSolutions().stream().mapToDouble(s -> s.getObjective(dummyOptimizedDim)).min().getAsDouble();
		}
		this.idealPoint = idealPoint;
		return idealPoint;
	}
}
