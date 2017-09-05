package experiment;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.nsgaiii.NSGAIII;
import algorithm.rankers.NonDominationRanker;
import experiment.metrics.IGD;
import problems.Problem;
import problems.dtlz.DTLZ1;
import problems.dtlz.DTLZ3;

public class NSGAIIIExperiment {
	
	private static final double DEFAULT_NUM_GENERATIONS = 1000;
	
	public static void main(String [] args){
		
		/* TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		 */
//		for(int k=0; k<5; k++){
			Problem problem = new DTLZ3(3);
//			double var[] = {0.8555320064628616, 0.09119105673440564, 0.49988207194654294, 0.4996186411813352, 0.5000401585967659, 0.4999231067023869, 0.4999485217881252};
//			double obj[] = {1,2,3};
//			Solution s = new Solution(var,obj);
//			System.out.println(s);
//			problem.evaluate(s);
//			System.out.println(s);
			Population target = problem.getReferenceFront();
			Population firstFront = null;
			NSGAIII nsgaiii = new NSGAIII(	problem, 
					new SBX(problem),
					new PolynomialMutation(problem),
					new BinaryTournament(new NonDominationRanker())
					);
			for(int i=0; i < DEFAULT_NUM_GENERATIONS; i++){
				nsgaiii.nextGeneration();
				problem.evaluate(nsgaiii.getPopulation());
				if(i%50 == 0){
					firstFront = NonDominationRanker.sortPopulation(nsgaiii.getPopulation()).get(0);
					System.out.println("First front IGD = " + IGD.execute(target, firstFront));
				}
			}
			int numObj = problem.getNumObjectives();
			ArrayList<ArrayList<double[]>> visData = new ArrayList<>();
			visData.add(PythonVisualizer.convert(problem.getReferenceFront()));
			visData.add(PythonVisualizer.convert(firstFront));					
			PythonVisualizer.visualise(numObj, visData);
//		}
	}
}