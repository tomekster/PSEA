package core;

import java.util.ArrayList;

public class Population{
	private ArrayList <Solution> solutions = null;
	
	public Population(){
		this.solutions = new ArrayList <Solution>();
	}
	
	public Population(Population pop) {
		this();
		for(Solution s : pop.getSolutions()){
			this.solutions.add(s.copy());
		}
	}

	public void addSolution(Solution solution){
		this.solutions.add(solution);
	}
	
	public void addSolutions(Population p){
		for(Solution s : p.getSolutions()){
			addSolution(s);
		}
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
		sb.append("[\n");
		for(Solution s: this.solutions){
			sb.append(s.toString());
			sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
}
