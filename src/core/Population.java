package core;

import java.util.ArrayList;

public class Population{
	private ArrayList <Solution> solutions = null;
	
	public Population(){
		this.solutions = new ArrayList <Solution>();
	}
	
	public Population(Population nextPopulation) {
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

	public Population copy() {
		return new Population(this);
	}
}
