package experiment;

import java.util.ArrayList;
import java.util.Arrays;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.SingleObjectiveEA;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.RferencePointDm;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.psea.PSEA;
import problems.KnowsOptimalAsfSolution;
import problems.Problem;
import utils.enums.PSEAphase;

public class ExperimentRunner {

	public static <S extends Solution> void  run (EA <S> alg, Problem <S> p, RferencePointDm simulatedDm, int numGen, int numObjectives){
		Solution optimalSolution = new Solution(new double[0]);
		if(p instanceof KnowsOptimalAsfSolution && simulatedDm instanceof AsfDm){
			optimalSolution = ((KnowsOptimalAsfSolution) p).getOptimalAsfDmSolution( (AsfDm) simulatedDm);
		}
		double optVal [] = {0.25, 0.25};
		optimalSolution = new Solution(optVal);//TODO - temporary for DTLZ1
		
		
		ArrayList <Double> bestAsf = new ArrayList<>();
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
			bestAsf.add(simulatedDm.eval(bestSol));
		}
		
		printResults(p, simulatedDm, numGen, alg, optimalSolution, bestAsf, bestSols);
	}
	
	public static <S extends Solution> void printResults(Problem <S> p, RferencePointDm simulatedDm, int numGen, EA <S> alg, Solution optimalSolution, ArrayList <Double> bestAsf, ArrayList<Solution> bestSols ){
		System.out.println("Problem: " + p.getName());
		System.out.println("NumObjectives: " + p.getNumObjectives());
		
		System.out.println();
		
		System.out.println("Ideal point used:" + Arrays.toString(simulatedDm.getReferencePoint().getDim()));
		
//		System.out.println("asfDMLambda: " + Arrays.toString(simulatedDm.getAsfFunction().getLambda()) );
		System.out.println("NumberGenerations: " + alg.getGenerationNum());
		System.out.println("Population size: " + alg.getPopSize());
		
		System.out.println();
		
		System.out.println("Optimal solution asf: " + simulatedDm.eval(optimalSolution));
		System.out.println("Best asf in first generation: " + bestAsf.get(0));
		System.out.println("Best asf in last generation: " + bestAsf.get(bestAsf.size() - 1));
		
		System.out.println();
		
		System.out.println("Optimal solution: " + Arrays.toString(optimalSolution.getObjectives()));
		System.out.println("Best solution in first generation: " + Arrays.toString(bestSols.get(0).getObjectives()) );
		System.out.println("Best solution in last generation: " + Arrays.toString(bestSols.get(bestSols.size() - 1).getObjectives()) );
		
		System.out.println("All asf vals: " + Arrays.toString(bestAsf.toArray()));
	}
}
