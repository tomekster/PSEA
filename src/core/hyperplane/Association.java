package core.hyperplane;

import core.Solution;

public class Association {
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
}
