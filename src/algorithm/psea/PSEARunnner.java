package algorithm.psea;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import algorithm.geneticAlgorithm.operators.impl.crossover.SBX;
import algorithm.geneticAlgorithm.operators.impl.mutation.PolynomialMutation;
import artificialDM.ADMBuilder;
import artificialDM.AsfDM;
import experiment.Evaluator;
import experiment.ExecutionHistory;
import problems.ContinousProblem;
import problems.Problem;

public class PSEARunnner {
	public static void runPSEA() {
		ExecutionHistory.getInstance().clear();
		PSEAParameters params = PSEAParameters.getInstance();
		Constructor<? extends Problem> problemConstructor = null;
		try {
			Class c = null;
			String problemName = params.getProblemName();
			System.out.println(problemName);
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
			problem = (ContinousProblem) problemConstructor.newInstance(params.getNumberObjectives());
			cr = ADMBuilder.getAsfDm(7, params.getNumberObjectives(), null);
			alg = new PSEA(
					problem,
					cr,
					new SBX(1.0, 30.0, ((ContinousProblem)problem).getLowerBounds(), ((ContinousProblem)problem).getUpperBounds()),
					new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, ((ContinousProblem)problem).getLowerBounds(), ((ContinousProblem)problem).getUpperBounds())
				);
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