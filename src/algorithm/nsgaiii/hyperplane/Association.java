package algorithm.nsgaiii.hyperplane;

import java.io.Serializable;

import algorithm.geneticAlgorithm.solutions.Solution;

public class Association implements Comparable <Association>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5366509349507913594L;
	private Solution solution;
	private double dist;
	
	public Association(Solution s, double dist) {
		this.solution = s;
		this.dist = dist;
	}
	public Solution getSolution(){
		return solution;
	}
	public void setSolution(Solution s){
		this.solution = s;
	}
	
	public double getDist(){
		return dist;
	}
	public void setDist(double dist){
		this.dist = dist;
	}
	
	@Override
	public String toString(){
		return "(" + this.solution + "\nDIST: " + this.dist + ")";
	}
	@Override
	public int compareTo(Association a2) {
		return Double.compare(this.getDist(), a2.getDist());
	}
}
