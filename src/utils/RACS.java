package utils;

import java.util.ArrayList;

import core.Population;
import core.Solution;
import core.hyperplane.ReferencePoint;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import preferences.Comparison;
import preferences.PreferenceCollector;

public class RACS {

	/**
	 * Two step procedure: 1) Find ReferencePoints which directions correspond
	 * to Chebyshev Scalarizing Functions coherent with user's preferences 2)
	 * Solve multiple LP's to determine for every solution whether there exists
	 * Chebyshev Scalarizing Function which evaluates this solution to dominate
	 * res of solutions
	 * 
	 * @param population
	 * @param referencePoints
	 * @param pc
	 * @return
	 */
	// public static ArrayList<Population> execute(Population population,
	// ArrayList<ReferencePoint> referencePoints,
	// PreferenceCollector pc) {
	// ArrayList<ReferencePoint> coherentReferencePoints =
	// findCoherentDirections(referencePoints, population, pc);
	// ArrayList<Population> fronts =
	// getDominationFronts(coherentReferencePoints, population, pc);
	//
	// System.out.println("#All directions = " + referencePoints.size());
	// System.out.println("#CoherentDirections = " +
	// coherentReferencePoints.size());
	// System.out.println("#Fronts = " + fronts.size());
	// for (int i = 0; i < fronts.size(); i++) {
	// Population p = fronts.get(i);
	// System.out.println("\t Front i size = " + p.size());
	// }
	//
	// return fronts;
	// }

	/**
	 * 
	 * @param referencePoints
	 * @param pc
	 * @return boolean[] where value at position i indicates whether
	 *         ReferencePoint i is coherent with user's preferences or not
	 */
	public static void execute(ArrayList<ReferencePoint> referencePoints,
			PreferenceCollector pc) {
		for (ReferencePoint rp : referencePoints) {
			rp.setCoherent(isCoherent(rp, pc));
		}
	}

	/**
	 * 
	 * @param referencePoints
	 * @param pc
	 * @return Array of Reference Points which are coherent with user's
	 *         preferences contained in PreferenceCollector
	 */
	private static ArrayList<ReferencePoint> findCoherentDirections(ArrayList<ReferencePoint> referencePoints, PreferenceCollector pc) {
		ArrayList<ReferencePoint> coherentReferencePoints = new ArrayList<ReferencePoint>();
		for (ReferencePoint rp : referencePoints) {
			if (isCoherent(rp, pc)) {
				coherentReferencePoints.add(rp);
			}
		}
		return coherentReferencePoints;
	}

	private static boolean isCoherent(ReferencePoint rp, PreferenceCollector pc) {
		double lambda[] = getDirection(rp);
		double eps = RATSLP(lambda, pc);
		return eps > 0;
	}

	/**
	 * 
	 * @param rp
	 * @return Extracts direction lambda from ReferencePoint rp
	 */
	private static double[] getDirection(ReferencePoint rp) {
		double lambda[] = new double[rp.getNumDimensions()];
		for (int i = 0; i < rp.getNumDimensions(); i++) {
			lambda[i] = 1 / rp.getDim(i);
		}
		return lambda;
	}

	/**
	 * 
	 * @param coherentReferencePoints
	 * @param pop
	 * @param pc
	 * @return Returns ArrayList of Population representing domination fronts.
	 *         Solution s is qualified to first front if there exists at least
	 *         one direction lambda associated with coherent ReferencePoint with
	 *         corresponding Chebyshev Scalarizing Function which evaluates
	 *         solution s as better than any other solution in population.
	 */
	private static ArrayList<Population> getDominationFronts(ArrayList<ReferencePoint> coherentReferencePoints,
			Population pop, PreferenceCollector pc) {
		ArrayList<Population> fronts = new ArrayList<Population>();
		ArrayList<Solution> front = new ArrayList<Solution>();
		double lambda[];
		boolean addedToFront = true;

		boolean included[] = new boolean[pop.size()];
		for (int i = 0; i < pop.size(); i++) {
			included[i] = false;
		}

		while (addedToFront) {
			front = new ArrayList<Solution>();
			for (int i = 0; i < pop.size(); i++) {
				if (included[i]) {
					continue;
				}
				for (ReferencePoint rp : coherentReferencePoints) {
					lambda = getDirection(rp);
					double eps = RATSDominationLP(lambda, pc, pop, i);
					if (eps > 0) {
						front.add(pop.getSolution(i));
						included[i] = true;
					}
				}
			}
			if (front.isEmpty()) {
				addedToFront = false;
				for (int i = 0; i < pop.size(); i++) {
					if (!included[i]) {
						front.add(pop.getSolution(i));
					}
				}
			}
			Population frontPop = new Population();
			for (Solution s : front) {
				frontPop.addSolution(s);
			}
			fronts.add(frontPop);
		}
		return fronts;
	}

