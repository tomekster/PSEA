package problems.knapsack;

import java.util.ArrayList;

public class Knapsack extends ArrayList<KnapsackItem>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1775003094012790346L;
	
	int id;
	double maxWeight;
	
	public Knapsack(int id, double maxWeight){
		this.id = id;
		this.maxWeight = maxWeight;
	}
	
//	//Returns pair <totalWeight, totalProfit>
//	public Pair<Integer, Integer> evaluate(Solution s){
//		int totalWeight = 0;
//		int totalProfit = 0;
//		for(int i=0; i<s.getNumVariables(); i++){
//			if(s.getVariable(i) > 0){
//				totalWeight += this.get(i).getWeight();
//				totalProfit += this.get(i).getProfit();
//			}
//		}
//		return new Pair<Integer, Integer>(totalWeight, totalProfit);
//	}
	
	public double getMaxWeight(){
		return this.maxWeight;
	}
}
