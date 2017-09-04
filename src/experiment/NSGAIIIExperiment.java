package experiment;

import java.io.IOException;
import java.io.PrintWriter;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import algorithm.geneticAlgorithm.operators.impl.selection.BinaryTournament;
import algorithm.nsgaiii.NSGAIII;
import algorithm.nsgaiii.ReferencePoint;
import algorithm.rankers.NonDominationRanker;
import experiment.metrics.IGD;
import problems.Problem;
import problems.dtlz.DTLZ1;

public class NSGAIIIExperiment {
	
	private static final double DEFAULT_NUM_GENERATIONS = 2000;
	
	public static void main(String [] args){
		
		/* TODO - WFG1 - something goes wrong here - obtained front looks weird, WFG8 - difficult problem
		 */
		Problem problem = new DTLZ1(3);
		NSGAIII nsgaiii = new NSGAIII(	problem, 
				new SBX(problem),
				new PolynomialMutation(problem),
				new BinaryTournament(new NonDominationRanker())
				);
		for(int i=0; i < DEFAULT_NUM_GENERATIONS; i++){
			nsgaiii.nextGeneration();
		}
		Population finalPop = nsgaiii.getPopulation();
		Population firstFront = NonDominationRanker.sortPopulation(finalPop).get(0);
		
		String tmpFilename = "tmpVizData.txt";
		writeVisualizatoinData(tmpFilename, problem.getReferenceFront(), firstFront);
		
		PythonVisualizer pv = new PythonVisualizer();
		pv.visualise(tmpFilename);
		
		double igd = IGD.execute(problem.getReferenceFront(), firstFront);
		System.out.println("First front IGD = " + igd);
		igd = IGD.execute(problem.getReferenceFront(), finalPop);
		System.out.println("Whole pop IGD = " + igd);
		
		Population target = new Population();
		for(ReferencePoint rp : nsgaiii.getHyperplane().getReferencePoints()){
			target.addSolution(new Solution(rp.getDim(), rp.getDim()));
		}
		igd = IGD.execute(target, finalPop);
		System.out.println("ReferencePoint IGD = " + igd);
	}

	private static void writeVisualizatoinData(String filename, Population referenceFront, Population firstFront) {
		try{
		    PrintWriter writer = new PrintWriter(filename, "UTF-8");
		    writer.println(referenceFront.getSolution(0).getNumObjectives());
		    writer.println(referenceFront.size());
		    writer.println(firstFront.size());
		    
		    for(int i = 0 ; i<referenceFront.size(); i++){
				Solution s = referenceFront.getSolution(i);
				for(double d : s.getObjectives()){
					writer.print(" " + d);
				}
				writer.println("");
			}
		    for(int i=0 ; i<firstFront.size(); i++){
				Solution s = firstFront.getSolution(i);
				for(double d : s.getObjectives()){
					writer.print(" " + d);
				}
				writer.println("");
			}
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
	}
}