package algorithm.geneticAlgorithm.operators;

import java.util.ArrayList;

import algorithm.geneticAlgorithm.solutions.Solution;

public interface CrossoverOperator <T extends Number>{

	ArrayList <Solution <T> > execute(ArrayList <Solution <T> > parents);

}
