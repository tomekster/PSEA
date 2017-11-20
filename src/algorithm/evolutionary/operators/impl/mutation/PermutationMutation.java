package algorithm.evolutionary.operators.impl.mutation;

import algorithm.evolutionary.solutions.VectorSolution;
import algorithm.evolutionary.operators.MutationOperator;
import utils.math.MyRandom;

public class PermutationMutation implements MutationOperator <VectorSolution<Integer>>{

	@Override
	public void execute(VectorSolution <Integer> s) {
		Integer variables[] = new Integer[s.getNumVariables()];
		for(int i=0; i<variables.length; i++) variables[i] = -1;
		int pos1 = MyRandom.getInstance().nextInt(s.getNumVariables());
		int pos2 = MyRandom.getInstance().nextInt(s.getNumVariables());
		System.out.println(pos1 + " " + pos2);
		
		variables[pos2] = s.getVariable(pos1);
		
		int pos = 0;
		for(int i=0; i<s.getNumVariables(); i++){
			if(i == pos1) continue;
			if(pos == pos2) pos++;
			variables[pos++] =  s.getVariable(i);
		}
		s.setVariables(variables);
	}
}
