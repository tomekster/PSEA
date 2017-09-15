package algorithm.psea;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import artificialDM.AsfDM;
import artificialDM.ADMBuilder;
import experiment.Evaluator;
import experiment.ExecutionHistory;
import problems.Problem;

public class PSEARunnner {
	public static void runPSEA() {
		ExecutionHistory.getInstance().clear();
		PSEAParameters params = PSEAParameters.getInstance();
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
		
		PSEA alg = null;
		Problem problem = null;
		AsfDM cr = null;
		try {
			problem = (Problem) problemConstructor.newInstance(params.getNumberObjectives());
			cr = ADMBuilder.getAsfDm(7, params.getNumberObjectives(), null);
			alg = new PSEA(problem,cr);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e1) {
			e1.printStackTrace();
		}
		alg.run();
		
		Evaluator.evaluateAsfDMRun(problem, cr, alg.getPopulation(), alg.getDMmodel().getAsfBundle());
		ExecutionHistory history = ExecutionHistory.getInstance();
		System.out.println("Generation min: " + history.getFinalMinDist());
		System.out.println("Generation avg: " + history.getFinalAvgDist());
	}
}