	/**
	 * 
	 * @param lambda
	 * @param pc
	 * @param pop
	 * @param solutionXId
	 * @return Solve LP which compare given solution with whole population
	 *         assuming Chebyshev Scalarizing function corresponding to given
	 *         direction lambda
	 */
	public static double RATSLP(double[] lambda, PreferenceCollector pc) {
		double epsVal = -Double.MAX_VALUE;
		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.BooleanParam.PreInd, false);
			cplex.setOut(null);
			IloNumVar eps = cplex.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
			IloNumVar rho = cplex.numVar(0, 1000000);
			cplex.addMaximize(eps);
			for (Comparison cmp : pc.getComparisons()) {
				Solution a = cmp.getBetter();
				Solution b = cmp.getWorse();

				// System.out.println(a + " >> " + b );
				// System.out.println("eps + " + "rho*" +
				// Geometry.dot(a.getObjectives(), lambda) + " + " +
				// getMax(a,lambda) + " <= ");
				// System.out.println("rho*" + Geometry.dot(b.getObjectives(),
				// lambda) + " + " + getMax(b,lambda) );
				
				cplex.addLe(
						cplex.sum(cplex.prod(1.0, eps), cplex.prod(rho, Geometry.dot(a.getObjectives(), lambda)),
								cplex.constant(getMax(a, lambda))),
						cplex.sum(cplex.prod(rho, Geometry.dot(b.getObjectives(), lambda)),
								cplex.constant(getMax(b, lambda))));
			}
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

				epsVal = cplex.getValue(eps);
				double rhoVal = cplex.getValue(rho);
				cplex.output().println("EpsValue = " + epsVal);
				cplex.output().println("RhoValue = " + rhoVal);
			}
			cplex.end();
		} catch (IloException e) {
			System.out.println("Concert exception cought: " + e);
		}
		return epsVal;
	}
	
	/**
	 * 
	 * @param lambda
	 * @param pc
	 * @param pop
	 * @param solutionXId
	 * @return Solve LP which compare given solution with whole population
	 *         assuming Chebyshev Scalarizing function corresponding to given
	 *         direction lambda
	 */
	public static double RATSDominationLP(double[] lambda, PreferenceCollector pc, Population pop, int solutionXId) {
		double epsVal = -Double.MAX_VALUE;
		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.BooleanParam.PreInd, false);
			cplex.setOut(null);
			IloNumVar eps = cplex.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
			IloNumVar rho = cplex.numVar(0, 1000000);

			cplex.addMaximize(eps);
			for (Comparison cmp : pc.getComparisons()) {
				Solution a = cmp.getBetter();
				Solution b = cmp.getWorse();

				// System.out.println(a + " >> " + b );
				// System.out.println("eps + " + "rho*" +
				// Geometry.dot(a.getObjectives(), lambda) + " + " +
				// getMax(a,lambda) + " <= ");
				// System.out.println("rho*" + Geometry.dot(b.getObjectives(),
				// lambda) + " + " + getMax(b,lambda) );

				cplex.addLe(
						cplex.sum(cplex.prod(1.0, eps), cplex.prod(rho, Geometry.dot(a.getObjectives(), lambda)),
								cplex.constant(getMax(a, lambda))),
						cplex.sum(cplex.prod(rho, Geometry.dot(b.getObjectives(), lambda)),
								cplex.constant(getMax(b, lambda))));
			}

			if (solutionXId >= 0) {
				for (int i = 0; i < pop.getSolutions().size(); i++) {
					if (i == solutionXId) {
						continue;
					}
					Solution x = pop.getSolution(solutionXId);
					Solution y = pop.getSolution(i);
					cplex.addLe(
							cplex.sum(cplex.prod(1.0, eps), cplex.prod(rho, Geometry.dot(x.getObjectives(), lambda)),
									cplex.constant(getMax(x, lambda))),
							cplex.sum(cplex.prod(rho, Geometry.dot(y.getObjectives(), lambda)),
									cplex.constant(getMax(y, lambda))));
				}
			}

			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

				epsVal = cplex.getValue(eps);
				double rhoVal = cplex.getValue(rho);
				cplex.output().println("EpsValue = " + epsVal);
				cplex.output().println("RhoValue = " + rhoVal);
			}
			cplex.end();
		} catch (IloException e) {
			System.out.println("Concert exception cought: " + e);
		}
		return epsVal;
	}

	private static double getMax(Solution a, double[] lambda) {
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < a.getNumObjectives(); i++) {
			max = Double.max(max, lambda[i] * a.getObjective(i));
		}
		return max;
	}
}
