package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
		try {
			alg = new RST_NSGAIII((Problem) problemConstructor.newInstance(params.getNumberObjectives()), params.getNumberGenerations(), params.getElicitationInterval(), ChebyshevRankerBuilder.getCentralChebyshevRanker(params.getNumberObjectives()));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		alg.run();
//		alg.run();
	}
	
}

	