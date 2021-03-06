package core;

import java.util.Comparator;
import java.util.PriorityQueue;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.points.ReferencePoint;
import core.points.Solution;
import utils.DegeneratedMatrixException;
import utils.GaussianElimination;

/***
 * Class encapsulates "selectKPoints" method from NSGA-III algorithm 
 */
public class NicheCountSelection {
	public static Population selectKPoints(int numObjectives, Population allFronts, Population allButLastFront, Population lastFront, 
			int k, Hyperplane hyperplane) throws DegeneratedMatrixException {
		normalize(numObjectives, allFronts);
		hyperplane.associate(allButLastFront, lastFront);
		Population res = niching(allButLastFront, lastFront, k, hyperplane);
		return res;
	}

	public static void normalize(int numObjectives, Population allFronts) {
		double z_min[] = new double[numObjectives];
		
		for(int i=0; i<numObjectives; i++){
			z_min[i] = Double.MAX_VALUE;
		}
		
		for(Solution s : allFronts.getSolutions()){
			for(int i=0; i<numObjectives; i++){
				z_min[i] = Double.min(z_min[i], s.getObjective(i));
			}
		}
		
		for(Solution s : allFronts.getSolutions()){
			for(int i=0; i<numObjectives; i++){
				double newObj = s.getObjective(i) - z_min[i];
				assert newObj >= 0;
				s.setObjective(i, newObj);
			}
		}

		Population extremePoints = computeExtremePoints(allFronts, numObjectives);

		double invertedIntercepts[] = new double[extremePoints.size()];

		try {
			invertedIntercepts = findIntercepts(extremePoints);
		} catch (DegeneratedMatrixException e) {
			invertedIntercepts = null;
		}
		if(invertedIntercepts == null){
			invertedIntercepts = new double[extremePoints.size()];
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstObjectives[] = findWorstObjectives(numObjectives, allFronts);
				invertedIntercepts[i] = 1.0 / worstObjectives[i];
			}
		}
		
		for (Solution s : allFronts.getSolutions()) {
			for (int i = 0; i < numObjectives; i++) {
				/**
				 * Multiplication instead of division - explained in
				 * findIntercepts()
				 */
				s.setObjective(i, s.getObjective(i) * invertedIntercepts[i]);
			}
		}
	}

	private static double[] findWorstObjectives(int numObjectives, Population allFronts) {
		double res[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			res[i] = -Double.MAX_VALUE;
		}
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

	private static Population niching(Population allButLastFront, Population lastFront, int K, Hyperplane hyperplane) {
		Population kPoints = new Population();
		
		int nicheCount=0, lastFrontCount=0;
		
		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(new Comparator <ReferencePoint>() {
			@Override
			public int compare(ReferencePoint o1, ReferencePoint o2) {
				return Double.compare(o1.getNicheCount(), o2.getNicheCount()); //Sort increasingly by nichecount
			}
		});
		
		for (ReferencePoint rp : hyperplane.getReferencePoints()) {
			refPQ.add(rp);
			nicheCount += rp.getNicheCount();
			lastFrontCount += rp.getLastFrontAssociationsQueue().size();
		}
		assert nicheCount == allButLastFront.size();
		assert lastFrontCount == lastFront.size();
		assert K <= lastFront.size();
		
		while (kPoints.size() < K) {
			ReferencePoint smallestNicheCountRefPoint = refPQ.poll();
			PriorityQueue<Association> associatedLastFrontSolutions = smallestNicheCountRefPoint
					.getLastFrontAssociationsQueue();
			if(associatedLastFrontSolutions.isEmpty()) continue;
			Association a = associatedLastFrontSolutions.poll();
			kPoints.addSolution(a.getSolution());
			smallestNicheCountRefPoint.addNichedAssociation(a);
			refPQ.add(smallestNicheCountRefPoint);
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
