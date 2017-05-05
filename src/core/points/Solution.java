package core.points;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Solution implements Comparable<Solution>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4812314617548779560L;
	protected double[] variables;
	protected double[] objectives;
	private boolean dominated;

	public Solution(double vars[], double obj[]) {
		this.variables = vars.clone();
		this.objectives = obj.clone();
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
		sb.append("[" + format.format(objectives[0]));
		for(int i=1; i<objectives.length; i++){
			sb.append(", ");
			sb.append(format.format(objectives[i]));
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("VAR: (");
		for (double v : variables) {
			sb.append(v);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")\n");
		
		sb.append("OBJ: (");
		for (double d : objectives) {
			sb.append(d);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")");
		return sb.toString();
	}

	public double[] getVariables() {
		return variables;
	}

	public int getNumObjectives() {
		return objectives.length;
	}

	public double getObjective(int pos) {
		return objectives[pos];
	}

	public double[] getObjectives() {
		return objectives;
	}

	public int getNumVariables() {
		return variables.length;
	}

	public double getVariable(int pos) {
		return variables[pos];
	}

	public void setVariable(int pos, double val) {
		variables[pos] = val;
	}
	
	public void setVariables(double[] val) {
		variables = val.clone();
	}

	public void setObjective(int pos, double objective) {
		this.objectives[pos] = objective;
	}

	public boolean sameSolution(Solution s) {
		for(int i=0; i<variables.length; i++){
			if( Double.compare(s.getVariable(i), variables[i]) != 0) return false;
		}
		return true;
	}

	public boolean isDominated() {
		return dominated;
	}

	public void setDominated(boolean dominated) {
		this.dominated = dominated;
	}

	/**
	 * Finds dominating solution assuming minimization problem

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
