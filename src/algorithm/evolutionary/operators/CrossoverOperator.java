package algorithm.evolutionary.operators;

import java.util.ArrayList;

import algorithm.evolutionary.solutions.Solution;

public interface CrossoverOperator <S extends Solution>{

	ArrayList <S> execute(ArrayList <S> parents);

}
