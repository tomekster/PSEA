package algorithm.geneticAlgorithm.solution;

import java.io.Serializable;
import java.text.DecimalFormat;

public abstract class Solution implements Comparable<Solution>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2692692848033042805L;
	protected double[] obj;
	protected boolean dominated;
	
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
	
	public int getNumObjectives() {
		return obj.length;
	}

	public double getObjective(int pos) {
		return obj[pos];
	}

	public double[] getObjectives() {
		return obj;
	}
	
	public void setObjective(int pos, double objective) {
		this.obj[pos] = objective;
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
	
	public abstract Solution copy();
	
	public boolean isDominated() {
		return dominated;
	}

	public void setDominated(boolean dominated) {
		this.dominated = dominated;
	}
}
