	package algorithm.implementations.psea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.ejml.simple.SimpleMatrix;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Solution;
import utils.math.Geometry;
import utils.math.Geometry.Line2D;
import utils.math.MyRandom;
import utils.math.structures.Interval;
import utils.math.structures.Pair;

public class GradientLambdaSearch {
	//private final static Logger LOGGER = Logger.getLogger(GradientLambdaSearch.class.getName());
	
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
	
	void validateInterval(ASFBundle asfBundle, Interval interval, int CV, ArrayList <Comparison> pc){
			ArrayList <AsfDm> asfDMs = new ArrayList<>();
			double l1[] = interval.getL1();
			double l2[] = interval.getL2();
			asfDMs.add( asfBundle.createAsfDm(Geometry.linearCombination(l1, l2, 0.01 * interval.getBeg() + 0.99 * interval.getEnd())));
			asfDMs.add( asfBundle.createAsfDm(Geometry.linearCombination(l1, l2, 0.99 * interval.getBeg() + 0.01 * interval.getEnd())));
			asfDMs.add( asfBundle.createAsfDm(Geometry.linearCombination(l1, l2, (interval.getBeg() + interval.getEnd()) / 2)));
	
			//Make sure that both endpoints and middle of interval have the same CV value
			for(AsfDm adm : asfDMs){
				int eval = adm.verifyModel(pc);
				if( eval != interval.getCV() || eval > CV){
					System.out.println("Error! CV values are different!");
					return;
				}
				assert eval == interval.getCV() && eval <= CV;
			}
	}
	
	private ArrayList <Interval> getImprovingIntervals(ASFBundle asfBundle, AsfDm adm, AsfDm bestAdm, ArrayList <Comparison> pc) {
		ArrayList <Interval> intervals = new ArrayList<>();
		int numDim = asfBundle.getReferencePoint().getNumDim();
		double bestLambdaGrad[] = new double[numDim];
		
		//Get gradient from current lambda to bestLambda
		for(int i=0; i<numDim; i++){
			bestLambdaGrad[i] = adm.getAsfFunction().getLambda(i) - bestAdm.getAsfFunction().getLambda(i);
		}
		
		assert( Math.abs( Arrays.stream(bestAdm.getAsfFunction().getLambda()).sum() - 1) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(adm.getAsfFunction().getLambda()).sum() - 1) < Geometry.EPS );
				
