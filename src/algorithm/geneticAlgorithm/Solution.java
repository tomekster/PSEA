package algorithm.geneticAlgorithm;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Solution is a core class for any Evolutionary Algorithm
 * Every Solution object stores array of real values called variables - var 
 * and array of real values called objectives - obj.
 * It is responsibility of Problem class to provide information about dimensions of both arrays
 * and to provide "evaluate" method that computes all objective values based on values stored in var array.
 * @author Tomasz
 *
 */
public class Solution implements Comparable<Solution>, Serializable {
	private static final long serialVersionUID = 4812314617548779560L;
	protected double[] var;
	protected double[] obj;
	private boolean dominated;

	public Solution(double var[], double obj[]) {
		this.var = var.clone();
		this.obj = obj.clone();
		setDominated(false);
	}

	public Solution(Solution solution) {
		this(solution.getVariables(), solution.getObjectives());
		this.dominated = solution.isDominated();
	}

	public Solution copy() {
		return new Solution(this);
	}
	
	public String objs(){
		StringBuffer sb = new StringBuffer();
		DecimalFormat format = new DecimalFormat("0.00");
		sb.append("[" + format.format(obj[0]));
		for(int i=1; i<obj.length; i++){
			sb.append(", ");
			sb.append(format.format(obj[i]));
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("VAR: (");
		for (double v : var) {
			sb.append(v);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")\n");
		
		sb.append("OBJ: (");
		for (double d : obj) {
			sb.append(d);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")");
		return sb.toString();
	}

	public double[] getVariables() {
		return var;
	}

	public int getNumObjectives() {
		return obj.length;
	}

	public double getObjective(int pos) {
		return obj[pos];
	}

	public double[] getObjectives() {
		return obj;
	}

	public int getNumVariables() {
		return var.length;
	}

	public double getVariable(int pos) {
		return var[pos];
	}

	public void setVariable(int pos, double val) {
		var[pos] = val;
	}
	
	public void setVariables(double[] val) {
		var = val.clone();
	}

	public void setObjective(int pos, double objective) {
		this.obj[pos] = objective;
	}

	public boolean isDominated() {
		return dominated;
	}

	public void setDominated(boolean dominated) {
		this.dominated = dominated;
	}

	/**
	 * Checks if given solutions dominates solution s2, solution s2 dominates given solution, or none of solution dominates other.
	 * @return 0 if neither of solutions dominates other ; -1 if s1
	 *         dominates s2 ; 1 if s2 dominates s1
	 */
	@Override
	public int compareTo(Solution s2) {
		if (this.getNumObjectives() != s2.getNumObjectives()) {
			throw new RuntimeException("Incomparable solutions. Different number of dimensions");
		}

		boolean firstDominates = false, secondDominates = false;
		int flag;
		for (int pos = 0; pos < this.getNumObjectives(); pos++) {
			flag = Double.compare(this.getObjective(pos), s2.getObjective(pos));
			if (flag == 1)
				secondDominates = true;
			if (flag == -1)
				firstDominates = true;
		}

		if (firstDominates && !secondDominates)
			return -1;
		else if (!firstDominates && secondDominates)
			return 1;
		else
			return 0;
	}
}
