package core;

import org.junit.Test;

import core.points.Solution;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import problems.TrivialProblem;
import solutionRankers.NonDominationRanker;

public class NSGAIIITest {

	@Test
	public void selectNewPopulation(){
		Problem problem = new TrivialProblem();
		NSGAIII alg = new NSGAIII(	problem, 
				new BinaryTournament(new NonDominationRanker()),
				new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound()),
				new PolynomialMutation(1.0 / problem.getNumObjectives(), 20.0, problem.getLowerBound(), problem.getUpperBound()));
		Population pop = new Population();
		
		double objVar[][][] = 	{ 	{{1,5}, {1,5}},
										{{1,6}, {1,6}}, 
										{{1,7}, {1,7}},
										{{1,8}, {1,8}},
										{{4,8}, {4,8}},
										{{4,9}, {4,9}},
										{{5,1}, {5,1}},
										{{4,3}, {4,3}}
									};
		for(int i=0; i<8; i++){
			pop.addSolution(new Solution(objVar[i][0], objVar[i][1]));
		}
		
		Population res = alg.selectNewPopulation(pop);
//		for(int i=1; i<9; i++){
//			assertEquals(5, res.getSolution(0).getVariable(0), Geometry.EPS);
//			assertEquals(1, res.getSolution(0).getVariable(1), Geometry.EPS);
//			assertEquals(4, res.getSolution(1).getVariable(0), Geometry.EPS);
//			assertEquals(3, res.getSolution(1).getVariable(1), Geometry.EPS);
//			assertEquals(4, res.getSolution(2).getVariable(0), Geometry.EPS);
//			assertEquals(8, res.getSolution(2).getVariable(1), Geometry.EPS);
//			assertEquals(4, res.getSolution(3).getVariable(0), Geometry.EPS);
//			assertEquals(9, res.getSolution(3).getVariable(1), Geometry.EPS);
//		}
	}
}