		// Perform interval search only if gradient is non-zero
		if(Geometry.getLen(bestLambdaGrad) > Geometry.EPS){
			intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, adm.getAsfFunction().getLambda(), bestLambdaGrad, pc));
		}
	
		//Additionally search for intervals on random direction from current lambda
		double randomGrad[] = Geometry.getRandomVectorOnHyperplane(numDim, 1);
		intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, adm.getAsfFunction().getLambda(),  randomGrad, pc));
		
		//Search on all gradients where only two dimensions change - one increases and second decreases by exactly same value
		for(int i=0; i<numObjectives; i++){
			for(int j=i+1; j<numObjectives; j++){
				double grad[] = new double[numObjectives];
				grad[i]=1;
				grad[j]=-1;
				intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, adm.getAsfFunction().getLambda(), grad, pc));
			}
		}
		//Search on all gradients where one dimensions increases and all other decreases
		for(int i=0; i<numObjectives; i++){
			double grad[] = new double[numObjectives];
			for(int j=0; j<numObjectives; j++){
				grad[j]=-1;
			}
			grad[i]=grad.length-1;
			intervals.addAll(getBestIntervalsOnGradientLine(asfBundle, adm.getAsfFunction().getLambda(), grad, pc));
		}
		
		//Evaluate lambda to make sure that CV value is up-to-date
		adm.verifyModel(pc);
		
		if(PSEA.assertions){
			for(Interval interval : intervals){
				validateInterval(asfBundle, interval, adm.getNumViolations(), pc);
			}
		}
		
		return intervals;
	}

	private ArrayList<Interval> getBestIntervalsOnGradientLine(ASFBundle asfBundle, double[] lambda, double[] grad, ArrayList <Comparison> pc) {
		assert( Math.abs( Arrays.stream(lambda).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(grad).sum()) < Geometry.EPS );
		
		Pair <double[], double[]> simplexSegment = Geometry.getSimplexSegment(lambda, grad);
		double l1[] = simplexSegment.first, l2[] = simplexSegment.second;
		
		//Each pair is (t, +-(id+1)), where t represents "time" on segment l1, l2 counted from l1 to l2
		//while absolute value of id is the id value of comparison whose evaluation changes from "coherent" to "incoherent" when [lambda = (1-t) l1 + t l2]. 
		//Positive id indicates change from "incoherent" to "coherent" comparison, while negative id indicates opposite.
		ArrayList < Pair<Double, Integer> > switchPoints = getAllSwitchPoints(asfBundle, l1, l2, pc);
		
		assert( Math.abs( Arrays.stream(l1).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(l2).sum() - 1 ) < Geometry.EPS );
		ArrayList <Interval > bestIntervals = findBestIntervals(asfBundle, switchPoints, l1, l2, pc);
		
		return bestIntervals;
	}

	/**
	 * Assumes switchPoints are sorted ascending by time (first value in pair)
	 * Returns intervals in which CV reaches it's minimum value;
	 */
	public ArrayList <Interval> findBestIntervals(ASFBundle asfBundle, ArrayList < Pair<Double, Integer> > switchPoints, double[] l1, double[] l2, ArrayList <Comparison> pc) {
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
					int eval = asfBundle.createAsfDm(middleLambda).verifyModel(pc);
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
	protected ArrayList<Pair<Double, Integer>> getAllSwitchPoints(ASFBundle asfBundle, double l1[], double l2[], ArrayList <Comparison> pc) {
		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
		for(int cpId = 0; cpId < pc.size(); cpId++){
			Comparison cp = pc.get(cpId);
			
			ArrayList <Line2D> lines = getLines(asfBundle, cp.getBetter(), cp.getWorse(), l1, l2); 
			ArrayList <Line2D> upperEnvelope = Geometry.linesUpperEnvelope(lines);

			//Check comparison for alpha=0 (linearCombination(lambda1, lambda2, 0) = lambda1) to properly initialize switches array
			AsfDm asfDM = asfBundle.createAsfDm(l1);
			int zeroComparison =asfDM.compare(cp.getBetter(), cp.getWorse());
			if(zeroComparison != 0){ res.add(new Pair<Double, Integer>(.0, -zeroComparison*(cpId+1)));}
			addComparisonSwitchPoints(asfBundle, res, upperEnvelope, l1, l2, cpId, lines, pc);
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

	private void addComparisonSwitchPoints(ASFBundle asfBundle, ArrayList<Pair<Double, Integer>> res, ArrayList<Line2D> upperEnvelope, double[] l1, double[] l2, int cpId, ArrayList<Line2D> lines, ArrayList <Comparison> pc) {
		for(int i=1; i<upperEnvelope.size(); i++){
			Line2D line1 = upperEnvelope.get(i-1);
			Line2D line2 = upperEnvelope.get(i);
			if( line1.isBetter() ^ line2.isBetter() ){
				double crossX = line1.crossX(line2);
				if(Double.compare(crossX,0) < 0 || Double.compare(crossX, 1) > 0 ) continue;
				if(line2.isBetter()){ res.add(new Pair<Double, Integer>(crossX, -(cpId+1))); }
				else{ res.add(new Pair<Double, Integer>(crossX, cpId+1)); }
				
				if(PSEA.assertions){
					AsfDm asfDM = asfBundle.createAsfDm(Geometry.linearCombination(l1, l2, crossX));
					Comparison cp = pc.get(cpId);
					double M1 = asfDM.eval(cp.getBetter());
					double M2 = asfDM.eval(cp.getWorse());
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
			betterMinusRef[i] = Math.abs(better.getObjective(i) - asfBundle.getReferencePoint().getDim(i));
			worseMinusRef[i]  = Math.abs(worse.getObjective(i) - asfBundle.getReferencePoint().getDim(i));
		}
		
		double sumB1 = Geometry.dot(l1, betterMinusRef);
		double sumB2 = Geometry.dot(l2, betterMinusRef);
		double sumW1 = Geometry.dot(l1, worseMinusRef);
		double sumW2 = Geometry.dot(l2, worseMinusRef);
		double rho = asfBundle.getAsfDmRho();
		
		for(int i=0; i<numObjectives; i++){
			lines.add(new Line2D(betterMinusRef[i] * (l2[i] - l1[i]) + rho * (sumB2 - sumB1), betterMinusRef[i] * l1[i] + rho * sumB1, true ) );
			lines.add(new Line2D(worseMinusRef[i] * (l2[i] - l1[i]) + rho * (sumW2 - sumW1), worseMinusRef[i] * l1[i] + rho * sumW1, false ) );
		}
		return lines;
	}
	
	public ArrayList <AsfDm> improvePreferenceModels(ASFBundle asfBundle, ArrayList <Comparison> pc) {
		ArrayList<AsfDm> asfDMs = asfBundle.getAsfDMs();
		for(AsfDm asfDM : asfDMs){
			asfDM.verifyModel(pc);
		}
		AsfDm bestLambda = asfDMs.stream().min(Comparator.comparing(AsfDm::getNumViolations)).get();
//		LOGGER.log(Level.INFO, "Best lambda CV: " + bestLambda.getNumViolations());
		ArrayList <Interval> intervals = new ArrayList<>();
		for(AsfDm lambda : asfDMs){
			intervals.addAll(getImprovingIntervals(asfBundle, lambda, bestLambda, pc));	
		}
		return chooseBestLambdaSubset(asfBundle, intervals);
		
	}

	private ArrayList<AsfDm> chooseBestLambdaSubset(ASFBundle asfBundle, ArrayList<Interval> intervals) {
		Collections.sort(intervals);
		int bestCV = intervals.get(0).getCV();
		
		ArrayList <Interval> bestIntervals = new ArrayList<>();
		for(Interval i : intervals){
			if(i.getCV() == bestCV) bestIntervals.add(i);
			else break;
		}
		
		//TODO - for now pick random interval and random point, later it can be optimized for maximizing diversity
		ArrayList <AsfDm> res = new ArrayList<>();
//		LOGGER.log(Level.INFO, "Best interval CV: " + bestCV);
		for(int i=0; i<asfBundle.size(); i++){
			int id = MyRandom.getInstance().nextInt( bestIntervals.size());
			Interval interval = bestIntervals.get(id);
			double t = MyRandom.getInstance().nextDouble();
			double lambda[] = Geometry.linearCombination(interval.getL1(), interval.getL2(), (1-t)*interval.getBeg() + t * interval.getEnd());
			res.add(asfBundle.createAsfDm(lambda));
		}
		return res;
	}
}
