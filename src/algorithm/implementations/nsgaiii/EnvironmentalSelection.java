package algorithm.implementations.nsgaiii;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.nsgaiii.hyperplane.Association;
import algorithm.implementations.nsgaiii.hyperplane.Hyperplane;
import algorithm.implementations.nsgaiii.hyperplane.ReferencePoint;
import utils.math.DegeneratedMatrixException;
import utils.math.GaussianElimination;
import utils.math.Geometry;

/***
 * Class encapsulates "selectKPoints" method from NSGA-III algorithm 
 */
public class EnvironmentalSelection {
	
	public static <S extends Solution> Population <S> selectKPoints(int numObjectives, Population <S> allButLastFront, Population <S> lastFront, 
			int k, Hyperplane hyperplane){
		
		Population <S> allFronts = new Population<>();
		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);
		
		normalize(numObjectives, allFronts);
		for(int i=0; i<numObjectives; i++){
			final int j = i;
			assert allFronts.getSolutions().stream().mapToDouble(s -> s.getObjective(j)).min().getAsDouble() == 0;
		}
		
		hyperplane.associate(allButLastFront, lastFront);
		return niching(allButLastFront, lastFront, k, hyperplane);
	}

	public static <S extends Solution> void normalize(int numObjectives, Population <S> allFronts) {
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

		double invertedIntercepts[] = findInvertedIntercepts(numObjectives, allFronts);
		
		for (Solution s : allFronts.getSolutions()) {
			for (int i = 0; i < numObjectives; i++) {
				s.setObjective(i, s.getObjective(i) * invertedIntercepts[i]); // Multiplication instead of division - explained in findIntercepts()
			}
		}
	}
	
	private static double[] alternativeInvertedIntercepts(int numObjectives, Population <? extends Solution> allFronts){
		return Geometry.invert(findWorstObjectives(numObjectives, allFronts));
	}

	private static double[] findWorstObjectives(int numObjectives, Population <? extends Solution> allFronts) {
		double res[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			final int j = i;
			res[j] = allFronts.getSolutions().stream().mapToDouble(s -> s.getObjective(j)).max().getAsDouble();
		}
		return res;
	}

	private static double[] findInvertedIntercepts(int numObjectives, Population <? extends Solution> allFronts){
		Population <? extends Solution> extremePoints = computeExtremePoints(allFronts, numObjectives);
		
		int n = extremePoints.size();

		boolean duplicate = false;
		for (int i = 0; !duplicate && i < n; i++) {
			for (int j = i + 1; !duplicate && j < n; j++) {
				duplicate = extremePoints.getSolution(i) == extremePoints.getSolution(j);
			}
		}

		if(duplicate){
			return alternativeInvertedIntercepts(numObjectives, allFronts);
		}

		double coef[] = new double[n];
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
			return alternativeInvertedIntercepts(numObjectives, allFronts);
		}
			
		for (int i = 0; i < n; i++) {
			if (coef[i] < 0){
				return alternativeInvertedIntercepts(numObjectives, allFronts);
			}
		}

		/**
		 * Loop beneath was commented, because since b[i] = 1 for all i and
		 * just after returning from this method we divide each solutions
		 * objective value by corresponding intercept value it is better to
		 * return inverted intercept values (by omitting division by b[i]),
		 * and multiply objective value instead of dividing it.
		 */

		/*
		 * for(int i = 0; i < n; i++){ coef[i] /= b[i]; }
		 */

		return coef;
	}

	private static <S extends Solution> Population <S> niching(Population <S> allButLastFront, Population <S> lastFront, int K, Hyperplane hyperplane) {
		Population <S> kPoints = new Population <> ();
		
		int totalNicheCount=0, totalLastFrontCount=0;
		
		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(new Comparator <ReferencePoint>() {
			@Override
			public int compare(ReferencePoint rp1, ReferencePoint rp2) {
				return Double.compare(rp1.getNicheCount(), rp2.getNicheCount()); //Sort increasingly by nichecount
			}
		});
		
		for (ReferencePoint rp : hyperplane.getReferencePoints()) {
			refPQ.add(rp);
			totalNicheCount += rp.getNicheCount();
			totalLastFrontCount += rp.getLastFrontAssociationsQueue().size();
		}
		assert totalNicheCount == allButLastFront.size();
		assert totalLastFrontCount == lastFront.size();
		assert K <= lastFront.size();
		
		while (kPoints.size() < K) {
			ReferencePoint smallestNicheCountRefPoint = refPQ.poll();
			PriorityQueue<Association> associatedLastFrontSolutions = smallestNicheCountRefPoint.getLastFrontAssociationsQueue();
			if(associatedLastFrontSolutions.isEmpty()) continue;
			Association a = associatedLastFrontSolutions.poll();
			kPoints.addSolution((S) a.getSolution());
			smallestNicheCountRefPoint.addNichedAssociation(a);
			refPQ.add(smallestNicheCountRefPoint);
		}
		return kPoints;
	}

	private static <S extends Solution> Population <S> computeExtremePoints(Population <S> population, int numObjectives) {
		Population <S> extremePoints = new Population <> ();
		for (int i = 0; i < numObjectives; i++) {
			final int j = i;
			extremePoints.addSolution(population.getSolutions().stream().min(Comparator.comparing(s -> ASF(s.getObjectives(), j))).get());
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
