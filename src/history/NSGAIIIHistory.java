package history;

import java.util.ArrayList;

import core.Population;

public class NSGAIIIHistory {
	
	private Population initialPopulation;
	private Population referencePoints;
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
	public Population getReferencePoints() {
		return referencePoints;
	}
	public void setReferencePoints(Population referencePoints) {
		this.referencePoints = referencePoints;
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
