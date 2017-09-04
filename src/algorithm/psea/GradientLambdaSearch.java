package algorithm.psea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ejml.simple.SimpleMatrix;

import algorithm.geneticAlgorithm.Solution;
import algorithm.psea.preferences.ASFBundle;
import algorithm.psea.preferences.Comparison;
import algorithm.psea.preferences.PreferenceCollector;
import algorithm.rankers.AsfRanker;
import utils.math.Geometry;
import utils.math.MyRandom;
import utils.math.Geometry.Line2D;
import utils.math.structures.Interval;
import utils.math.structures.Pair;

public class GradientLambdaSearch {
	private final static Logger LOGGER = Logger.getLogger(GradientLambdaSearch.class.getName());
	
	int numObjectives;
	SimpleMatrix M, Minv;
	
	public GradientLambdaSearch(int numObjectives) {
		this.numObjectives = numObjectives;
		M = new SimpleMatrix(Geometry.genHypTransMap(numObjectives));
		Minv = M.invert();
	}
	
	public double[] lambda2theta(double lambdaIn[]){
		double lambda[] = lambdaIn.clone();
		for(int i=0; i<numObjectives; i++){
			lambda[i] -= 1.0/numObjectives;
		}
		SimpleMatrix L = new SimpleMatrix(1, numObjectives, true, lambda);
		SimpleMatrix theta = L.mult(Minv);
		double resTheta[] = theta.getMatrix().getData();
		return resTheta;
	}
	
	public double[] theta2lambda(double theta[]){
		SimpleMatrix T = new SimpleMatrix(1, numObjectives, true, theta);
		SimpleMatrix lambda = T.mult(M);
		double resLambda[] = lambda.getMatrix().getData();
		for(int i=0; i<resLambda.length; i++){
			resLambda[i] += 1.0/numObjectives;
		}
		return resLambda;
	}
	
	void validateInterval(ASFBundle asfBundle, Interval interval, int CV){
			ArrayList <AsfPreferenceModel> lambdas = new ArrayList<>();
			double l1[] = interval.getL1();
			double l2[] = interval.getL2();
			lambdas.add(new AsfPreferenceModel( Geometry.linearCombination(l1, l2, 0.01 * interval.getBeg() + 0.99 * interval.getEnd())));
			lambdas.add(new AsfPreferenceModel( Geometry.linearCombination(l1, l2, 0.99 * interval.getBeg() + 0.01 * interval.getEnd())));
			lambdas.add(new AsfPreferenceModel( Geometry.linearCombination(l1, l2, (interval.getBeg() + interval.getEnd()) / 2)));
	
			//Make sure that both endpoints and middle of interval have the same CV value
			for(AsfPreferenceModel lambda : lambdas){
				int eval = asfBundle.evaluateLambda(lambda);
				if( eval != interval.getCV() || eval > CV){
					System.out.println("Error! CV values are different!");
					return;
				}
				assert eval == interval.getCV() && eval <= CV;
			}
	}
	
	private ArrayList <Interval> getImprovingIntervals(ASFBundle asfBundle, AsfPreferenceModel lambda, AsfPreferenceModel bestLambda) {
		ArrayList <Interval> intervals = new ArrayList<>();
		int numDim = lambda.getNumDimensions();
		double bestLambdaGrad[] = new double[numDim];
		
		//Get gradient from current lambda to bestLambda
		for(int i=0; i<numDim; i++){
			bestLambdaGrad[i] = lambda.getDim(i) - bestLambda.getDim(i);
		}
		
		assert( Math.abs( Arrays.stream(bestLambda.getDim()).sum() - 1) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(lambda.getDim()).sum() - 1) < Geometry.EPS );
				
