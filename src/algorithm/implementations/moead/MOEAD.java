package algorithm.implementations.moead;

//MOEAD.java
//
//Author:
//   Antonio J. Nebro <antonio@lcc.uma.es>
//   Juan J. Durillo <durillo@lcc.uma.es>
//
//Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.util.Vector;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.Problem;
import utils.math.MyRandom;
import algorithm.implementations.moead.Utils;

public class MOEAD<S extends Solution> extends EA<S> {

	/**
	 * Z vector (ideal point)
	 */
	double[] z_;
	/**
	 * Lambda vectors
	 */
	// Vector<Vector<Double>> lambda_ ;
	double[][] lambda_;
	/**
	 * T: neighbour size
	 */
	int T_;
	/**
	 * Neighborhood
	 */
	int[][] neighborhood_;
	/**
	 * delta: probability that parent solutions are selected from neighbourhood
	 */
	double delta_;
	/**
	 * nr: maximal number of solutions replaced by each child solution
	 */
	int nr_;
	Population<S> indArray_;
	String dataDirectory_;

	/** 
	* Constructor
	* @param problem Problem to solve
	*/
	public MOEAD(Problem <S> problem, int popSize, GeneticOperators<S> go, double[] idealPoint, int neighSize) {
		super(problem, popSize, go); 
		z_ = idealPoint.clone();
		
		this.popSize = popSize; 
		Population <S> indArray_ = new Population<>();

		T_ = neighSize;

		/*
		 * T_ = (int) (0.1 * populationSize_); delta_ = 0.9; nr_ = (int) (0.01 *
		 * populationSize_);
		 */
		neighborhood_ = new int[this.popSize][T_];

		z_ = new double[problem.getNumObjectives()];
		// lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
		lambda_ = new double[this.popSize][problem.getNumObjectives()];

		// STEP 1. Initialization
		// STEP 1.1. Compute euclidean distances between weight vectors and find
		// T
		initUniformWeight();
		// for (int i = 0; i < 300; i++)
		// System.out.println(lambda_[i][0] + " " + lambda_[i][1]) ;

		initNeighborhood();
	}

	/**
	 * initUniformWeight
	 */
	public void initUniformWeight() {
		for (int n = 0; n < this.popSize; n++) {
			double a = 1.0 * n / (this.popSize - 1);
			lambda_[n][0] = a;
			lambda_[n][1] = 1 - a;
		} // for
	} // initUniformWeight

	/**
	* 
	*/
	public void initNeighborhood() {
		double[] x = new double[popSize];
		int[] idx = new int[popSize];

		for (int i = 0; i < popSize; i++) {
			// calculate the distances based on weight vectors
			for (int j = 0; j < popSize; j++) {
				x[j] = Utils.distVector(lambda_[i], lambda_[j]);
				// x[j] = dist_vector(population[i].namda,population[j].namda);
				idx[j] = j;
				// System.out.println("x["+j+"]: "+x[j]+ ". idx["+j+"]:
				// "+idx[j]) ;
			} // for

			// find 'niche' nearest neighboring subproblems
			Utils.minFastSort(x, idx, popSize, T_);
			// minfastsort(x,idx,population.size(),niche);

			System.arraycopy(idx, 0, neighborhood_[i], 0, T_);
		} // for
	} // initNeighborhood

//	/**
//	* 
//	*/
//	public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
//		// list : the set of the indexes of selected mating parents
//		// cid : the id of current subproblem
//		// size : the number of selected mating parents
//		// type : 1 - neighborhood; otherwise - whole population
//		int ss;
//		int r;
//		int p;
//
//		ss = neighborhood_[cid].length;
//		while (list.size() < size) {
//			if (type == 1) {
//				r = PseudoRandom.randInt(0, ss - 1);
//				p = neighborhood_[cid][r];
//				// p = population[cid].table[r];
//			} else {
//				p = PseudoRandom.randInt(0, populationSize_ - 1);
//			}
//			boolean flag = true;
//			for (int i = 0; i < list.size(); i++) {
//				if (list.get(i) == p) // p is in the list
//				{
//					flag = false;
//					break;
//				}
//			}
//
//			// if (flag) list.push_back(p);
//			if (flag) {
//				list.addElement(p);
//			}
//		}
//	} // matingSelection

	/**
	 * 
	 * @param individual
	 */
	void updateReference(S individual) {
		for (int n = 0; n < problem.getNumObjectives(); n++) {
			if (individual.getObjective(n) < z_[n]) {
				z_[n] = individual.getObjective(n);
				indArray_.getSolutions().set(n, (S) individual.copy());
			}
		}
	} // updateReference

	/**
	 * @param individual
	 * @param id
	 * @param type
	 */
	void updateProblem(S indiv, int id, int type) {
		// indiv: child solution
		// id: the id of current subproblem
		// type: update solutions in - neighborhood (1) or whole population
		// (otherwise)
		int size;

		if (type == 1) {
			size = neighborhood_[id].length;
		} else {
			size = popSize;
		}
		int[] perm = new int[size];

		Utils.randomPermutation(perm, size);

		for (int i = 0; i < size; i++) {
			int k;
			if (type == 1) {
				k = neighborhood_[id][perm[i]];
			} else {
				k = perm[i]; // calculate the values of objective function
								// regarding the current subproblem
			}
			double f1, f2;

			f1 = fitnessFunction(population.getSolution(k), lambda_[k]);
			f2 = fitnessFunction(indiv, lambda_[k]);

			if (f2 < f1) {
				population.getSolutions().set(k, indiv);
			}
		}
	} // updateProblem

	@Override
	protected Population <S> selectNewPopulation(Population <S> pop) {
		
		int[] permutation = new int[popSize];
		Utils.randomPermutation(permutation, popSize);

		for (int i = 0; i < popSize; i++) {
			int n = permutation[i]; // or int n = i;
			

			// STEP 2.4. Update z_
			updateReference(child);

			// STEP 2.5. Update of solutions
			updateProblem(child, n, type);
	}
} // MOEAD
