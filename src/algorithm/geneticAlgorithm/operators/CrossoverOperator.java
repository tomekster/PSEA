package algorithm.geneticAlgorithm.operators;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.Solution;

public interface CrossoverOperator {

	ArrayList <Solution> execute(ArrayList <Solution> parents);

}
