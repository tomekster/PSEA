package core;

public class Solution {

	private int numVariables;
	private double[] variables;
	private double[] objectives;
	private int numObjectives;

	public Solution(int numVariables, int numObjectives) {
		this.numVariables = numVariables;
		this.numObjectives = numObjectives;
		this.variables = new double[numVariables];
		this.objectives = new double[numObjectives];
		for (int i = 0; i < numVariables; i++) {
			variables[i] = 0.0;
		}
	}
	
	public Solution(double vars[], int numObjectives) {
		this(vars.length, numObjectives);
		for (int i = 0; i < numVariables; i++) {
			variables[i] = vars[i];
		}
	}
	
	public Solution(double vars[], double objectives[]) {
		this(vars.length, objectives.length);
		for (int i = 0; i < vars.length; i++) {
			this.variables[i] = vars[i];
		}
		for (int i = 0; i < objectives.length; i++) {
			this.objectives[i] = objectives[i];
		}
	}

	public Solution(Solution solution) {
		this(solution.getVariables(), solution.getNumObjectives());
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
}
