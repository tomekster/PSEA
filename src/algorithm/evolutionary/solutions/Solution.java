package algorithm.evolutionary.solutions;

import java.text.DecimalFormat;

import javax.management.RuntimeErrorException;

import utils.Copyable;

public class Solution implements Copyable{
	protected double[] obj;
	protected boolean dominated;
	
	public Solution(double [] obj){
		this.obj = obj.clone();
		setDominated(false);
	}
	
	public Solution(Solution s){
		this.obj = s.getObjectives().clone();
		setDominated(s.isDominated());
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

	//TODO
	@Override
	public Copyable copy() {
		throw new RuntimeErrorException(new Error(), "Should not be executed");
	}
}
