package algorithm.nsgaiii;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.hyperplane.Association;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import utils.math.DegeneratedMatrixException;
import utils.math.GaussianElimination;

/***
 * Class encapsulates "selectKPoints" method from NSGA-III algorithm 
 */
public class EnvironmentalSelection {
	public static Population selectKPoints(int numObjectives, Population allButLastFront, Population lastFront, 
			int k, Hyperplane hyperplane){
		
		Population allFronts = new Population();
		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);
		
		normalize(numObjectives, allFronts);
		hyperplane.associate(allButLastFront, lastFront);
		Population res = niching(allButLastFront, lastFront, k, hyperplane);
		return res;
	}

	public static void normalize(int numObjectives, Population allFronts) {
		double z_min[] = new double[numObjectives];
		
		for(int i=0; i<numObjectives; i++){
			final int j = i;
			z_min[j] = allFronts.getSolutions().stream().mapToDouble(s->s.getObjective(j)).min().getAsDouble();
		}
		
		for(Solution s : allFronts.getSolutions()){
			for(int i=0; i<numObjectives; i++){
				s.setObjective(i, s.getObjective(i) - z_min[i]);
			}
		}

		double invertedIntercepts[] = findIntercepts(numObjectives, allFronts);
		
		for (Solution s : allFronts.getSolutions()) {
			for (int i = 0; i < numObjectives; i++) {
				s.setObjective(i, s.getObjective(i) * invertedIntercepts[i]); // Multiplication instead of division - explained in findIntercepts()
			}
		}
	}
	
	private static double[] alternativeIntercepts(int numObjectives, Population allFronts){
		double coef[] = new double[numObjectives];
		for (int i = 0; i < numObjectives; i++) {
			double worstObjectives[] = findWorstObjectives(numObjectives, allFronts);
			coef[i] = 1.0 / worstObjectives[i];
		}
		return coef;
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

	private static double[] findIntercepts(int numObjectives, Population allFronts){
		Population extremePoints = computeExtremePoints(allFronts, numObjectives);
		
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

			try{
				coef = GaussianElimination.execute(a, b);
			} catch(DegeneratedMatrixException e){
				return alternativeIntercepts(numObjectives, allFronts);
			}
			
			for (int i = 0; i < n; i++) {
				if (coef[i] < 0){
					return alternativeIntercepts(numObjectives, allFronts);
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
			final int j = i;
			extremePoints.addSolution(population.getSolutions().stream().min(Comparator.comparing(s -> ASF( ((Solution)s).getObjectives(), j))).get());
		}
		return extremePoints;
	}

	private static double ASF(double objectives[], int i) {
		int mult = 1000000;
		objectives[i] /= mult;
		double res = Arrays.stream(objectives).max().getAsDouble();
		objectives[i] *= mult;
		return res;
	}
}
