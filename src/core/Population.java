package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import core.points.Solution;
import utils.Geometry;

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

	public void addSolution(Solution sol){
		this.solutions.add(sol);
	}
	
	/**
	 * Add all points from population p to give Population. Deepcopy.
	 * @param p
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
	public boolean empty(){
		return this.solutions.size() == 0;
	}
	
	/**
	 * 
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

	public void removeSolutions(Population solutionsToRemove) {
		for (Iterator<Solution> it = solutions.iterator(); it.hasNext();){
			Solution s = it.next();
			if(Arrays.asList(solutionsToRemove).contains(s)){
				it.remove();
			}
		}
	}
	
	public double spread() {
		double maxDist = 0;
		for(Solution s1 : solutions){
			for(Solution s2 : solutions) maxDist = Double.max(maxDist, Geometry.euclideanDistance(s1.getObjectives(), s2.getObjectives()));
		}
		return maxDist;
	}
}
