package algorithm.geneticAlgorithm.solutions;

import java.text.DecimalFormat;

import utils.Copyable;

public abstract class Solution implements Comparable <Solution>, Copyable{
	protected double[] obj;
	protected boolean dominated;
	
	public Solution(double [] obj){
		this.obj = obj.clone();
		setDominated(false);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("OBJ: (");
		for (double d : obj) {
			sb.append(d);
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ")");
		return sb.toString();
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

	public boolean isDominated() {
		return dominated;
	}

	public void setDominated(boolean dominated) {
		this.dominated = dominated;
	}

	/**
	 * Returns 	1 if this solution dominates s2 
	 * 		   -1 if this solution is dominated by s2
	 * 		 	0 if solutions are incomparable
	 * (assuming minimization problem)
	 * 
	 * Both solutions have to be evaluated before running this method
	 */
	@Override
	public int compareTo(Solution s2) {
		boolean s1Dominates = false;
		boolean s2Dominates = false;
		
		for(int i=0; i<this.getNumObjectives(); i++){
			if(this.getObjective(i) < s2.getObjective(i)) s1Dominates = true;
			if(this.getObjective(i) > s2.getObjective(i)) s2Dominates = true;
		}
		
		if(s1Dominates == s2Dominates) return 0;
		else if(s1Dominates == true) return -1;
		else return 1;
	}
}
