package operators;

import core.Population;
import core.points.Solution;

public interface SelectionOperator {
	public Solution execute(Population population);
}