		// Perform interval search only if gradient is non-zero
		if(Geometry.getLen(bestLambdaGrad) > Geometry.EPS){
			intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, lambda.getDim(), bestLambdaGrad));
		}
	
		//Additionally search for intervals on random direction from current lambda
		double randomGrad[] = Geometry.getRandomVectorOnHyperplane(numDim, 1);
		intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, lambda.getDim(),  randomGrad));
		
		//Search on all gradients where only two dimensions change - one increases and second decreases by exactly same value
		for(int i=0; i<numObjectives; i++){
			for(int j=i+1; j<numObjectives; j++){
				double grad[] = new double[numObjectives];
				grad[i]=1;
				grad[j]=-1;
				intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, lambda.getDim(), grad));
			}
		}
		//Search on all gradients where one dimensions increases and all other decreases
		for(int i=0; i<numObjectives; i++){
			double grad[] = new double[numObjectives];
			for(int j=0; j<numObjectives; j++){
				grad[j]=-1;
			}
			grad[i]=grad.length-1;
			intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, lambda.getDim(), grad));
		}
		
		//Evaluate lambda to make sure that CV value is up-to-date
		asfBundle.evaluateLambda(lambda);
		
		if(PSEA.assertions){
			for(Interval interval : intervals){
				validateInterval(asfBundle, interval, lambda.getNumViolations());
			}
		}
		
		return intervals;
	}

	private ArrayList<Interval> getBestIntervalsOnGradientLine(ASFBundle asfBundle, double[] lambda, double[] grad) {
		assert( Math.abs( Arrays.stream(lambda).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(grad).sum()) < Geometry.EPS );
		
		Pair <double[], double[]> simplexSegment = Geometry.getSimplexSegment(lambda, grad);
		double l1[] = simplexSegment.first, l2[] = simplexSegment.second;
		
		//Each pair is (t, +-(id+1)), where t represents "time" on segment l1, l2 counted from l1 to l2
		//while absolute value of id is the id value of comparison whose evaluation changes from "coherent" to "incoherent" when [lambda = (1-t) l1 + t l2]. 
		//Positive id indicates change from "incoherent" to "coherent" comparison, while negative id indicates opposite.
		ArrayList < Pair<Double, Integer> > switchPoints = getAllSwitchPoints(asfBundle, l1, l2);
		
		assert( Math.abs( Arrays.stream(l1).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(l2).sum() - 1 ) < Geometry.EPS );
		ArrayList <Interval > bestIntervals = findBestIntervals(asfBundle, switchPoints, l1, l2);
		
		return bestIntervals;
	}

	/**
	 * Assumes switchPoints are sorted ascending by time (first value in pair)
	 * Returns intervals in which CV reaches it's minimum value;
	 */
	public ArrayList <Interval> findBestIntervals(ASFBundle asfBundle, ArrayList < Pair<Double, Integer> > switchPoints, double[] l1, double[] l2) {
		int CV=0, bestCV = Integer.MAX_VALUE;
		double bestBeg, bestEnd;
		ArrayList <Interval> bestIntervals = new ArrayList<>();
		assert switchPoints.get(0).first == 0;
		
		Set <Integer> violatedComparisons = new HashSet<>();
		
		//Consider switch points in increasing order by t
		int i = 0;
		while( i<switchPoints.size() ){
			Pair<Double, Integer> p = switchPoints.get(i);
			assert p.first >=0 && p.first <= 1;
			assert p.second != 0;
		
			//Process all switch points with same time t at once
			while( i<switchPoints.size() && Double.compare(switchPoints.get(i).first, p.first) == 0 ){
				int comparisonID = switchPoints.get(i).second;
				if(comparisonID < 0){
					CV++;
					violatedComparisons.add(-comparisonID);
				}
				if(comparisonID > 0 && violatedComparisons.contains(comparisonID)) CV--;
				i++;
			}
			if(CV < 0){
				System.out.println("NEGATIVE_CV_ERROR");
			}
			assert CV >= 0;
			
			//If given starting point of interval has best Constraint Violation value, add interval to result 
			if(CV <= bestCV){
				if(CV < bestCV){
					bestIntervals.clear(); 
				}
				bestCV = CV;
				bestBeg = p.first;
				bestEnd = i < switchPoints.size() ? Double.min(switchPoints.get(i).first, 1) : 1;
				bestIntervals.add(new Interval(bestBeg, bestEnd, CV, l1, l2));
				
				if(PSEA.assertions){
					double middleLambda[] = Geometry.linearCombination(l1, l2, (bestBeg + bestEnd)/2);
					int eval = asfBundle.evaluateLambda(new AsfPreferenceModel(middleLambda));
					if(eval != CV){
						System.out.println("MIDDLE_CV_DIFFERS");
						System.out.println(eval + " != " + CV);
						System.out.println("Interval: (" + bestBeg + ", " + bestEnd + ")" );
						if(eval != CV){
							System.out.println("ERROR! Wrong CV!");
						}
					}
				}
			}
		}
		
		return bestIntervals;
	}
	
	/**
	 * 
	 * @param l1 - first endpoint of lambda segment in user preference models space (standard n-dimensional simplex)
	 * @param l2 - second endpoint of lambda segment in user preference models space (standard n-dimensional simplex)
	 * @return list of pairs (alpha, +/- (id+1) ). 
	 * Alpha corresponds to value from range [0,1], at which pair of solutions (a, b) 
	 * compared by user becomes indistinguishable i. e. both solutions a and b evaluated through ASF function using 
	 * model given by lambda = ( (1-alpha) * l1 + alpha * l2) have the same value. 
	 * ID corresponds to id of comparison (a,b) on PreferenceCollector.comparisons list. 
	 * ID with positive sign indicates that when alpha changes from (alpha - epsilon) to (alpha + epsilon) comparison 
	 * "a is better than b" changes from "unsatisfied" to "satisfied" or "not reproduced by lambda(alpha)" to "reproduced by lambda(alpha)"
	 * while negative value of ID indicates opposite change from "satisfied" to "unsatisfied".  
	 */
	protected ArrayList<Pair<Double, Integer>> getAllSwitchPoints(ASFBundle asfBundle, double l1[], double l2[]) {
		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
		for(int cpId = 0; cpId < PreferenceCollector.getInstance().getComparisons().size(); cpId++){
			Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
			
			ArrayList <Line2D> lines = getLines(asfBundle, cp.getBetter(), cp.getWorse(), l1, l2); 
			ArrayList <Line2D> upperEnvelope = Geometry.linesUpperEnvelope(lines);

			//Check comparison for alpha=0 (linearCombination(lambda1, lambda2, 0) = lambda1) to properly initialize switches array
			int zeroComparison = AsfRanker.compareSolutions(cp.getBetter(), cp.getWorse(), asfBundle.getReferencePoint(), l1);
			if(zeroComparison != 0){ res.add(new Pair<Double, Integer>(.0, -zeroComparison*(cpId+1)));}
			addComparisonSwitchPoints(asfBundle, res, upperEnvelope, l1, l2, cpId, lines);
		}
		Collections.sort(res, new Comparator<Pair<Double, Integer>>() {
			@Override
			public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
				return Double.compare(o1.first, o2.first) == 0 ? 
						Double.compare(o1.second, o2.second) : Double.compare(o1.first, o2.first);
			}
		});
		return res;
	}

	private void addComparisonSwitchPoints(ASFBundle asfBundle, ArrayList<Pair<Double, Integer>> res, ArrayList<Line2D> upperEnvelope, double[] l1, double[] l2, int cpId, ArrayList<Line2D> lines) {
		for(int i=1; i<upperEnvelope.size(); i++){
			Line2D line1 = upperEnvelope.get(i-1);
			Line2D line2 = upperEnvelope.get(i);
			if( line1.isBetter() ^ line2.isBetter() ){
				double crossX = line1.crossX(line2);
				if(Double.compare(crossX,0) < 0 || Double.compare(crossX, 1) > 0 ) continue;
				if(line2.isBetter()){ res.add(new Pair<Double, Integer>(crossX, -(cpId+1))); }
				else{ res.add(new Pair<Double, Integer>(crossX, cpId+1)); }
				
				if(PSEA.assertions){
					double lambda[] = Geometry.linearCombination(l1, l2, crossX);
					Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
					double M1 = AsfRanker.eval(cp.getBetter(), asfBundle.getReferencePoint(), lambda);
					double M2 = AsfRanker.eval(cp.getWorse(), asfBundle.getReferencePoint(), lambda);
					if(  Math.abs(M1-M2) > Geometry.EPS ){
						 System.out.println("Error! Crossing point wasn't computed correctly!");
					}
				}
			}
		}
	}

	private ArrayList<Line2D> getLines(ASFBundle asfBundle, Solution better, Solution worse, double[] l1, double[] l2) {
		ArrayList <Line2D> lines = new ArrayList<>();
		double[] betterMinusRef = new double[numObjectives];
		double[] worseMinusRef = new double[numObjectives];
		
		for(int i=0; i < numObjectives; i++){
			betterMinusRef[i] = better.getObjective(i) - asfBundle.getReferencePoint()[i];
			worseMinusRef[i]  = worse.getObjective(i) - asfBundle.getReferencePoint()[i];
		}
		
		double sumB1 = Geometry.dot(l1, betterMinusRef);
		double sumB2 = Geometry.dot(l2, betterMinusRef);
		double sumW1 = Geometry.dot(l1, worseMinusRef);
		double sumW2 = Geometry.dot(l2, worseMinusRef);
		double rho = AsfRanker.getRho();
		
		for(int i=0; i<numObjectives; i++){
			lines.add(new Line2D(betterMinusRef[i] * (l2[i] - l1[i]) + rho * (sumB2 - sumB1), betterMinusRef[i] * l1[i] + rho * sumB1, true ) );
			lines.add(new Line2D(worseMinusRef[i] * (l2[i] - l1[i]) + rho * (sumW2 - sumW1), worseMinusRef[i] * l1[i] + rho * sumW1, false ) );
		}
		return lines;
	}
	
	public ArrayList <AsfPreferenceModel> improveLambdas(ASFBundle asfBundle) {
		ArrayList<AsfPreferenceModel> lambdas = asfBundle.getLambdas();
		for(AsfPreferenceModel lambda : lambdas) asfBundle.evaluateLambda(lambda);
		AsfPreferenceModel bestLambda = lambdas.stream().min(Comparator.comparing(AsfPreferenceModel::getNumViolations)).get();
		LOGGER.log(Level.INFO, "Best lambda CV: " + bestLambda.getNumViolations());
		ArrayList <Interval> intervals = new ArrayList<>();
		for(AsfPreferenceModel lambda : lambdas){
			intervals.addAll(getImprovingIntervals(asfBundle, lambda, bestLambda));	
		}
		return chooseBestLambdaSubset(intervals, lambdas.size());
		
	}

	private ArrayList<AsfPreferenceModel> chooseBestLambdaSubset(ArrayList<Interval> intervals, int N) {
		Collections.sort(intervals);
		int bestCV = intervals.get(0).getCV();
		
		ArrayList <Interval> bestIntervals = new ArrayList<>();
		for(Interval i : intervals){
			if(i.getCV() == bestCV) bestIntervals.add(i);
			else break;
		}
		
		//TODO - for now pick random interval and random point, later it can be optimized for maximizing diversity
		ArrayList <AsfPreferenceModel> res = new ArrayList<>();
		LOGGER.log(Level.INFO, "Best interval CV: " + bestCV);
		for(int i=0; i<N; i++){
			int id = MyRandom.getInstance().nextInt( bestIntervals.size());
			Interval interval = bestIntervals.get(id);
			double t = MyRandom.getInstance().nextDouble();
			double dim[] = Geometry.linearCombination(interval.getL1(), interval.getL2(), (1-t)*interval.getBeg() + t * interval.getEnd());
			res.add(new AsfPreferenceModel(dim));
		}
		return res;
	}
}
