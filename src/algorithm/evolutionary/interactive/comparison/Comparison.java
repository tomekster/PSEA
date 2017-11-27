package algorithm.evolutionary.interactive.comparison;

import java.io.Serializable;

import algorithm.evolutionary.solutions.Solution;

public class Comparison implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3596108190287135380L;
	private Solution better, worse;
	private int generation;
	
	public Comparison(Solution a, Solution b, int gen) {
		better = a;
		worse = b;
		generation = gen;
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

	public int getGeneration(){
		return generation;
	}
	
	public void setGeneration(int gen){
		this.generation = gen;
	}
	
	@Override
	public String toString(){
		return this.better + "\n>\n" + this.worse;
	}
}
