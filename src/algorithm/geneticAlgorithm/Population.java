package algorithm.geneticAlgorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import algorithm.geneticAlgorithm.solutions.Solution;
import utils.math.Geometry;

public class Population <S extends Solution> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5522918817753869897L;
	private ArrayList <S> solutions = null;
	
	public Population(){
		this.solutions = new ArrayList <S>();
	}
	
	@SuppressWarnings("unchecked")
	public Population(Population <S> pop) {
		this();
		for(S s : pop.getSolutions()){
			this.solutions.add((S) s.copy());
		}
	}

	public Population(List<S> solList) {
		this.solutions = new ArrayList<>(solList);
	}

	/**
	 * Add solution sol to population.
	 * @param sol
	 */
	public void addSolution(S sol){
		this.solutions.add(sol);
	}
	
	/**
	 * Add all points from population pop to this Population. 
	 * This procedure adds ->deep copy<- of every solution in pop.
	 * @param pop
	 */
	public void addSolutions(Population <S> pop){
		for(S s : pop.getSolutions()){
			addSolution(s);
		}
	}
	
	public S getSolution(int pos){
		return this.solutions.get(pos);
	}
	
	public ArrayList <S> getSolutions(){
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
	public Population <S> copy() {
		return new Population <>(this);
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("[\n");
		for(S s: this.solutions){
			sb.append(s.toString());
			sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 
	 * @param point
	 * @param pop
	 * @return Computes average distance between point and solutions from pop (in objectives space)
	 */
	public static double getAvgDist(double point[], Population <Solution> pop){
		double avg = 0;
		for(Solution s : pop.getSolutions()){
			avg += Geometry.euclideanDistance(point, s.getObjectives());
		}
		return avg / pop.size();
	}
	
	/**
	 * @return Returns the Euclidean distance between two farthest solutions in given population pop.
	 */
	public static double maxDist(Population <Solution> pop) {
		double maxDist = 0;
		for(Solution s1 : pop.getSolutions()){
			for(Solution s2 : pop.getSolutions()) maxDist = Double.max(maxDist, Geometry.euclideanDistance(s1.getObjectives(), s2.getObjectives()));
		}
		return maxDist;
	}
}
