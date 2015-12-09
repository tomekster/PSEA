package core;

public class Solution {

	private int numVariables;
	private double[] variables;
	private double[] objectives;
	
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

	public Solution(int numVariables) {
		this.numVariables = numVariables;
		this.variables = new double[numVariables];
		for (int i = 0; i < numVariables; i++) {
			variables[i] = 0.0;
		}
	}

	public Solution(double vars[]) {
		this.numVariables = vars.length;
		this.variables = new double[numVariables];
		for (int i = 0; i < numVariables; i++) {
			variables[i] = vars[i];
		}
	}

	public Solution(Solution solution) {
		this(solution.numVariables);
		variables = solution.getVariables();
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
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for(double d : this.variables){
			sb.append(d);
			sb.append(", ");
		}
		sb.replace(sb.length()-2, sb.length(),")");
		return sb.toString();
	}

	public double[] getVariables() {
		return this.variables;
	}
}
