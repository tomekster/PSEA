package preferences;

import javax.swing.JOptionPane;

import core.Population;
import core.points.Solution;
import solutionRankers.ChebyshevRanker;
import utils.Geometry;
import utils.NSGAIIIRandom;
import utils.Pair;

public class Elicitator {

	public static void elicitate(Population firstFront, ChebyshevRanker decisionMakerRanker, PreferenceCollector PC) {
		Pair<Solution, Solution> p = getComparedSolutions(firstFront);
//		Pair<Solution, Solution> p = getComparedSolutions2(firstFront, PC.getComparisons().size());
		Solution s1 = p.first;
		Solution s2 = p.second;
		assert s1 != null;
		assert s2 != null;
				
		
		if (decisionMakerRanker != null) {
			int comparisonResult = decisionMakerRanker.compareSolutions(s1, s2);
			if (comparisonResult == -1) {
				PC.addComparison(s1, s2);
			} else if (comparisonResult == 1) {
				PC.addComparison(s2, s1);
			} else {
				System.out.println("Incomparable solutions - equal chebyshev function value");
			}
		} else {
			elicitateDialog(s1,s2,PC);
		}
	}

	private static Pair<Solution, Solution> getComparedSolutions(Population pop) {
		int i= NSGAIIIRandom.getInstance().nextInt(pop.size());
		int j = NSGAIIIRandom.getInstance().nextInt(pop.size()-1);
		if(j >= i) j++;
		return new Pair<Solution, Solution>(pop.getSolution(i), pop.getSolution(j));
	}
	
	private static Pair<Solution, Solution> getComparedSolutions2(Population pop, int numComparisons) {
		int numObjectives = pop.getSolution(0).getNumObjectives();
		double grad[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			if(i == numComparisons % (numObjectives - 1)){
				grad[i] = 1;
			}
			else grad[i] = 0;
		}
		
		Solution s1 = null, s2 = null;
		Geometry.mapOnParallelHyperplane(grad);
		
		double maxCos =  -Double.MAX_VALUE;
		for(int i=0; i < pop.size(); i++){
			for(int j=i+1; j < pop.size(); j++){
				double vect[] = Geometry.getVect(pop.getSolution(i).getObjectives(), pop.getSolution(j).getObjectives());
				double cosVal = Math.abs(Geometry.dot(grad, vect)) / (Geometry.getLen(grad) * Geometry.getLen(vect));
				
				if(cosVal > maxCos){
					maxCos = cosVal;
					s1 = pop.getSolution(i);
					s2 = pop.getSolution(j);
				}
			}
		}
		return new Pair<Solution, Solution>(s1, s2);
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

		PC.addComparison(s1, s2);
	}
}
