package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.hyperplane.ReferencePoint;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import utils.GaussianElimination;
import utils.Geometry;
import utils.MyComparator;
import utils.NonDominatedSort;

public class NSGAIII {

	private Problem problem;
	private Population population;
	private int populationSize, numGenerations;
	private Hyperplane hyperplane;

	private SelectionOperator selectionOperator = new BinaryTournament();
	private CrossoverOperator crossoverOperator = new SBX(1.0, 30.0);
	private MutationOperator mutationOperator = new PolynomialMutation(0.0, 0.0);

	public NSGAIII(Problem problem, int populationSize) {
		this.problem = problem;
		this.population = createInitialPopulation();
		this.hyperplane = new Hyperplane(problem.getNumVariables(), problem.getNumPartitions());
	}

	public Population run() {
		Population offspring = createOffspring(population);
		Population combinedPopulation = new Population();

		for (Solution s : population.getSolutions())
			combinedPopulation.addSolution(s);
		for (Solution s : offspring.getSolutions())
			combinedPopulation.addSolution(s);

		ArrayList<Population> fronts = NonDominatedSort.execute(combinedPopulation);

		Population allFronts = new Population();
		Population allButLastFront = new Population();
		Population lastFront;

		int lastFrontId = 0;
		while (true) {
			Population front = fronts.get(lastFrontId);
			if (allButLastFront.size() + front.size() > populationSize) {
				lastFront = front;
				break;
			}

			for (Solution s : fronts.get(lastFrontId).getSolutions()) {
				allButLastFront.addSolution(s.copy());
			}
			lastFrontId++;
		}

		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);

		if (allFronts.size() == populationSize) {
			population = allFronts.copy();
		} else {
			int K = populationSize - allButLastFront.size();
			Population normalizedPopulation = normalize(allFronts, problem.getNumVariables());
			associate(normalizedPopulation);
			Population kPoints = niching(allButLastFront, lastFront, K);
		}

