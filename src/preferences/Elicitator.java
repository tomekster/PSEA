package preferences;

import java.util.stream.DoubleStream;

import javax.swing.JOptionPane;

import core.Lambda;
import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
import solutionRankers.ChebyshevRanker;
import utils.Geometry;
import utils.NSGAIIIRandom;
import utils.Pair;

public class Elicitator {

	public static boolean elicitate(Population firstFront, ChebyshevRanker decisionMakerRanker, Lambda lambda, boolean pairsUsed[][]) {
//		Pair<Solution, Solution> p = getComparedSolutions(firstFront);
//		Pair<Solution, Solution> p = getComparedSolutions2(firstFront, PC.getComparisons().size());
		Pair<Integer, Integer> p = getComparedSolutions3(firstFront, lambda, pairsUsed);
//		if(p.first==-1 && p.second==-1) return false;
		pairsUsed[p.first][p.second] = true;
		
		Solution s1 = firstFront.getSolution(p.first);
		Solution s2 = firstFront.getSolution(p.second);
//		System.out.println(s1 + " " + s2);
		PreferenceCollector PC = PreferenceCollector.getInstance();		
		
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
		return true;
	}

	private static Pair<Integer, Integer> getRandomIds(int size) {
		int i= NSGAIIIRandom.getInstance().nextInt(size);
		int j = NSGAIIIRandom.getInstance().nextInt(size-1);
		if(j >= i) j++;
		return new Pair<Integer, Integer>(i, j);
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
	
	private static Pair<Integer, Integer> getComparedSolutions3(Population pop, Lambda lambda, boolean pairsUsed[][]) {
		double maxSplit = -1;
		double maxMinDif = -1;
		int res1=-1,res2=-1,inc=-1;
		int id1=-1, id2=-1;
		
		for(int i=0; i<pop.size(); i++){
			for(int j=i+1; j<pop.size(); j++){
				if(pairsUsed[i][j]) continue;
				int score1=0, score2=0, incomparable=0;
				Solution s1 = pop.getSolution(i), s2 = pop.getSolution(j);
				
				for(ReferencePoint rp : lambda.getLambdas()){
					int comparison = ChebyshevRanker.compareSolutions(s1,s2, null, rp.getDim(), 0);
					if(comparison < 0) score1++;
					else if(comparison > 0) score2++;
					else incomparable++;
				}
				int split = Math.min(score1, score2);
				
				int numObjectives = s1.getNumObjectives();
				double dif[] = new double[numObjectives];
				for(int k=0; k<numObjectives; k++){
					dif[k] = Math.abs(s1.getObjective(k) - s2.getObjective(k));
				}
				double minDif = DoubleStream.of(dif).min().getAsDouble();
				
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
		System.out.println("final split:" + res1 + " " + res2 + " " + inc);
		if(maxSplit == 0) return getRandomIds(pop.size());
//		if(maxSplit == 0) return new Pair<Integer, Integer>(-1, -1);
		else return new Pair<Integer, Integer>(id1, id2);
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
