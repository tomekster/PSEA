package preferences;

import javax.swing.JOptionPane;

import core.Population;
import core.points.Solution;
import solutionRankers.ChebyshevRanker;
import utils.NSGAIIIRandom;

public class Elicitator {

	public static void elicitate(Population firstFront, ChebyshevRanker decisionMakerRanker, PreferenceCollector PC) {
		Solution s1 = null, s2 = null;
		int i,j;
		i= NSGAIIIRandom.getInstance().nextInt(firstFront.size());
		j = NSGAIIIRandom.getInstance().nextInt(firstFront.size()-1);
		if(j >= i) j++;
		
		s1 = firstFront.getSolution(i);
		s2 = firstFront.getSolution(j);

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