		return population;
	}

	private Population niching(Population allButLastFront, Population lastFront, int K) {
		Population kPoints = new Population();
		HashMap<Solution, Boolean> isLastFront = new HashMap<>();
		for(Solution s : allButLastFront.getSolutions()){
			isLastFront.put(s, false);
		}
		for(Solution s : lastFront.getSolutions()){
			isLastFront.put(s, true);
		}
		
		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(MyComparator.referencePointComparator);
		for(ReferencePoint rp : hyperplane.getReferencePoints()){
			refPQ.add(rp);
		}
		
		while(kPoints.size() < K){
			ReferencePoint smallestNicheCountRefPoint = refPQ.poll();
			PriorityQueue<Association> associatedSolutionsQueue = smallestNicheCountRefPoint.getAssociatedSolutionsQueue();
			while(! associatedSolutionsQueue.isEmpty()){
				Solution s = associatedSolutionsQueue.poll().getSolution();
				if(isLastFront.get(s)){
					kPoints.addSolution(s);
					smallestNicheCountRefPoint.incrNicheCount();
					refPQ.add(smallestNicheCountRefPoint);
					break;
				}
			}
		}
		
		return null;
	}

	private void associate(Population population) {
		hyperplane.resetAssociations();
		ArrayList<ReferencePoint> refPoints = hyperplane.getReferencePoints();

		for (Solution s : population.getSolutions()) {
			double minDist = Double.MAX_VALUE;
			ReferencePoint bestRefPoint = null;
			for (int i = 0; i < refPoints.size(); i++) {
				ReferencePoint curRefPoint = refPoints.get(i);
				double dist = Geometry.pointLineDist(s.getVariables(), curRefPoint.getDimensions());
				if (dist < minDist) {
					minDist = dist;
					bestRefPoint = curRefPoint;
				}
			}
			bestRefPoint.addAssociation(new Association(s, minDist));
		}
	}

	private Population normalize(Population population, int numVariables) {
		Population resPop = population.copy();
		double z_min[] = new double[numVariables];
		for (int j = 0; j < numVariables; j++) {
			double min = Double.MAX_VALUE;
			for (Solution s : resPop.getSolutions()) {
				min = Double.min(s.getVariable(j), min);
			}
			z_min[j] = min;
		}

		for (Solution s : resPop.getSolutions()) {
			for (int j = 0; j < s.getNumVariables(); j++) {
				s.setVariable(j, s.getVariable(j) - z_min[j]);
			}
		}

		Population extremePoints = computeExtremePoints(resPop, numVariables);

		fixDuplicates(extremePoints);

		double invertedIntercepts[] = findIntercepts(extremePoints);

		for (Solution s : resPop.getSolutions()) {
			for (int i = 0; i < s.getNumVariables(); i++) {
				// Multiplication instead of division - explained in
				// findIntercepts()
				s.setVariable(i, s.getVariable(i) * invertedIntercepts[i]);
			}
		}
		return resPop;
	}

	private double[] findIntercepts(Population extremePoints) {

		int n = extremePoints.size();
		double a[][] = new double[n][n];
		double b[] = new double[n];
		for (int i = 0; i < n; i++) {
			b[i] = 1.0;
			for (int j = 0; j < n; j++) {
				a[i][j] = extremePoints.getSolution(i).getVariable(j);
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

	private void fixDuplicates(Population extremePoints) {

		// Look for duplicates
		for (int i = 0; i < extremePoints.size(); i++) {
			for (int j = i + 1; j < extremePoints.size(); j++) {
				if (extremePoints.getSolution(i) == extremePoints.getSolution(j)) {
					throw new RuntimeException("Duplicated extreme points");
				}
			}
		}

		/*
		 * TODO In JMetal idea was following: if extremePoints for i and j are
		 * the same point, we obtain new points by projecting Pi on i-th axis,
		 * and Pj on j-th axis.
		 * 
		 * New idea: Instead of projecting Pi, and Pj we move them epsilon
		 * towards their axis. Problems: - New duplicates may appear, - Lower
		 * bound values for problem - Negative values
		 * 
		 */

	}

	private Population computeExtremePoints(Population population, int numVariables) {
		Population extremePoints = new Population();
		for (int i = 0; i < numVariables; i++) {
			double min = Double.MAX_VALUE;
			Solution minSolution = null;
			for (Solution s : population.getSolutions()) {
				double asf = ASF(s, i);
				if (asf < min) {
					min = asf;
					minSolution = s;
				}
			}
			extremePoints.addSolution(minSolution);
		}
		return extremePoints;
	}

	private double ASF(Solution s, int i) {
		double res = Double.MAX_VALUE;
		double cur;
		for (int j = 0; j < s.getNumVariables(); j++) {
			if (j == i) {
				cur = s.getVariable(j);
			} else {
				cur = s.getVariable(j) * 1000000;
			}
			res = Double.min(res, cur);
		}
		return res;
	}

	private Population createOffspring(Population population) {
		Population offspring = new Population();
		Population matingPopulation = new Population();

		while (matingPopulation.size() < populationSize) {
			matingPopulation.addSolution(selectionOperator.execute(population));
		}

		for (int i = 0; i < populationSize; i += 2) {
			ArrayList<Solution> parents = new ArrayList<Solution>(2);
			parents.add(matingPopulation.getSolution(i));
			parents.add(matingPopulation.getSolution(i + 1));
			ArrayList<Solution> children = crossoverOperator.execute(parents);

			mutationOperator.execute(children.get(0));
			mutationOperator.execute(children.get(1));

			offspring.addSolution(children.get(0));
			offspring.addSolution(children.get(1));
		}
		return offspring;
	}

	private Population createInitialPopulation() {
		Population population = new Population();
		for (int i = 0; i < populationSize; i++) {
			population.addSolution(problem.createSolution());
		}
		return population;
	}

}
