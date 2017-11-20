package algorithm.evolutionary.solutions;

import utils.Copyable;

public class VectorSolution <T extends Number> extends Solution{
	
	protected T[] var;
	
	public VectorSolution(T[] vars, double obj[]) {
		super(obj);
		this.var = vars.clone();
	}

	public VectorSolution(VectorSolution <T> solution) {
		this(solution.getVariables(), solution.getObjectives());
		this.dominated = solution.isDominated();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("VAR: (");
		for (T v : var) {
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

	public T[] getVariables() {
		return var;
	}

	public int getNumVariables() {
		return var.length;
	}

	public T getVariable(int pos) {
		return var[pos];
	}

	public void setVariable(int pos, T val) {
		var[pos] = val;
	}
	
	public void setVariables(T[] val) {
		var = val.clone();
	}

	@Override
	public Copyable copy() {
		return new VectorSolution <T> (this);
	}
	
	
}
