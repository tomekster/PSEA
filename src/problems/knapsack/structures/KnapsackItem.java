package problems.knapsack.structures;

public class KnapsackItem {
	private int id;
	private int weight;
	private int profit;
	
	public KnapsackItem(int id, int w, int p) {
		this.id = id;
		weight = w;
		profit = p;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id  = id;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public int getProfit(){
		return profit;
	}
}
