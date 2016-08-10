package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.hyperplane.ReferencePoint;
import exceptions.DegeneratedMatrixException;
import utils.GaussianElimination;
import utils.Geometry;
import utils.MyComparator;
import utils.NormalizationTransform;

/***
 * Class encapsulates "selectKPoints" method from NSGA-III algorithm 
 */
public class NicheCountSelection {

	public static Population selectKPoints(Population allFronts, Population allButLastFront, Population lastFront, 
			int k, Hyperplane hyperplane, NormalizationTransform normTrans)throws DegeneratedMatrixException {
		double idealPoint[] = getIdealPoint(allFronts, hyperplane.getDim());
		double scales[] = normalize(allFronts, idealPoint);
		normTrans = new NormalizationTransform(idealPoint, scales);
		for(ReferencePoint rp : hyperplane.getReferencePoints()){
			normTrans.denormalize(rp);
		}
		associate(allFronts, hyperplane);
		Population kPoints = niching(allButLastFront, lastFront, k, hyperplane);
		return kPoints;
	}
	private static double [] getIdealPoint(Population pop, int numObjectives){
		double res[] = new double[numObjectives];
		Arrays.fill(res, Double.MAX_VALUE);
		for (int j = 0; j < numObjectives; j++) {
			for (Solution s : pop.getSolutions()) {
				res[j] = Double.min(s.getObjective(j), res[j]);
			}
		}
		return res;
	}

	public static double[] normalize(Population pop, double idealPoint[]) {
		// Move all points to new origin
		int numObjectives = idealPoint.length;
		for (Solution s : pop.getSolutions()) {
			for (int j = 0; j < numObjectives; j++) {
				s.setNormalizedObjective(j, s.getObjective(j) - idealPoint[j]);
			}
		}

		Population extremePoints = computeExtremePoints(pop, numObjectives);

		double invertedIntercepts[] = new double[extremePoints.size()];

		try {
			invertedIntercepts = findIntercepts(extremePoints);
		} catch (DegeneratedMatrixException e) {
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstNormObjectives[] = findWorstNormObjectives(pop, numObjectives);
				invertedIntercepts[i] = 1.0 / worstNormObjectives[i];
			}
			e.printStackTrace();
		}
		//TODO what is this part for?
		if(invertedIntercepts == null){
			invertedIntercepts = new double[extremePoints.size()];
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstObjectives[] = findWorstNormObjectives(pop, numObjectives);
				invertedIntercepts[i] = 1.0 / worstObjectives[i];
			}
		}
		
		for (Solution s : pop.getSolutions()) {
			for (int i = 0; i < numObjectives; i++) {
				//Multiplication instead of division - explained in findIntercepts() method
				s.setObjective(i, s.getNormalizedObjective(i) * invertedIntercepts[i]);
			}
		}
		
		return invertedIntercepts;
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
				a[i] = extremePoints.getSolution(i).getNormObjectives();
			}
			coef = GaussianElimination.execute(a, b);
			for (int i = 0; i < n; i++) {
				if (coef[i] < 0){
					return null;
				}
			}

			/**
			 * Loop beneath comes from JMetal implementation. It was commented 
			 * because since b[i] = 1 for all i and
			 * just after returning from this method we divide each solutions
			 * objective value by corresponding intercept value it is better to
			 * return inversed intercept values (by omitting division by b[i]),
			 * and multiply objective value instead of dividing it.
			 */

			 //for(int i = 0; i < n; i++){ coef[i] /= b[i]; }
			 
		}

		return coef;
	}

	public static void associate(Population population, Hyperplane hyperplane) {
		hyperplane.resetAssociations();
		ArrayList<ReferencePoint> refPoints = hyperplane.getReferencePoints();

		for (Solution s : population.getSolutions()) {
			double minDist = Double.MAX_VALUE;
			ReferencePoint bestRefPoint = null;
			for (int i = 0; i < refPoints.size(); i++) {
				ReferencePoint curRefPoint = refPoints.get(i);
				double dist = Geometry.pointLineDist(s.getNormObjectives(), curRefPoint.getNormDimensions());
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
		HashMap<Solution, Boolean> isLastFront = new HashMap<>();
		for (Solution s : allButLastFront.getSolutions()) {
			isLastFront.put(s, false);
		}
		for (Solution s : lastFront.getSolutions()) {
			isLastFront.put(s, true);
		}
		
		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(MyComparator.referencePointComparator);
		for (ReferencePoint rp : hyperplane.getReferencePoints()) {
			refPQ.add(rp);
		}

		while (kPoints.size() < K) {
			ReferencePoint smallestNicheCountRefPoint = refPQ.poll();
			PriorityQueue<Association> associatedSolutionsQueue = smallestNicheCountRefPoint
					.getAssociatedSolutionsQueue();
			while (!associatedSolutionsQueue.isEmpty()) {
				Solution s = associatedSolutionsQueue.poll().getSolution();
				if (isLastFront.get(s)) {
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
				double asf = ASF(s.getNormObjectives(), i);
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
	
	public static double[] findWorstNormObjectives(Population pop, int numObjectives) {
		double worstObjectives[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++ ){
			worstObjectives[i] = Double.MIN_VALUE;
			for(Solution s : pop.getSolutions()){
				worstObjectives[i] = Double.max(worstObjectives[i], s.getNormalizedObjective(i));
			}
		}
		return worstObjectives;
	}
}
