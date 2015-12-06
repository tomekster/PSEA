package operators;

import core.Population;
import core.Solution;

public interface SelectionOperator {
	public Solution execute(Population population);
}
