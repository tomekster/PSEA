package algorithm.geneticAlgorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Population implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5522918817753869897L;
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

	public Population(List<Solution> solList) {
		this.solutions = new ArrayList<>(solList);
	}

	/**
	 * Add ->deep copy<- of solution sol to this population.
	 * @param sol
	 */
	public void addSolution(Solution sol){
		this.solutions.add(sol);
	}
	
	/**
	 * Add all points from population pop to this Population. 
	 * This procedure adds ->deep copy<- of every solution in pop.
	 * @param pop
	 */
	public void addSolutions(Population pop){
		for(Solution s : pop.getSolutions()){
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
	public boolean isEmpty(){
		return this.solutions.size() == 0;
	}
	
	/**
	 * @return shallow copy of population
	 */
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
