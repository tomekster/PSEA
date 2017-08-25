package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ejml.simple.SimpleMatrix;

import core.Lambda;
import core.algorithm.RST_NSGAIII;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import utils.Geometry.Line2D;

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
	
	void validateInterval(Interval interval, int CV){
			ArrayList <ReferencePoint> lambdaPoints = new ArrayList<>();
			double l1[] = interval.getL1();
			double l2[] = interval.getL2();
			lambdaPoints.add(new ReferencePoint(  Geometry.linearCombination(l1, l2, 0.01 * interval.getBeg() + 0.99 * interval.getEnd())));
			lambdaPoints.add(new ReferencePoint(  Geometry.linearCombination(l1, l2, 0.99 * interval.getBeg() + 0.01 * interval.getEnd())));
			lambdaPoints.add(new ReferencePoint(  Geometry.linearCombination(l1, l2, (interval.getBeg() + interval.getEnd()) / 2)));
	
			//Make sure that both endpoints and middle of interval have the same CV value
			for(ReferencePoint lambdaPoint : lambdaPoints){
				int eval = Lambda.evaluateLambdaPoint(lambdaPoint);
				if( Math.abs(eval - interval.getCV()) > 0 || eval > CV){
					System.out.println("ERROR");
					return;
				}
				assert eval == interval.getCV() && eval <= CV;
			}
	}
	
	private ArrayList <Interval> getImprovingIntervals(ReferencePoint lambdaPoint, ReferencePoint bestLambdaPoint) {
		ArrayList <Interval> intervals = new ArrayList<>();
		int numDim = lambdaPoint.getNumDimensions();
		double lambdaDirection[] = lambdaPoint.getDirection();
		double bestLambdaDirection[] = bestLambdaPoint.getDirection();
		double bestLambdaGrad[] = new double[numDim];
		
		assert( Math.abs( Arrays.stream(bestLambdaPoint.getDim()).sum() - 1) < Geometry.EPS );
		
		
		//Get gradient from current lambdaDirection to bestLambdaDirection
		for(int i=0; i<lambdaDirection.length; i++){
			bestLambdaGrad[i] = lambdaDirection[i] - bestLambdaDirection[i];
		}
		if(Math.abs( Arrays.stream(lambdaDirection).sum() - 1 ) >= Geometry.EPS){
			System.out.println("Point not summing to 1:" + Arrays.toString(lambdaDirection));
		}
		//TODO
		//assert( Math.abs( Arrays.stream(lambdaPoint).sum() - 1 ) < Geometry.EPS );		
		//assert Math.abs(Arrays.stream(bestLambdaGrad).sum()) < Geometry.EPS;
				
		// Perform interval search only if gradient is non-empty
		if(Geometry.getLen(bestLambdaGrad) > Geometry.EPS){
			intervals.addAll(getBestIntervalsOnGradientLine(lambdaDirection, bestLambdaGrad));
		}
	
		//Additionally search for intervals on random direction from current lambda
		double randomGrad[] = Geometry.getRandomVectorOnHyperplane(numDim, 1);
		assert Math.abs(Arrays.stream(randomGrad).sum()) < Geometry.EPS;
		intervals.addAll(getBestIntervalsOnGradientLine(lambdaDirection,  randomGrad));
		
		//Search on all gradients where only two dimensions change - one increases and second decreases by exactly same value
		for(int i=0; i<numObjectives; i++){
			for(int j=i+1; j<numObjectives; j++){
				double grad[] = new double[numObjectives];
				grad[i]=1;
				grad[j]=-1;
				intervals.addAll(getBestIntervalsOnGradientLine(lambdaDirection, grad));
			}
		}
		//Search on all gradients where one dimensions increases and all other decreases
		for(int i=0; i<numObjectives; i++){
			double grad[] = new double[numObjectives];
			for(int j=0; j<numObjectives; j++){
				grad[j]=-1;
			}
			grad[i]=grad.length-1;
			intervals.addAll(getBestIntervalsOnGradientLine(lambdaDirection, grad));
		}
		
		//Evaluate lambda to make sure that CV value is up-to-date
		Lambda.evaluateLambdaPoint(lambdaPoint);
		
		if(RST_NSGAIII.assertions){
			for(Interval interval : intervals){
				//TODO
				//Numerical errors
				if(Math.abs(interval.getBeg() - interval.getEnd()) > 1e-4){
					validateInterval(interval, lambdaPoint.getNumViolations());
				}
			}
		}
		
		return intervals;
	}

	private ArrayList<Interval> getBestIntervalsOnGradientLine(double[] lambdaDirection, double[] grad) {
		//TODO
//		assert( Math.abs( Arrays.stream(lambdaPoint).sum() - 1 ) < Geometry.EPS );
//		assert( Math.abs( Arrays.stream(grad).sum()) < Geometry.EPS );
		
		Pair <double[], double[]> simplexSegment = Geometry.getSimplexSegment(lambdaDirection, grad);
		double l1[] = simplexSegment.first, l2[] = simplexSegment.second;
		
		//Each pair is (t, +-(id+1)), where t represents "time" on segment l1, l2 counted from l1 to l2
		// while absolute value of id represents comparison id which changes when lambda crosses this point. Positive id indicates 
		//change from "not reproduced" to "reproduced" comparison, while negative id indicates opposite.
		ArrayList < Pair<Double, Integer> > switchPoints = getAllSwitchPoints(l1, l2);
		
		assert( Math.abs( Arrays.stream(l1).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(l2).sum() - 1 ) < Geometry.EPS );
		ArrayList <Interval > bestIntervals = findBestIntervals(switchPoints, l1, l2);
		
		return bestIntervals;
	}

	/**
	 * Assumes switchPoints are sorted ascending by time (first value in pair)
	 * Returns intervals in which CV reaches it's minimum value;
	 */
	public ArrayList <Interval> findBestIntervals(ArrayList < Pair<Double, Integer> > switchPoints, double[] l1, double[] l2) {
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
			//TODO - risky place - numerical errors 
			while( i<switchPoints.size() && Math.abs(switchPoints.get(i).first - p.first) < Geometry.EPS ){
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
			
//			double end = i < switches.size() ? Double.min(switches.get(i).first, 1) : 1;
//			System.out.println("i = " + i + " CV([" + p.first + ", " + end  + "]) = " + CV);
			
			//If given starting point of interval has best Constraint Violation value, add interval to result 
			if(CV <= bestCV){
				if(CV < bestCV){
					bestIntervals.clear(); 
				}
				bestCV = CV;
				bestBeg = p.first;
				bestEnd = i < switchPoints.size() ? Double.min(switchPoints.get(i).first, 1) : 1;
				bestIntervals.add(new Interval(bestBeg, bestEnd, CV, l1, l2));
				
				if(RST_NSGAIII.assertions){
					double direction[] = Geometry.linearCombination(l1, l2, (bestBeg + bestEnd)/2);
					ReferencePoint middleLambdaPoint = new ReferencePoint(Geometry.dir2point(direction));
					int eval = Lambda.evaluateLambdaPoint(middleLambdaPoint);
					if(Math.abs(bestBeg - bestEnd) > 1e-4 && eval != CV){
						System.out.println("MIDDLE_CV_DIFFERS");
						System.out.println(eval + " != " + CV);
						System.out.println("Interval: (" + bestBeg + ", " + bestEnd + ")" );
						assert eval == CV;
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
	 * model given by lambda = (alpha * l1 + (1-alpha) * l2) have the same value. 
	 * ID corresponds to id of comparison (a,b) on PreferenceCollector.comparisons list. 
	 * ID with positive sign indicates that when alpha changes from (alpha - epsilon) to (alpha + epsilon) comparison 
	 * "a is better than b" changes from "unsatisfied" to "satisfied" or "not reproduced by lambda(alpha)" to "reproduced by lambda(alpha)"
	 * while negative value of ID indicates opposite change from "satisfied" to "unsatisfied".  
	 */
	protected ArrayList<Pair<Double, Integer>> getAllSwitchPoints(double l1[], double l2[]) {
		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
		for(int cpId=0; cpId<PreferenceCollector.getInstance().getComparisons().size(); cpId++){
			Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
			
			ArrayList <Line2D> lines = getLines(cp.getBetter(), cp.getWorse(), l1, l2); 
			ArrayList <Line2D> upperEnvelope = Geometry.linesUpperEnvelope(lines);

			//Check comparison for alpha=0 (linearCombination(lambda1, lambda2, 0) = lambda2) to properly initialize switches array
			int zeroComparison = ChebyshevRanker.compareSolutions(cp.getBetter(), cp.getWorse(), null, l2);
			if(zeroComparison != 0){ res.add(new Pair<Double, Integer>(.0, -zeroComparison*(cpId+1)));}
			addComparisonSwitchPoints(res, upperEnvelope, l1, l2, cpId, lines);
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

	private void addComparisonSwitchPoints(ArrayList<Pair<Double, Integer>> res, ArrayList<Line2D> upperEnvelope, double[] l1, double[] l2, int cpId, ArrayList<Line2D> lines) {
		for(int i=1; i<upperEnvelope.size(); i++){
			Line2D line1 = upperEnvelope.get(i-1);
			Line2D line2 = upperEnvelope.get(i);
			if( line1.isBetter() ^ line2.isBetter() ){
				double crossX = line1.crossX(line2);
				if(Double.compare(crossX,0) < 0 || Double.compare(crossX, 1) > 0 ) continue;
				if(line2.isBetter()){ res.add(new Pair<Double, Integer>(crossX, -(cpId+1))); }
				else{ res.add(new Pair<Double, Integer>(crossX, cpId+1)); }
				
				if(RST_NSGAIII.assertions){
					double l1Point[] = Geometry.dir2point(l1);
					double l2Point[] = Geometry.dir2point(l2);
					double lambdaDirection[] = Geometry.invert(Geometry.linearCombination(l1Point, l2Point, crossX));
					Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
					double M1 = ChebyshevRanker.eval(cp.getBetter(), null, lambdaDirection);
					double M2 = ChebyshevRanker.eval(cp.getWorse(), null, lambdaDirection);
					if(  Math.abs(M1-M2) > Geometry.EPS ){
						 System.out.println("ERROR");
					}
				}
			}
		}
	}

	private ArrayList<Line2D> getLines(Solution better, Solution worse, double[] l1, double[] l2) {
		ArrayList <Line2D> lines = new ArrayList<>();
		for(int i=0; i<numObjectives; i++){
			lines.add(new Line2D(better.getObjective(i) * (l1[i] - l2[i]), better.getObjective(i) * l2[i], true ) );
			lines.add(new Line2D(worse.getObjective(i) * (l1[i] - l2[i]), worse.getObjective(i) * l2[i], false ) );
		}
		return lines;
	}
	
	public ArrayList <ReferencePoint> improveLambdaPoints(ArrayList<ReferencePoint> lambdaPointList) {
		for(ReferencePoint lambdaPoint : lambdaPointList) Lambda.evaluateLambdaPoint(lambdaPoint);
		ReferencePoint bestLambdaPoint = lambdaPointList.stream().min(Comparator.comparing(ReferencePoint::getNumViolations)).get();
		LOGGER.log(Level.INFO, "Best lambda CV: " + bestLambdaPoint.getNumViolations());
		ArrayList <Interval> intervals = new ArrayList<>();
		for(ReferencePoint rp : lambdaPointList){
			intervals.addAll(getImprovingIntervals(rp, bestLambdaPoint));	
		}
		return chooseBestLambdaSubset(intervals, lambdaPointList.size());
		
	}

	private ArrayList<ReferencePoint> chooseBestLambdaSubset(ArrayList<Interval> intervals, int N) {
		Collections.sort(intervals);
		int bestCV = intervals.get(0).getCV();
		
		ArrayList <Interval> bestIntervals = new ArrayList<>();
		for(Interval i : intervals){
			if(i.getCV() == bestCV) bestIntervals.add(i);
			else break;
		}
		
		//TODO
		//For now pick random interval and random point
		//Later it can be optimized for maximizing diversity
		ArrayList <ReferencePoint> res = new ArrayList<>();
		LOGGER.log(Level.INFO, "Best interval CV: " + bestCV);
		for(int i=0; i<N; i++){
			int id = NSGAIIIRandom.getInstance().nextInt( bestIntervals.size());
			Interval interval = bestIntervals.get(id);
			double t = NSGAIIIRandom.getInstance().nextDouble();
			double dim[] = Geometry.dir2point(Geometry.linearCombination(interval.getL1(), interval.getL2(), t*interval.getBeg() + (1-t) * interval.getEnd()));
			res.add(new ReferencePoint(dim));
		}
		return res;
	}
}
