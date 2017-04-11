package preferences;

import java.io.Serializable;

import core.points.Solution;

public class Comparison implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3596108190287135380L;
	private Solution better, worse;
	private double maxEpsilon, minEpsilon;
	
	public Comparison(Solution a, Solution b, double maxEps, double minEps) {
		better = a;
		worse = b;
		maxEpsilon = maxEps;
		minEpsilon = minEps;
	}

	public Solution getBetter() {
		return better;
	}

	public void setBetter(Solution better) {
		this.better = better;
	}

	public Solution getWorse() {
		return worse;
	}

	public void setWorse(Solution worse) {
		this.worse = worse;
	}

	public double getMaxEpsilon() {
		return maxEpsilon;
	}

	public void setMaxEpsilon(double maxEpsilon) {
		this.maxEpsilon = maxEpsilon;
	}

	public double getMinEpsilon() {
		return minEpsilon;
	}

	public void setMinEpsilon(double minEpsilon) {
		this.minEpsilon = minEpsilon;
	}
	
	@Override
	public String toString(){
		return this.better + "\n>\n" + this.worse;
	}
}
