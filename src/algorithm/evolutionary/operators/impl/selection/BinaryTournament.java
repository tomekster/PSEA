package algorithm.evolutionary.operators.impl.selection;

import java.util.Comparator;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.operators.SelectionOperator;
import utils.math.MyRandom;

public class BinaryTournament implements SelectionOperator{
	
	private Comparator <Solution> comparator;
	
	public BinaryTournament(Comparator <Solution> comparator){
		this.comparator = comparator;
	}
	
	public Solution execute(Population <? extends Solution> population){
		MyRandom random = MyRandom.getInstance();
		
		int pos1 = random.nextInt(population.size());
		int pos2 = random.nextInt(population.size());
		Solution candidate1 = (Solution) population.getSolution(pos1).copy();
		Solution candidate2 = (Solution) population.getSolution(pos2).copy();
		
		int flag = comparator.compare(candidate1, candidate2);
		if(flag < 0) return candidate1;
		else if(flag > 0) return candidate2;
		else {
			return random.nextDouble() < 0.5 ? candidate1 : candidate2;
		}
	}
}