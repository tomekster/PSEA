package operators;

import java.util.ArrayList;
import core.Solution;

public interface CrossoverOperator {

	ArrayList <Solution> execute(ArrayList <Solution> parents);

}
