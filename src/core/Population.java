package core;

import java.util.ArrayList;

public class Population{
	private ArrayList <Solution> solutions = null;
	
	public Population(){
		this.solutions = new ArrayList <Solution>();
	}
	
	public Population(Population nextPopulation) {
		this();
		for(Solution s : this.solutions){
			this.solutions.add(s.copy());
		}
	}

	public void addSolution(Solution solution){
		this.solutions.add(solution);
	}
	
	public Solution getSolution(int pos){
		return this.solutions.get(pos);
	}
	
	public ArrayList <Solution> getSolutions(){
		return this.solutions;
	}
	
	public int size(){
		return this.solutions.size();
	}
	public boolean empty(){
		return this.solutions.size() == 0;
	}

	public Population copy() {
		return new Population(this);
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(Solution s: this.solutions){
			sb.append(s.toString());
			sb.append(", ");
		}
		sb.replace(sb.length()-2, sb.length(),"]");
		return sb.toString();
	}
}
