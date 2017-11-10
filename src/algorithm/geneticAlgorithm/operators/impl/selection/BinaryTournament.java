package algorithm.geneticAlgorithm.operators.impl.selection;

import java.util.Comparator;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.operators.SelectionOperator;
import algorithm.geneticAlgorithm.solutions.Solution;
import utils.math.MyRandom;

public class BinaryTournament implements SelectionOperator {
	
	private Comparator <Solution> comparator;
	
	public BinaryTournament(Comparator <Solution> comparator){
		this.comparator = comparator;
	}
	
	public Solution execute(Population population){
		MyRandom random = MyRandom.getInstance();
		
		int pos1 = random.nextInt(population.size());
		int pos2 = random.nextInt(population.size());
		Solution candidate1 = new Solution(population.getSolution(pos1));
		Solution candidate2 = new Solution(population.getSolution(pos2));
		
		int flag = comparator.compare(candidate1, candidate2);
		if(flag < 0) return candidate1;
		else if(flag > 0) return candidate2;
		else {
			return random.nextDouble() < 0.5 ? candidate1 : candidate2;
		}
	}
}