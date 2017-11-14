package algorithm.geneticAlgorithm.operators;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.solutions.Solution;

public interface CrossoverOperator <S extends Solution>{

	ArrayList <S> execute(ArrayList <S> parents);

}
