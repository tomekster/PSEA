package core;

import java.util.Arrays;

public class Solution {

	private int numVariables;
	private int numObjectives;
	private double[] variables;
	private double[] objectives;

	public Solution(double vars[], double obj[]) {
		this.numVariables = vars.length;
		this.numObjectives = obj.length;

		this.variables = new double[vars.length];
		this.objectives = new double[obj.length];

		for (int i = 0; i < vars.length; i++) {
			this.variables[i] = vars[i];
		}

		for (int i = 0; i < obj.length; i++) {
			this.objectives[i] = obj[i];
		}
	}

	public Solution(Solution solution) {
		this(solution.getVariables(), solution.getObjectives());
	}

	public Solution copy() {
		return new Solution(this);
	}

	public boolean sameAs(Solution s2) {
		if (this.numVariables != s2.getNumVariables()) {
			return false;
		}

		for (int i = 0; i < this.numVariables; i++) {
			if (Double.compare(this.variables[i], s2.getVariable(i)) != 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("VAR: (");
		for (double d : this.variables) {
			sb.append(d);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")\n");
		sb.append("OBJ: (");
		for (double d : this.objectives) {
			sb.append(d);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")");
		return sb.toString();
	}

	public double[] getVariables() {
		return this.variables;
	}

	public int getNumObjectives() {
		return numObjectives;
	}

	public double getObjective(int pos) {
		return this.objectives[pos];
	}

	public void setNumObjectives(int numObjectives) {
		this.numObjectives = numObjectives;
	}

	public double[] getObjectives() {
		return objectives;
	}

	public int getNumVariables() {
		return this.numVariables;
	}

	public double getVariable(int pos) {
		return variables[pos];
	}

	public void setVariable(int pos, double val) {
		variables[pos] = val;
	}

	public void setObjective(int pos, double objective) {
		this.objectives[pos] = objective;
	}

	public void setNumVariables(int numVariables) {
		this.numVariables = numVariables;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numObjectives;
		result = prime * result + numVariables;
		result = prime * result + Arrays.hashCode(variables);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Solution other = (Solution) obj;
		if (numObjectives != other.numObjectives)
			return false;
		if (numVariables != other.numVariables)
			return false;
		if (!Arrays.equals(variables, other.variables))
			return false;
		return true;
	}
	
}
