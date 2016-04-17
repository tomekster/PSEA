package operators.impl.selection;

import core.Population;
import core.Solution;
import operators.SelectionOperator;
import utils.MyComparator;
import utils.NSGAIIIRandom;

public class BinaryTournament implements SelectionOperator {
	
	public Solution execute(Population population){
		NSGAIIIRandom random = NSGAIIIRandom.getInstance();
		
		int pos1 = random.nextInt(population.size());
		int pos2 = random.nextInt(population.size());
		Solution candidate1 = population.getSolution(pos1).copy();
		Solution candidate2 = population.getSolution(pos2).copy();
		
		MyComparator cp = new MyComparator();
		
		int flag = cp.compareDominance(candidate1, candidate2);
		if(flag == -1) return candidate1;
		else if(flag == 1) return candidate2;
		else {
			return random.nextDouble() < 0.5 ? candidate1 : candidate2;
		}
		
	}
}