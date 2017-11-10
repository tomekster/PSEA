package algorithm.geneticAlgorithm.operators.impl.crossover;

import java.util.ArrayList;
import java.util.HashSet;

import algorithm.geneticAlgorithm.operators.CrossoverOperator;
import algorithm.geneticAlgorithm.solutions.Solution;
import utils.math.Geometry;
import utils.math.MyRandom;

public class PermutationCrossover implements CrossoverOperator <Integer>{

	@Override
	public ArrayList<Solution<Integer>> execute(ArrayList<Solution<Integer>> parents) {
		Solution <Integer> p1 = parents.get(0);
		Solution <Integer> p2 = parents.get(1);
		
		boolean mask[] = new boolean[p1.getNumVariables()];
		for(int i=0; i<mask.length; i++){
			mask[i] = MyRandom.getInstance().nextBoolean();
		}
		
		Solution <Integer> res1 = new Solution <Integer> (p1.getVariables(), p1.getObjectives());
		Solution <Integer> res2 = new Solution <Integer> (p2.getVariables(), p2.getObjectives());
		
		rearrange(res1.getVariables(), mask, p2.getVariables(), false);
		rearrange(res2.getVariables(), mask, p1.getVariables(), true);
		
		ArrayList<Solution <Integer> > res = new ArrayList<>();
		res.add(res1);
		res.add(res2);
		return res;
	}
	
	private void rearrange(Integer[] permutationToRearrange, boolean[] mask, Integer[] arrangement, boolean marker) {
		assert Geometry.isPermutation(permutationToRearrange);
		assert Geometry.isPermutation(arrangement);
		
		
		HashSet <Integer> valsToArrange = new HashSet<>();
		//Collect elements of permutation for which mask has value equal to marker
		for(int i=0; i<mask.length; i++){
			if(mask[i] == marker){
				valsToArrange.add((int) Math.round(permutationToRearrange[i]));
			}
		}
		
		//Next order those elements according to the order in which they appear in arrangement array
		int nummarkers = 0;
		for(boolean m : mask){
			if(m==marker)nummarkers++;
		}
		assert nummarkers == valsToArrange.size();
			
		int pos = 0;
		for(int i=0; i<permutationToRearrange.length; i++){
			if(mask[i] != marker) continue;
			while(pos < arrangement.length && !valsToArrange.contains((int) Math.round(arrangement[pos]))) pos++;
			permutationToRearrange[i] = arrangement[pos++];
		}
		assert Geometry.isPermutation(permutationToRearrange);
		
	}
}
