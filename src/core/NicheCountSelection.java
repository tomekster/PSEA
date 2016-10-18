package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import javax.swing.plaf.synth.SynthSeparatorUI;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.points.ReferencePoint;
import core.points.Solution;
import utils.DegeneratedMatrixException;
import utils.GaussianElimination;
import utils.Geometry;

/***
 * Class encapsulates "selectKPoints" method from NSGA-III algorithm 
 */
public class NicheCountSelection {

	private final static Logger LOGGER = Logger.getLogger(NicheCountSelection.class.getName());

	public static Population selectKPoints(int numObjectives, Population allFronts, Population allButLastFront, Population lastFront, 
			int k, Hyperplane hyperplane) throws DegeneratedMatrixException {
		Population normalizedPopulation = normalize(numObjectives, allFronts);
		associate(normalizedPopulation, hyperplane);
		Population kPoints = niching(allButLastFront, lastFront, k, hyperplane);
		return kPoints;
	}

	public static Population normalize(int numObjectives, Population allFronts) {
		Population resPop = allFronts.copy();
		double z_min[] = new double[numObjectives];
		for (int j = 0; j < numObjectives; j++) {
			double min = Double.MAX_VALUE;
			for (Solution s : resPop.getSolutions()) {
				min = Double.min(s.getObjective(j), min);
			}
			z_min[j] = min;
		}

		// Move all points to new origin
		for (Solution s : resPop.getSolutions()) {
			for (int j = 0; j < numObjectives; j++) {
				s.setObjective(j, s.getObjective(j) - z_min[j]);
			}
		}

		Population extremePoints = computeExtremePoints(resPop, numObjectives);

		double invertedIntercepts[] = new double[extremePoints.size()];

		try {
			invertedIntercepts = findIntercepts(extremePoints);
		} catch (DegeneratedMatrixException e) {
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstObjectives[] = findWorstObjectives(numObjectives, allFronts);
				invertedIntercepts[i] = 1.0 / worstObjectives[i];
			}
			e.printStackTrace();
		}
		if(invertedIntercepts == null){
			invertedIntercepts = new double[extremePoints.size()];
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstObjectives[] = findWorstObjectives(numObjectives, allFronts);
				invertedIntercepts[i] = 1.0 / worstObjectives[i];
			}
		}
		
		for (Solution s : resPop.getSolutions()) {
			for (int i = 0; i < numObjectives; i++) {
				/**
				 * Multiplication instead of division - explained in
				 * findIntercepts()
				 */
				s.setObjective(i, s.getObjective(i) * invertedIntercepts[i]);
			}
		}

		return resPop;
	}

	private static double[] findWorstObjectives(int numObjectives, Population allFronts) {
		double res[] = new double[numObjectives];
		for(Solution s : allFronts.getSolutions()){
			for(int i=0; i<numObjectives; i++){
				res[i] = Double.max(res[i], s.getObjective(i));
			}
		}
		return res;
	}

	private static double[] findIntercepts(Population extremePoints) throws DegeneratedMatrixException {

		int n = extremePoints.size();
		double coef[] = null;

		boolean duplicate = false;
		for (int i = 0; !duplicate && i < n; i++) {
			for (int j = i + 1; !duplicate && j < n; j++) {
				duplicate = extremePoints.getSolution(i) == extremePoints.getSolution(j);
			}
		}

		if (!duplicate) {
			coef = new double[n];

			double a[][] = new double[n][n];
			double b[] = new double[n];
			for (int i = 0; i < n; i++) {
				b[i] = 1.0;
				for (int j = 0; j < n; j++) {
					a[i][j] = extremePoints.getSolution(i).getObjective(j);
				}
			}

			coef = GaussianElimination.execute(a, b);

			for (int i = 0; i < n; i++) {
				if (coef[i] < 0){
					return null;
				}
			}

			/**
			 * Loop beneath was commented, because since b[i] = 1 for all i and
			 * just after returning from this method we divide each solutions
			 * objective value by corresponding intercept value it is better to
			 * return inversed intercept values (by omitting division by b[i]),
			 * and multiply objective value instead of dividing it.
			 */

			/*
			 * for(int i = 0; i < n; i++){ coef[i] /= b[i]; }
			 */
		}

		return coef;
	}
	
	public static void associate(Population population, Hyperplane hyperplane) {
		hyperplane.resetAssociations();
		ArrayList<ReferencePoint> refPoints = hyperplane.getReferencePoints();

		for(int i=0; i<population.getSolutions().size(); i++){
			Solution s = population.getSolutions().get(i);
			double minDist = Double.MAX_VALUE;
			ReferencePoint bestRefPoint = null;
			for (int j = 0; j < refPoints.size(); j++) {
				ReferencePoint curRefPoint = refPoints.get(j);
				double dist = Geometry.pointLineDist(s.getObjectives(), curRefPoint.getDim());
				if (dist < minDist) {
					minDist = dist;
					bestRefPoint = curRefPoint;
				}
			}
			bestRefPoint.addAssociation(new Association(s, minDist));
		}
	}

	private static Population niching(Population allButLastFront, Population lastFront, int K, Hyperplane hyperplane) {
		Population kPoints = new Population();
		HashMap<double[], Boolean> isLastFront = new HashMap<>();
		for (Solution s : allButLastFront.getSolutions()) {
			isLastFront.put(s.getVariables(), false);
		}
		for (Solution s : lastFront.getSolutions()) {
			isLastFront.put(s.getVariables(), true);
		}
		
		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(new Comparator <ReferencePoint>() {
			@Override
			public int compare(ReferencePoint o1, ReferencePoint o2) {
				return Double.compare(o1.getNicheCount(), o2.getNicheCount());
			}
		});
		
		for (ReferencePoint rp : hyperplane.getReferencePoints()) {
			refPQ.add(rp);
		}

		while (kPoints.size() < K) {
			ReferencePoint smallestNicheCountRefPoint = refPQ.poll();
			PriorityQueue<Association> associatedSolutionsQueue = smallestNicheCountRefPoint
					.getAssociatedSolutionsQueue();
			while (!associatedSolutionsQueue.isEmpty()) {
				Solution s = associatedSolutionsQueue.poll().getSolution();
//				System.out.println("ALL BUT LAST: ");
//				for(Solution x : allButLastFront.getSolutions()){
//					System.out.println(x.hashCode());
//				}
//				System.out.println("LAST: ");
//				for(Solution x : lastFront.getSolutions()){
//					System.out.println(x.hashCode());
//				}
//				System.out.println("SOLUTION: ");
//				System.out.println(s.hashCode());
				if (isLastFront.get(s.getVariables())) {
					kPoints.addSolution(s);
					smallestNicheCountRefPoint.incrNicheCount();
					refPQ.add(smallestNicheCountRefPoint);
					break;
				}
			}
		}
		return kPoints;
	}

	private static Population computeExtremePoints(Population population, int numObjectives) {
		Population extremePoints = new Population();
		for (int i = 0; i < numObjectives; i++) {
			double min = Double.MAX_VALUE;
			Solution minSolution = null;
			for (Solution s : population.getSolutions()) {
				double asf = ASF(s.getObjectives(), i);
				if (asf < min) {
					min = asf;
					minSolution = s;
				}
			}
			extremePoints.addSolution(minSolution);
		}
		return extremePoints;
	}

	private static double ASF(double objectives[], int i) {
		double res = Double.MIN_VALUE;
		double cur;
		for (int j = 0; j < objectives.length; j++) {
			if (j == i) {
				cur = objectives[j];
			} else {
				cur = objectives[j] * 100000;
			}
			res = Double.max(res, cur);
		}
		return res;
	}
}
