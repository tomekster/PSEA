package core;

import java.text.DecimalFormat;
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
	
	public String objs(){
		StringBuffer sb = new StringBuffer();
		DecimalFormat format = new DecimalFormat("0.00");
		sb.append("[" + format.format(this.objectives[0]));
		for(int i=1; i<numObjectives; i++){
			sb.append(", ");
			sb.append(format.format(this.objectives[i]));
		}
		sb.append("]");
		return sb.toString();
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
}
