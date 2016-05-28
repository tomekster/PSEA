package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.hyperplane.ReferencePoint;
import exceptions.DegeneratedMatrixException;
import utils.GaussianElimination;
import utils.Geometry;
import utils.MyComparator;

public class NicheCountSelection {

	private final static Logger LOGGER = Logger.getLogger(NicheCountSelection.class.getName());

	private int numObjectives;
	private Hyperplane hyperplane;

	public NicheCountSelection(int numObjectives) {
		this.numObjectives = numObjectives;
		this.hyperplane = new Hyperplane(numObjectives, getNumPartitions());
	}

	public Population selectKPoints(Population pop, int k)
			throws DegeneratedMatrixException {
		Population normalizedPopulation = normalize(pop);
		associate(normalizedPopulation);
		Population kPoints = niching(pop, k);
		return kPoints;
	}

	public Population normalize(Population allFronts) {
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

		// extremePoints = fixDuplicates(extremePoints);

		double invertedIntercepts[] = new double[extremePoints.size()];

		try {
			invertedIntercepts = findIntercepts(extremePoints);
		} catch (DegeneratedMatrixException e) {
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstObjectives[] = allFronts.findWorstObjectives();
				invertedIntercepts[i] = 1.0 / worstObjectives[i];
			}
			e.printStackTrace();
		}
		if(invertedIntercepts == null){
			invertedIntercepts = new double[extremePoints.size()];
			for (int i = 0; i < extremePoints.size(); i++) {
				double worstObjectives[] = allFronts.findWorstObjectives();
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

	private Population fixDuplicates(Population extremePoints) {

		Population fixed = new Population();
		for (Solution s : extremePoints.getSolutions()) {
			fixed.addSolution(s.copy());
		}

		// Look for duplicates
		for (int i = 0; i < fixed.size(); i++) {
			for (int j = i + 1; j < fixed.size(); j++) {
				if (Geometry.euclideanDistance(fixed.getSolution(i).getObjectives(),
						fixed.getSolution(j).getObjectives()) < MyComparator.EPS) {
					// throw new RuntimeException("Duplicated extreme points");
					LOGGER.severe("Duplicated extreme points");

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

					// Get duplicated solution
					Solution fixedSolution = fixed.getSolution(j);
					// Set all its variables to 0
					for (int k = 0; k < fixedSolution.getNumObjectives(); k++) {
						// Except of the value dimension for which it was chosen
						// via ASF
						if (k != j) {
							fixedSolution.setObjective(k, 0.0);
						}
					}
				}
			}
		}

		return fixed;

	}

	private double[] findIntercepts(Population extremePoints) throws DegeneratedMatrixException {

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

	public void associate(Population population) {
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
	}

	private Population niching(Population pop, int K) {
		Population kPoints = new Population();

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
				kPoints.addSolution(s);
				smallestNicheCountRefPoint.incrNicheCount();
				refPQ.add(smallestNicheCountRefPoint);
				break;	
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
		return extremePoints;
	}

	private double ASF(double objectives[], int i) {
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

	public int getPopulationSize() {
		int populationSize = 0;
		while (populationSize < hyperplane.getReferencePoints().size()) {
			populationSize += 4;
		}
		return populationSize;
	}

	private ArrayList<Integer> getNumPartitions() {
		ArrayList<Integer> res = new ArrayList<>();
		switch (numObjectives) {
		case 2:
			res.add(2);
			break;
		case 3:
			res.add(12);
			break;
		case 5:
			res.add(6);
			break;
		case 8:
		case 10:
			res.add(3);
			res.add(2);
			break;
		case 15:
			res.add(2);
			res.add(1);
			break;
		default:
			throw new RuntimeException("Undefined number of hyperplane partitions for given problem dimensionality ("
					+ numObjectives + ")");
		}
		return res;
	}

	public Hyperplane getHyperplane() {
		return hyperplane;
	}

}
