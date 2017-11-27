package algorithm.implementations.psea.history;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;

public class GenerationSnapshot {

	private int generationId;
	private Population <? extends Solution> population;
	private ArrayList<AsfDm> lambdas;
	private Comparison c;
	
	public GenerationSnapshot(int genId, Population<? extends Solution> pop, ArrayList<AsfDm> lambdas){
		this.generationId = genId;
		this. population = pop;
		this.lambdas = lambdas;
	}

	public int getGenerationId() {
		return generationId;
	}

	public void setGenerationId(int generationId) {
		this.generationId = generationId;
	}

	public Population<? extends Solution> getPopulation() {
		return population;
	}

	public void setPopulation(Population<? extends Solution> population) {
		this.population = population;
	}

	public ArrayList<AsfDm> getLambdas() {
		return lambdas;
	}

	public void setLambdas(ArrayList<AsfDm> lambdas) {
		this.lambdas = lambdas;
	}

	public Comparison getComparison() {
		return c;
	}

	public void setComparison(Comparison c) {
		this.c = c;
	}

}
