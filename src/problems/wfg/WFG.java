//  WFG.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package problems.wfg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.Solution;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import problems.ContinousProblem;
import utils.WfgFrontReader;
import utils.math.Geometry;

/**
 * Implements a reference abstract class for all wfg org.uma.test problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston A Scalable
 * Multi-objective Test Problem Toolkit. Evolutionary Multi-Criterion
 * Optimization: Third International Conference, EMO 2005. Proceedings, volume
 * 3410 of Lecture Notes in Computer Science
 */
public abstract class WFG extends ContinousProblem{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1127620034852695810L;

	/**
	 * stores a epsilon default value
	 */
	private final double epsilon = (double) 1e-7;

	protected int k;
	protected int m;
	protected int l;
	protected int[] a;
	protected int[] s;
	protected int d = 1;
	protected Random random = new Random();

	/**
	 * Constructor Creates a wfg problem
	 *
	 * @param k
	 *            position-related parameters
	 * @param l
	 *            distance-related parameters
	 * @param M
	 *            Number of objectives
	 */
	public WFG(Integer k, Integer l, Integer M) {
		super(k + l, M, 0, "WFG");
		this.k = k;
		this.l = l;
		this.m = M;

	}

	@Override
	public void setBoundsOnVariables() {
		for (int i = 0; i < getNumVariables(); i++) {
			setLowerBound(i, 0);
			setUpperBound(i, 2.0 * (i + 1));
		}
	}

	/**
	 * Gets the x vector
	 */
	public double[] calculateX(double[] t) {
		double[] x = new double[m];

		for (int i = 0; i < m - 1; i++) {
			x[i] = Math.max(t[m - 1], a[i]) * (t[i] - (double) 0.5) + (double) 0.5;
		}

		x[m - 1] = t[m - 1];

		return x;
	}

	/**
	 * Normalizes a vector (consulte wfg toolkit reference)
	 */
	public double[] normalise(double[] z) {
		double[] result = new double[z.length];

		for (int i = 0; i < z.length; i++) {
			double bound = (double) 2.0 * (i + 1);
			result[i] = z[i] / bound;
			result[i] = correctTo01(result[i]);
		}

		return result;
	}

	/**
	 */
	public double correctTo01(double a) {
		double min = (double) 0.0;
		double max = (double) 1.0;

		double minEpsilon = min - epsilon;
		double maxEpsilon = max + epsilon;

		if ((a <= min && a >= minEpsilon) || (a >= min && a <= minEpsilon)) {
			return min;
		} else if ((a >= max && a <= maxEpsilon) || (a <= max && a >= maxEpsilon)) {
			return max;
		} else {
			return a;
		}
	}

	/**
	 * Gets a subvector of a given vector (Head inclusive and tail inclusive)
	 *
	 * @param z
	 *            the vector
	 * @return the subvector
	 */
	public double[] subVector(double[] z, int head, int tail) {
		int size = tail - head + 1;
		double[] result = new double[size];

		System.arraycopy(z, head, result, head - head, tail + 1 - head);

		return result;
	}
	
	@Override
	public Population getReferenceFront(){
		Hyperplane h = new Hyperplane(this.getNumObjectives());
		ArrayList <ReferencePoint> rp = h.getReferencePoints();
		for(int i=0; i<rp.size(); i++){
			double dim[] = rp.get(i).getDim();
			dim[0] *= 2;
			dim[1] *= 4;
			dim[2] *= 6;
		}
		Population front = WfgFrontReader.getFront(this);
		Population res = new Population();
		for(ReferencePoint r : rp){
			res.addSolution(
					front.getSolutions()
					.stream()
					.map(s-> new Object[] {s, Geometry.pointLineDist(((Solution)s).getObjectives(), r.getObjectives())})
					.min(Comparator.comparingDouble(a -> (Double) a[1]))
					.map(a -> (Solution) a[0])
					.get()
				);
		}
		return res;
	}
	
	@Override
	public int getNumObjectives(){
		return m;
	}
}