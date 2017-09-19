package algorithm.geneticAlgorithm.solution;

/**
 * Solution is a core class for any Evolutionary Algorithm
 * Every Solution object stores array of real values called variables - var 
 * and array of real values called objectives - obj.
 * It is responsibility of Problem class to provide information about dimensions of both arrays
 * and to provide "evaluate" method that computes all objective values based on values stored in var array.
 * @author Tomasz
 *
 */
public class DoubleSolution extends Solution {
	private static final long serialVersionUID = 4812314617548779560L;
	protected double[] var;

	public DoubleSolution(double var[], double obj[]) {
		this.var = var.clone();
		this.obj = obj.clone();
		setDominated(false);
	}
	
	public DoubleSolution(DoubleSolution s) {
		this(s.getVariables(), s.getObjectives());
		this.setDominated(s.isDominated());
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

	@Override
	public Solution copy() {
		DoubleSolution ds = new DoubleSolution(this.getVariables(), this.getObjectives());
		ds.dominated = this.isDominated();
		return ds;
	}

}
