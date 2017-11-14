package utils;

import algorithm.geneticAlgorithm.SingleObjectiveEA;
import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.operators.MutationOperator;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.geneticAlgorithm.solutions.Solution;
import algorithm.geneticAlgorithm.solutions.VectorSolution;
import problems.ContinousProblem;
import problems.Problem;

public class SOOIdealPointFinder {
	
	public static double[] findIdealPoint(ContinousProblem problem){
		CrossoverOperator <VectorSolution<Double>> co = new SBX(1.0, 30.0, problem.getLowerBounds(), problem.getUpperBounds());
		MutationOperator <VectorSolution<Double>> mo = new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBounds(), problem.getUpperBounds());
		return findIdealPoint(problem, co , mo);
	}
	
	public static <S extends Solution> double[] findIdealPoint(Problem <S> p, CrossoverOperator <S> co, MutationOperator <S> mo){
		double idealPoint[] = new double[p.getNumObjectives()];
		
		for(int i=0; i<idealPoint.length; i++){
			idealPoint[i] = Double.MAX_VALUE;
		}
			
		for(int optimizedDim=0; optimizedDim < p.getNumObjectives(); optimizedDim++){
			SingleObjectiveDM soDM = new SingleObjectiveDM(optimizedDim);
			SingleObjectiveEA so = new SingleObjectiveEA(	
				this,
				new BinaryTournament(soDM),
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
