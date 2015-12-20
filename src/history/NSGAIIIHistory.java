package history;

import java.util.ArrayList;

import core.Population;

public class NSGAIIIHistory {
	
	private Population initialPopulation;
	private Population targetPoints;
	private ArrayList<Population> generations;
	
	public NSGAIIIHistory(int numGenerations){
		this.generations = new ArrayList<>(numGenerations);
	}
	
	public Population getInitialPopulation() {
		return initialPopulation;
	}
	public void setInitialPopulation(Population initialPopulation) {
		this.initialPopulation = initialPopulation;
	}
	public Population getTargetPoints() {
		return targetPoints;
	}
	public void setTargetPoints(Population targetPoints) {
		this.targetPoints = targetPoints;
	}
	public ArrayList<Population> getGenerations() {
		return generations;
	}
	public void setGenerations(ArrayList<Population> generations) {
		this.generations = generations;
	}
	public Population getGeneration(int pos){
		return generations.get(pos);
	}
	public void addGeneration(Population pop){
		this.generations.add(pop);
	}
	
}
