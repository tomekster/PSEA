package operators;

import java.util.ArrayList;

import core.points.Solution;

public interface CrossoverOperator {

	ArrayList <Solution> execute(ArrayList <Solution> parents);

}
