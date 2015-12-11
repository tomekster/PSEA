package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.hyperplane.ReferencePoint;
import utils.GaussianElimination;
import utils.Geometry;
import utils.MyComparator;

public class NicheCountSelection {

	private int numObjectives;
	private Hyperplane hyperplane;

	public NicheCountSelection(int numObjectives) {
		this.numObjectives = numObjectives;
		this.hyperplane = new Hyperplane(numObjectives, getNumPartitions());
	}

	public Population selectKPoints(Population allFronts, Population allButLastFront, Population lastFront, int k) {
		// System.out.println("selectKPoints");
		Population normalizedPopulation = normalize(allFronts);
		associate(normalizedPopulation);
		Population kPoints = niching(allButLastFront, lastFront, k);
		return kPoints;
	}

	private Population normalize(Population population) {
		// System.out.println("NORMALIZATION");
		Population resPop = population.copy();
		double z_min[] = new double[numObjectives];
		for (int j = 0; j < numObjectives; j++) {
			double min = Double.MAX_VALUE;
			for (Solution s : resPop.getSolutions()) {
				min = Double.min(s.getObjective(j), min);
			}
			z_min[j] = min;
		}

		for (Solution s : resPop.getSolutions()) {
			for (int j = 0; j < numObjectives; j++) {
				s.setObjective(j, s.getObjective(j) - z_min[j]);
			}
		}

		Population extremePoints = computeExtremePoints(resPop, numObjectives);

		fixDuplicates(extremePoints);

		double invertedIntercepts[] = findIntercepts(extremePoints);

		for (Solution s : resPop.getSolutions()) {
			for (int i = 0; i < numObjectives; i++) {
				// Multiplication instead of division - explained in
				// findIntercepts()
				s.setObjective(i, s.getObjective(i) * invertedIntercepts[i]);
			}
		}
		return resPop;
	}

	private void fixDuplicates(Population extremePoints) {

		// Look for duplicates
		for (int i = 0; i < extremePoints.size(); i++) {
			for (int j = i + 1; j < extremePoints.size(); j++) {
				if (extremePoints.getSolution(i) == extremePoints.getSolution(j)) {
					// throw new RuntimeException("Duplicated extreme points");

					System.out.println("Duplicated extreme points");

					/*
					 * TODO In JMetal idea was following: if extremePoints for i
					 * and j are the same point, we obtain new points by
					 * projecting Pi on i-th axis, and Pj on j-th axis.
					 * 
					 * New idea: Instead of projecting Pi, and Pj we move them
					 * epsilon towards their axis. Problems: - New duplicates
					 * may appear, - Lower bound values for problem - Negative
					 * values
					 * 
					 */
					Solution dup = extremePoints.getSolution(j);
					for (int k = 0; k < dup.getNumObjectives(); k++) {
						if (k != 1)
							dup.setObjective(k, 0.0);
					}
				}
			}
		}
	}

	private double[] findIntercepts(Population extremePoints) {

		int n = extremePoints.size();
		double a[][] = new double[n][n];
		double b[] = new double[n];
		for (int i = 0; i < n; i++) {
			b[i] = 1.0;
			for (int j = 0; j < n; j++) {
				a[i][j] = extremePoints.getSolution(i).getObjective(j);
			}
		}

		double coef[] = new double[n];
		coef = GaussianElimination.execute(a, b);

		/**
		 * Loop beneath was commented, because since b[i] = 1 for all i and just
		 * after returning from this method we divide each solutions objective
		 * value by corresponding intercept value it is better to return
		 * inversed intercept values (by omitting division by b[i]), and
		 * multiply objective value instead of dividing it.
		 */

		/*
		 * for(int i = 0; i < n; i++){ coef[i] /= b[i]; }
		 */

		return coef;
	}

	private void associate(Population population) {
		hyperplane.resetAssociations();
		ArrayList<ReferencePoint> refPoints = hyperplane.getReferencePoints();

		for (Solution s : population.getSolutions()) {
			double minDist = Double.MAX_VALUE;
			ReferencePoint bestRefPoint = null;
			for (int i = 0; i < refPoints.size(); i++) {
				ReferencePoint curRefPoint = refPoints.get(i);
				double dist = Geometry.pointLineDist(s.getObjectives(), curRefPoint.getDimensions());
				if (dist < minDist) {
					minDist = dist;
					bestRefPoint = curRefPoint;
				}
			}
			bestRefPoint.addAssociation(new Association(s, minDist));
		}

		// System.out.println("ASSOCIATIONS: ");
		// for(ReferencePoint rp : refPoints){
		// System.out.println(rp);
		// }
	}

	private Population niching(Population allButLastFront, Population lastFront, int K) {
		Population kPoints = new Population();
		HashMap<Solution, Boolean> isLastFront = new HashMap<>();
		// System.out.println("FALSE: ");
		for (Solution s : allButLastFront.getSolutions()) {
			isLastFront.put(s, false);
			// System.out.println(s);
		}
		// System.out.println("TURUE: ");
		for (Solution s : lastFront.getSolutions()) {
			isLastFront.put(s, true);
			// System.out.println(s);
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
				// System.out.println("AssociatedSolutionQueue.poll(): " + s);
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

	private Population computeExtremePoints(Population population, int numObjectives) {
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

		// System.out.println("EXTREME POINTS: ");
		// System.out.println(population);
		// System.out.println(extremePoints);

		return extremePoints;
	}

	private double ASF(double objectives[], int i) {
		double res = Double.MIN_VALUE;
		double cur;
		for (int j = 0; j < objectives.length; j++) {
			if (j == i) {
				cur = objectives[j];
			} else {
				cur = objectives[j] * 1000000;
			}
			res = Double.max(res, cur);
		}
		return res;
	}

	public int getPopulationSize() {
		// System.out.println("REF POINTS: " +
		// hyperplane.getReferencePoints().size());
		int populationSize = 0;
		while (populationSize < hyperplane.getReferencePoints().size()) {
			populationSize += 4;
		}
		return populationSize;
	}

	private int getNumPartitions() {
		switch (numObjectives) {
		case 3:
			return 12;
		case 5:
			return 6;
		default:
			throw new RuntimeException("Undefined number of hyperplane partitions for given problem dimensionality ("
					+ numObjectives + ")");
		}
	}

	public Hyperplane getHyperplane() {
		return hyperplane;
	}

}
