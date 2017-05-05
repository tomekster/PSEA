package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import history.ExecutionHistory;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;

public class NSGAIIIRunnner {
	
	public static void runNSGAIII() {
		NSGAIIIParameters params = NSGAIIIParameters.getInstance();
		Constructor problemConstructor = null;
		try {
			Class c = null;
			String problemName = params.getProblemName();
			if(problemName.contains("DTLZ")) {
				c = Class.forName("problems.dtlz." + problemName);
			} else if(problemName.contains("WFG")) {
				c = Class.forName("problems.wfg." + problemName);
			}
			problemConstructor = c.getConstructor(Integer.class);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		
		RST_NSGAIII alg = null;
		Problem problem = null;
		ChebyshevRanker cr = null;
		try {
			problem = (Problem) problemConstructor.newInstance(params.getNumberObjectives());
			cr = ChebyshevRankerBuilder.getMinXZChebyshevRanker(params.getNumberObjectives());
			alg = new RST_NSGAIII(problem, params.getNumberExplorationGenerations(), params.getNumberExploitationGenerations(), params.getNumElicitations1(), params.getNumElicitations2(), params.getElicitationInterval(), cr, params.getNumLambdas(), params.getSpreadThreshold());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		alg.run();
		
		Evaluator.evaluateRun(problem, cr, alg.getPopulation());
		ExecutionHistory history = ExecutionHistory.getInstance();
		System.out.println("Generation min: " + history.getFinalMinDist());
		System.out.println("Generation avg: " + history.getFinalAvgDist());
	}
	
}

	