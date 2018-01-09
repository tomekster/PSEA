package experiment;

import java.util.ArrayList;
import java.util.Arrays;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.SingleObjectiveEA;
import algorithm.evolutionary.interactive.artificialDM.ReferencePointDm;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.psea.PSEA;
import problems.KnowsOptimalAsfSolution;
import problems.Problem;
import utils.enums.PSEAphase;

public class ExperimentRunner {

	public static <S extends Solution> void  run (EA <S> alg, Problem <S> p, ReferencePointDm simulatedDm, int numGen, int numObjectives){
		Solution optimalSolution = new Solution(new double[0]);
		if(p instanceof KnowsOptimalAsfSolution){
			optimalSolution = ((KnowsOptimalAsfSolution) p).getOptimalSolution(simulatedDm);
		}
		
		ArrayList <Double> bestVal = new ArrayList<>();
		ArrayList <Solution> bestSols = new ArrayList<>();
		for(int i=0; i<numGen; i++) {
			if(alg instanceof SingleObjectiveEA<?>){
				((SingleObjectiveEA<?>) alg).run(1);
			}
			else if(alg instanceof PSEA<?>){
				PSEA <S> psea = (PSEA<S>) alg;
				psea.run();
				if(psea.getPseaPhase() != PSEAphase.REACHED_MAX_GENERATIONS){
					i=0;
				}
				else{
					numGen = Integer.MIN_VALUE;
				}
			}
			Population <S> pop = alg.getPopulation();
			p.evaluate(pop);
			Solution bestSol = simulatedDm.getBestSolutionVal(pop); 
			bestSols.add(bestSol);
			bestVal.add(simulatedDm.eval(bestSol));
		}
		
		printResults(p, simulatedDm, numGen, alg, optimalSolution, bestVal, bestSols);
	}
	
	public static <S extends Solution> void printResults(Problem <S> p, ReferencePointDm simulatedDm, int numGen, EA <S> alg, Solution optimalSolution, ArrayList <Double> bestVal, ArrayList<Solution> bestSols ){
		System.out.println("Problem: " + p.getName());
		System.out.println("NumObjectives: " + p.getNumObjectives());
		
		System.out.println();
		
		System.out.println("Ideal point used:" + Arrays.toString(simulatedDm.getReferencePoint().getDim()));
		
		System.out.println("DM description: " + simulatedDm);
		System.out.println("NumberGenerations: " + alg.getGenerationNum());
		System.out.println("Population size: " + alg.getPopSize());
		
		System.out.println();
		
		System.out.println("Optimal solution evaluation: " + simulatedDm.eval(optimalSolution));
		System.out.println("Best solution value in first generation: " + bestVal.get(0));
		System.out.println("Best solution value in last generation : " + bestVal.get(bestVal.size() - 1));
		
		System.out.println();
		
		System.out.println("Optimal solution: " + Arrays.toString(optimalSolution.getObjectives()));
		System.out.println("Best solution in first generation: " + Arrays.toString(bestSols.get(0).getObjectives()) );
		System.out.println("Best solution in last generation: " + Arrays.toString(bestSols.get(bestSols.size() - 1).getObjectives()) );
		
		System.out.println("DM's evaluations for every generation: " + Arrays.toString(bestVal.toArray()));
	}
}
