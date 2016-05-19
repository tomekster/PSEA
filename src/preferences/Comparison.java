package preferences;

import core.Solution;

public class Comparison {
	private Solution better, worse;

	public Comparison(Solution a, Solution b) {
		better = a;
		worse = b;
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
}
