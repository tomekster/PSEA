package problems.knapsack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import problems.knapsack.structures.Knapsack;
import problems.knapsack.structures.KnapsackItem;

public class KnapsackProblemBuilder {
	
	public int readInteger(BufferedReader br) throws NumberFormatException, IOException{
		return Integer.parseInt(br.readLine().trim());
	}
	
	public double readDouble(BufferedReader br) throws NumberFormatException, IOException{
		return Double.parseDouble(br.readLine().trim());
	}
	
	public int[] readIntegerArray(BufferedReader br) throws NumberFormatException, IOException{
		String strNums[] = br.readLine().split("\\s");
		int nums[] = new int[strNums.length];
		for(int i=0; i < strNums.length; i++){
            nums[i] = Integer.parseInt(strNums[i]);
		}
		return nums;
	}
	
	public KnapsackProblemInstance readFile(int numItems, int numKnapsacks){
		String filename = "parsed_knapsack." + numItems + "." + numKnapsacks;
		KnapsackProblemInstance kpi = new KnapsackProblemInstance(numItems, numKnapsacks, 0, filename);

		try(BufferedReader br = new BufferedReader(new FileReader(Paths.get("benchmarks/knapsack/probleminstance", filename).toFile()))) {
			int nk = readInteger(br);
			int ni = readInteger(br);
			
		    assert numKnapsacks == nk;
		    assert numItems == ni;
		    
		    for(int i=0; i<numKnapsacks; i++){
		    	int knapsackId  = readInteger(br);
		    	double knapsackMaxWeight = readDouble(br);
		    	Knapsack k = new Knapsack(knapsackId, knapsackMaxWeight);
		    	
		    	int itemIds[] = readIntegerArray(br);
		    	int itemWeights[] = readIntegerArray(br);
		    	int itemProfits[] = readIntegerArray(br);
		    	
		    	for(int j=0; j<numItems; j++){
		    		KnapsackItem ki = new KnapsackItem(itemIds[j], itemWeights[j], itemProfits[j]);
		    		k.add(ki);
		    	}
		    	
		    	kpi.getKnapsacks().add(k);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	    
		return kpi;
	}
	
	public static void main(String arg[]){
		KnapsackProblemBuilder kr = new KnapsackProblemBuilder();
		kr.readFile(100, 2);
	}
}
