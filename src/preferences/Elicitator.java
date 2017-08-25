package preferences;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;

import javax.swing.JOptionPane;

import core.Lambda;
import core.Population;
import core.points.Solution;
import solutionRankers.ChebyshevRanker;
import solutionRankers.NonDominationRanker;
import utils.NSGAIIIRandom;
import utils.Pair;

public class Elicitator {
	
	private final static Logger LOGGER = Logger.getLogger(Elicitator.class.getName());
	
	public static int elicitate(Population pop, ChebyshevRanker cr, Lambda lambda, Pair<Solution, Solution> p) {
//		We want to select for comparison only non-dominated solutions, therefore we consider only solutions from first front
		Population firstFront = NonDominationRanker.sortPopulation(pop).get(0);
		return firstFront.size() > 1 ? getComparedSolutions(firstFront, lambda, p) : -1;
	}

	public static void compare(ChebyshevRanker cr, Solution s1, Solution s2) {
		PreferenceCollector PC = PreferenceCollector.getInstance();		
		if (cr != null) {
			int comparisonResult = cr.compare(s1, s2);
			if (comparisonResult == -1) {
				PC.addComparison(s1, s2);
			} else if (comparisonResult == 1) {
				PC.addComparison(s2, s1);
			} else {
				//TODO - add epsilon for incomparable solutions
				System.out.println("Incomparable solutions - equal chebyshev function value");
			}
		} else {
			elicitateDialog(s1,s2,PC);
		}
	}

	private static Pair<Integer, Integer> getRandomIds(int size) {
		int i= NSGAIIIRandom.getInstance().nextInt(size);
		int j = NSGAIIIRandom.getInstance().nextInt(size-1);
		if(j >= i) j++;
		return new Pair<Integer, Integer>(i, j);
	}
	
	private static int getComparedSolutions(Population pop, Lambda lambda, Pair <Solution, Solution> p) {
		double maxMinDif = -1;
		int maxSplit = -1, res1=-1,res2=-1,inc=-1, id1=-1, id2=-1;
		
		//Evaluate all solutions by all lambdas
		double solutionsLambdasEvals[][] = new double[pop.size()][lambda.getLambdaPoints().size()];
		for(int i=0; i<pop.size(); i++){
			for( int j=0; j<lambda.getLambdaPoints().size(); j++){
				solutionsLambdasEvals[i][j] = ChebyshevRanker.eval(pop.getSolution(i), null, lambda.getLambdaPoints().get(j).getDirection());
			}
		}
		int numObjectives = pop.getSolution(0).getNumObjectives();
		
		//For every pair of solutions determine how many lambdas consider first lambda better and how many consider second lambda better
		for(int i=0; i<pop.size(); i++){
			for(int j=i+1; j<pop.size(); j++){
				int score1=0, score2=0, incomparable=0;
				Solution s1 = pop.getSolution(i), s2 = pop.getSolution(j);
				
				for(int k = 0; k<lambda.getLambdaPoints().size(); k++){
					if(solutionsLambdasEvals[i][k] < solutionsLambdasEvals[j][k]) score1++;
					else if(solutionsLambdasEvals[i][k] > solutionsLambdasEvals[j][k]) score2++;
					else incomparable++;
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
					res1 = score1;
					res2 = score2;
					inc = incomparable;
				}
			}
		}
		LOGGER.log(Level.INFO, "final split:" + res1 + " " + res2 + " " + inc);
		Pair <Integer, Integer> pi = null;
		if(maxSplit == 0){
			 pi =  getRandomIds(pop.size());
		}
//		if(maxSplit == 0) return new Pair<Integer, Integer>(-1, -1);
		else {
			pi = new Pair<Integer, Integer>(id1, id2);
		}
		p.first  = pop.getSolution(pi.first).copy();
		p.second = pop.getSolution(pi.second).copy();
		return maxSplit;
	}

	private static void elicitateDialog(Solution s1, Solution s2, PreferenceCollector PC) {
		Object[] options = { "A: " + s1.objs(), "B: " + s2.objs() };
		int n = JOptionPane.showOptionDialog(null, "Which Solution do youprefer?", "Compare solutions",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

		if (n == 0) {
			PC.addComparison(s1, s2);
		} else {
			PC.addComparison(s2, s1);
		}
//		PC.addComparison(s1, s2);
	}
}
