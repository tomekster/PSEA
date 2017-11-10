/**
 * WFG9.java
 * @author Juan J. Durillo
 * @version 1.0
 */
package problems.wfg;

import algorithm.geneticAlgorithm.solutions.Solution;

/**
 * Creates a default WFG9 problem with
 * 2 position-related parameters,
 * 4 distance-related parameters,
 * and 2 objectives
 */
public class WFG9 extends WFG {
	/**
	 * 
	 */
	private static final long serialVersionUID = 912532111390784752L;

	public WFG9(Integer M){
		this(2*(M-1), 2, M);
	}
  /**
   * Creates a default WFG9 with
   * 2 position-related parameters,
   * 4 distance-related parameters,
   * and 2 objectives
   */
  public WFG9() {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG9 problem instance
   *
   * @param k            Number of position variables
   * @param l            Number of distance variables
   * @param m            Number of objective functions
   */
  public WFG9(Integer k, Integer l, Integer m) {
    super(k, l, m);
    setName("WFG9");

    s = new int[m];
    for (int i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    for (int i = 0; i < m - 1; i++) {
      a[i] = 1;
    }
  }

  /** Evaluate() method */
  public double[] evaluate(double[] z) {
    double[] y;

    y = normalise(z);
    y = t1(y, k);
    y = t2(y, k);
    y = t3(y, k, m);

    double[] result = new double[m];
    double[] x = calculateX(y);
    for (int m = 1; m <= this.m; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).concave(x, m);
    }
    return result;
  }

  /**
   * WFG9 t1 transformation
   */
  public double[] t1(double[] z, int k) {
    double[] result = new double[z.length];
    double[] w = new double[z.length];

    for (int i = 0; i < w.length; i++) {
      w[i] = (double) 1.0;
    }

    for (int i = 0; i < z.length - 1; i++) {
      int head = i + 1;
      int tail = z.length - 1;
      double[] subZ = subVector(z, head, tail);
      double[] subW = subVector(w, head, tail);
      double aux = (new Transformations()).rSum(subZ, subW);
      result[i] = (new Transformations())
        .bParam(z[i], aux, (double) 0.98 / (double) 49.98, (double) 0.02, (double) 50);
    }

    result[z.length - 1] = z[z.length - 1];
    return result;
  }

  /**
   * WFG9 t2 transformation
   */
  public double[] t2(double[] z, int k) {
    double[] result = new double[z.length];

    for (int i = 0; i < k; i++) {
      result[i] = (new Transformations()).sDecept(z[i], (double) 0.35, (double) 0.001, (double) 0.05);
    }

    for (int i = k; i < z.length; i++) {
      result[i] = (new Transformations()).sMulti(z[i], 30, 95, (double) 0.35);
    }

    return result;
  }

  /**
   * WFG9 t3 transformation
   */
  public double[] t3(double[] z, int k, int M) {
    double[] result = new double[M];

    for (int i = 1; i <= M - 1; i++) {
      int head = (i - 1) * k / (M - 1) + 1;
      int tail = i * k / (M - 1);
      double[] subZ = subVector(z, head - 1, tail - 1);
      result[i - 1] = (new Transformations()).rNonsep(subZ, k / (M - 1));
    }

    int head = k + 1;
    int tail = z.length;
    int l = z.length - k;
    double[] subZ = subVector(z, head - 1, tail - 1);
    result[M - 1] = (new Transformations()).rNonsep(subZ, l);

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


