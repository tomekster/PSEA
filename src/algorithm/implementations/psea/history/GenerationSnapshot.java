package algorithm.implementations.psea.history;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.AsfDM;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;

public class GenerationSnapshot {

	private int generationId;
	private Population <? extends Solution> population;
	private ArrayList<AsfDM> lambdas;
	private Comparison c;
	
	public GenerationSnapshot(int genId, Population<? extends Solution> pop, ArrayList<AsfDM> lambdas){
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

	public ArrayList<AsfDM> getLambdas() {
		return lambdas;
	}

	public void setLambdas(ArrayList<AsfDM> lambdas) {
		this.lambdas = lambdas;
	}

	public Comparison getComparison() {
		return c;
	}

	public void setComparison(Comparison c) {
		this.c = c;
	}

}
