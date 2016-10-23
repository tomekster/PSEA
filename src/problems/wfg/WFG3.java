//  WFG3.java
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

import core.points.Solution;

/**
 * This class implements the WFG3 problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
public class WFG3 extends WFG {

	public WFG3(Integer M){
		this(M, 3, M);
	}
  /**
   * Creates a default WFG3 instances with
   * 2 position-related parameters
   * 4 distance-related parameters
   * and 2 objectives
   **/
  public WFG3() {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG3 problem instance
   *
   * @param k            Number of position parameters
   * @param l            Number of distance parameters
   * @param m            Number of objective functions
   */
  public WFG3(Integer k, Integer l, Integer m) {
    super(k, l, m);
    setName("WFG3");

    s = new int[m];
    for (int i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    a[0] = 1;
    for (int i = 1; i < m - 1; i++) {
      a[i] = 0;
    }
  }

  public double[] evaluate(double[] z) {
    double[] y;

    y = normalise(z);
    y = t1(y, k);
    y = t2(y, k);
    y = t3(y, k, m);

    double[] result = new double[m];
    double[] x = calculateX(y);
    for (int m = 1; m <= this.m; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).linear(x, m);
    }

    return result;
  }

  /**
   * WFG3 t1 transformation
   */
  public double[] t1(double[] z, int k) {
    double[] result = new double[z.length];

    System.arraycopy(z, 0, result, 0, k);

    for (int i = k; i < z.length; i++) {
      result[i] = (new Transformations()).sLinear(z[i], (double) 0.35);
    }

    return result;
  }


  /**
   * WFG3 t2 transformation
   */
  public double[] t2(double[] z, int k) {
    double[] result = new double[z.length];

    System.arraycopy(z, 0, result, 0, k);

    int l = z.length - k;
    for (int i = k + 1; i <= k + l / 2; i++) {
      int head = k + 2 * (i - k) - 1;
      int tail = k + 2 * (i - k);
      double[] subZ = subVector(z, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).rNonsep(subZ, 2);
    }
    return result;
  }

  /**
   * WFG3 t3 transformation
   */
  public double[] t3(double[] z, int k, int M) {
    double[] result = new double[M];
    double[] w = new double[z.length];

    for (int i = 0; i < z.length; i++) {
      w[i] = (double) 1.0;
    }

    for (int i = 1; i <= M - 1; i++) {
      int head = (i - 1) * k / (M - 1) + 1;
      int tail = i * k / (M - 1);
      double[] subZ = subVector(z, head - 1, tail - 1);
      double[] subW = subVector(w, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).rSum(subZ, subW);
    }

    int l = z.length - k;
    int head = k + 1;
    int tail = k + l / 2;
    double[] subZ = subVector(z, head - 1, tail - 1);
    double[] subW = subVector(w, head - 1, tail - 1);
    result[M - 1] = (new Transformations()).rSum(subZ, subW);

    return result;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) {
    double[] variables = new double[getNumVariables()];
    double[] x = new double[getNumVariables()];

    for (int i = 0; i < getNumVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    for (int i = 0; i < getNumVariables(); i++) {
      variables[i] = (double) x[i] ;
    }

    double[] sol2 = evaluate(variables);

    for (int i = 0; i < sol2.length; i++) {
      solution.setObjective(i, sol2[i]);
    }
  }
}
