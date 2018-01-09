package algorithm.implementations.psea.preferences;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import javax.swing.JOptionPane;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.ReferencePointDm;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.psea.ASFBundle;
import utils.math.MyRandom;
import utils.math.structures.Pair;

public class Elicitator {
	
	//private final static Logger LOGGER = Logger.getLogger(Elicitator.class.getName());
	
	public static int elicitate(Population <? extends Solution> pop, ReferencePointDm adm, ASFBundle asfBundle, Pair<Solution, Solution> p) {
		return getComparedSolutions(pop, asfBundle, p);
	}

	public static Comparison compare(ReferencePointDm adm, Solution s1, Solution s2, int gen) {		
		if (adm != null) {
			int comparisonResult = adm.compare(s1, s2);
			if (comparisonResult == -1) {
				return new Comparison(s1, s2, gen);
			} else if (comparisonResult == 1) {
				return new Comparison(s2, s1, gen);
			} else {
				//TODO - add epsilon for incomparable solutions
				System.out.println(s1 + "\n" + s2 );
				System.out.println(adm.eval(s1) + " " + adm.eval(s2));
				System.out.println("Incomparable solutions - equal chebyshev function value");
				return null;
			}
		} else {
			return elicitateDialog(s1,s2, gen);
		}
	}
	
	private static Comparison elicitateDialog(Solution s1, Solution s2, int gen) {
		Object[] options = { "A: " + s1.objs(), "B: " + s2.objs() };
		int n = JOptionPane.showOptionDialog(null, "Which Solution do you prefer?", "Compare solutions",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

		if (n == 0) {
			return new Comparison(s1, s2, gen);
		} else {
			return new Comparison(s2, s1, gen);
		}
	}

	private static Pair<Integer, Integer> getRandomIds(int size) {
		int i= MyRandom.getInstance().nextInt(size);
		int j = MyRandom.getInstance().nextInt(size-1);
		if(j >= i) j++;
		return new Pair<Integer, Integer>(i, j);
	}
	
	private static int getComparedSolutions(Population <? extends Solution> pop, ASFBundle asfBundle, Pair <Solution, Solution> p) {
		double maxMinDif = -1;
		int maxSplit = -1, id1=-1, id2=-1;
		
		//Evaluate all solutions by all lambdas
		double solutionsLambdasEvals[][] = new double[pop.size()][asfBundle.getAsfDMs().size()];
		for(int i=0; i<pop.size(); i++){
			for( int j=0; j<asfBundle.getAsfDMs().size(); j++){
				solutionsLambdasEvals[i][j] = asfBundle.getAsfDMs().get(j).eval(pop.getSolution(i));
			}
		}
		int numObjectives = pop.getSolution(0).getNumObjectives();
		
		//For every pair of solutions determine how many lambdas consider first lambda better and how many consider second lambda better
		for(int i=0; i<pop.size(); i++){
			for(int j=i+1; j<pop.size(); j++){
				int score1=0, score2=0;
				Solution s1 = pop.getSolution(i), s2 = pop.getSolution(j);
				
				for(int k = 0; k < asfBundle.getAsfDMs().size(); k++){
					if(solutionsLambdasEvals[i][k] < solutionsLambdasEvals[j][k]) score1++;
					else if(solutionsLambdasEvals[i][k] > solutionsLambdasEvals[j][k]) score2++;
				}
				int split = Math.min(score1, score2);		
				
				double dif[] = new double[numObjectives];
				for(int k=0; k<numObjectives; k++){
					dif[k] = Math.abs(s1.getObjective(k) - s2.getObjective(k));
				}
				double minDif = DoubleStream.of(dif).min().getAsDouble();
				
				//maxSplit = min(|L_ij|, |U_ij|), where L_ij - set of lambdas that evaluate 
				//s_i as bettern than s_j, and U_ij - set of lambdas that evaluate s_i as worse than s_j
				if( (split==maxSplit && minDif > maxMinDif) || split > maxSplit ){
					id1 = i;
					id2 = j;
					maxSplit = split;
					maxMinDif = minDif;
				}
			}
		}
		Pair <Integer, Integer> pi = null;
		if(maxSplit == 0){
			 pi =  getRandomIds(pop.size());
		}
		else {
			pi = new Pair<Integer, Integer>(id1, id2);
		}
		p.first  = (Solution) pop.getSolution(pi.first).copy();
		p.second = (Solution) pop.getSolution(pi.second).copy();
		return maxSplit;
	}
}
