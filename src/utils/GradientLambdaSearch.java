package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.ejml.simple.SimpleMatrix;

import core.Lambda;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import utils.Geometry.Line2D;

public class GradientLambdaSearch {
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
	
	private ReferencePoint improve(ReferencePoint lambda, ReferencePoint bestLambda) {
		double allUnsatisfiedGrad[] = getTotalPCGradient(lambda);
		
		double bestLambdaGrad[] = new double[lambda.getNumDimensions()];
		assert( Math.abs( Arrays.stream(lambda.getDim()).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(bestLambda.getDim()).sum() - 1 ) < Geometry.EPS );
		for(int i=0; i<lambda.getNumDimensions(); i++){
			bestLambdaGrad[i] = lambda.getDim(i) - bestLambda.getDim(i);
		}
		
//		double neigh[] = Geometry.getRandomNeighbour(lambda.getDim(), 1.0);
//		double grad[] = new double[numObjectives];
//		for(int i=0; i<numObjectives; i++){
//			grad[i] = neigh[i] - lambda.getDim(i);
//		}
		
		ReferencePoint res1 = new ReferencePoint(lambda.getNumDimensions());
		ReferencePoint res2 = new ReferencePoint(lambda.getNumDimensions());
		
		// If gradient is empty (for example lambda reproduces all comparisons) then no improvement is needed
		if(Geometry.getLen(allUnsatisfiedGrad) < Geometry.EPS){
			res1.setNumViolations(Integer.MAX_VALUE);
		}
		else{
			res1 = getBestOnGradientLine(lambda, allUnsatisfiedGrad);
		}
		
		if(Geometry.getLen(bestLambdaGrad) < Geometry.EPS){
			res2.setNumViolations(Integer.MAX_VALUE);
		}
		else{
			res2 = getBestOnGradientLine(lambda, bestLambdaGrad);
		}
		
		ArrayList<ReferencePoint> lambdas = new ArrayList<>();
		lambdas.add(res1);
		lambdas.add(res2);
		lambdas.add( getBestOnGradientLine(lambda, Geometry.getRandomVectorOnHyperplane(lambda.getNumDimensions(), 1)) );
		
		for(int i=0; i<numObjectives; i++){
			for(int j=i+1; j<numObjectives; j++){
				double grad[] = new double[numObjectives];
				grad[i]=1;
				grad[j]=-1;
				lambdas.add(getBestOnGradientLine(lambda, grad));
			}
		}
		
//		ReferencePoint res = lambdas.stream().min(Comparator.comparing(ReferencePoint::getNumViolations)).get();
//		int cv = res.getNumViolations();
//		for(ReferencePoint rp : lambdas){
//			assert cv <= rp.getNumViolations();
//		}
//		return res;
		return lambdas.stream().min(Comparator.comparing(ReferencePoint::getNumViolations)).get();
	}

	private ReferencePoint getBestOnGradientLine(ReferencePoint lambda, double[] grad) {
		assert( Math.abs( Arrays.stream(lambda.getDim()).sum() - 1 ) < Geometry.EPS );
		
		Pair <double[], double[]> simplexSegment = Geometry.getSimplexSegment(lambda.getDim(), grad);
		double l1[] = simplexSegment.first, l2[] = simplexSegment.second;
		
		//Each pair is (t, [+,-] id), where t represents "time" on segment l1, l2 counted from l1 to l2
		// while absolute value of id represents comparison id which changes when lambda crosses this point. Positive id indicates 
		//change from "not reproduced" to "reproduced" comparison, while negative id indicates opposite.
		ArrayList < Pair<Double, Integer> > switchPoints = getAllSwitchPoints(l1, l2);
		
		assert( Math.abs( Arrays.stream(l1).sum() - 1 ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(l2).sum() - 1 ) < Geometry.EPS );
		ReferencePoint res = new ReferencePoint(Geometry.linearCombination(l1, l2, findBestTime(switchPoints)));
		assert( Math.abs( Arrays.stream(res.getDim()).sum() - 1 ) < Geometry.EPS );
		
		//Debug
		if(Lambda.evaluateLambda(res) > Lambda.evaluateLambda(lambda)){
			PreferenceCollector PC = PreferenceCollector.getInstance();
			System.out.println("ERROR");
		}
		assert Lambda.evaluateLambda(res) <= Lambda.evaluateLambda(lambda);
		
		return res;
	}

	protected double findBestTime(ArrayList < Pair<Double, Integer> > switches) {
		int CV = 0, bestCV = Integer.MAX_VALUE;
		
		int pos=0;
		while(pos<switches.size() && switches.get(pos).first < Geometry.EPS){
			if(switches.get(pos).second < 0) CV++;
			pos++;
		}
		if(pos >= switches.size()){
			return 0.5;
		}
		bestCV = CV;
		double bestBeg=0, bestEnd=switches.get(pos).first;
		
		for(int i=pos; i<switches.size(); i++){
			Pair<Double, Integer> p = switches.get(i);
			if(p.first > 1) break;
			if( switches.get(i-1).first < p.first && CV < bestCV){
				bestCV = CV;
				bestEnd = p.first;
				bestBeg = switches.get(i-1).first;
			}
			
			if(p.second < 0) CV++;
			else if(p.second > 0) CV--;
		}
		if(CV < bestCV){
			bestCV = CV;
			bestEnd = 1;
			bestBeg = switches.get(switches.size()-1).first;
		}
		
		//return (bestBeg + bestEnd)/2;
		return bestBeg + (bestEnd - bestBeg) * NSGAIIIRandom.getInstance().nextDouble();
	}

	protected ArrayList<Pair<Double, Integer>> getAllSwitchPoints(double l1[], double l2[]) {
		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
		for(int cpId=0; cpId<PreferenceCollector.getInstance().getComparisons().size(); cpId++){
			Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
			
			ArrayList <Line2D> lines = getLines(cp.getBetter(), cp.getWorse(), l1, l2); 
			ArrayList <Line2D> upperEnvelope = Geometry.linesUpperEnvelope(lines);

			//Check comparison for alpha=0 (linearCombination(lambda1, lambda2, 0) = lambda2) to properly initialize switches array
			int zeroComparison = ChebyshevRanker.compareSolutions(cp.getBetter(), cp.getWorse(), null, l2, 0);
			if(zeroComparison != 0){ res.add(new Pair<Double, Integer>(.0, -zeroComparison*(cpId+1)));}
			addComparisonSwitchPoints(res, upperEnvelope, l1, l2, cpId, lines);
		}
		Collections.sort(res, new Comparator<Pair<Double, Integer>>() {
			@Override
			public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
				return Double.compare(o1.first, o2.first);
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
				if(crossX < 0 || crossX > 1) continue;
				if(line2.isBetter()){ res.add(new Pair<Double, Integer>(crossX, -(cpId+1))); }
				else{ res.add(new Pair<Double, Integer>(crossX, cpId+1)); }
				
				double lambda[] = Geometry.linearCombination(l1, l2, crossX);
				Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
				double M1 = ChebyshevRanker.eval(cp.getBetter(), null, lambda, 0);
				double M2 = ChebyshevRanker.eval(cp.getWorse(), null, lambda, 0);
				if( ! ((M1-M2) < Geometry.EPS)){
					 System.out.println("ERROR");
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

	protected double[] getTotalPCGradient(ReferencePoint lambda) {
		double gradTheta[] = new double[numObjectives];
		int CV = 0;
		for(Comparison cp : PreferenceCollector.getInstance().getComparisons()){
			Solution a = cp.getBetter(), b = cp.getWorse();
			//If lambda does not reproduce comparison a is better than b
			if(ChebyshevRanker.eval(a, null, lambda.getDim(), 0.0) >= ChebyshevRanker.eval(b, null, lambda.getDim(), 0.0) ){
				CV++;
				//We want to maximize function value so we compute it's gradient
				double gradThetaA[] = smoothMaxGrad(a.getObjectives(), lambda.getDim());
				double gradThetaB[] = smoothMaxGrad(b.getObjectives(), lambda.getDim());
				for(int i=0; i<numObjectives; i++){
					gradTheta[i] = gradThetaB[i] - gradThetaA[i]; //Maximize B and minimize A; 
				}
			}
		}

		assert CV == 0 || DoubleStream.of(gradTheta).anyMatch(e -> Math.abs(e) > 0);
		
		double gradLambda[] = theta2lambda(gradTheta);
		
		for(int i=0; i<numObjectives; i++){
			gradLambda[i] -= 1.0/numObjectives;
		}
		return gradLambda;
	}

	/**
	 * Implements derivative defined here: https://en.wikipedia.org/wiki/Smooth_maximum  
	 * @param a - solution
	 * @param lambda - Chebyshev function direction
	 * @param i - id of variable with regard to which derivative is computed
	 * @return
	 */
	
	public double[] smoothMaxGrad(double a[], double lambda[]){
		double alpha = 20;
		
		double A[] = new double[numObjectives], B[] = new double[numObjectives];
		double dA[] = new double[numObjectives], dB[] = new double[numObjectives];
		double grad[] = new double[numObjectives];
		
		for(int i=0; i<numObjectives; i++){
			A[i] = a[i] * lambda[i];
			B[i] = Math.exp(alpha * A[i]);
		}
		
		for(int k=0; k<numObjectives -1 ; k++){
			double sum1=0, sum2=0, sum3=0, sum4=0;

			for(int i=0; i<numObjectives; i++){
				dA[i] = a[i] * M.get(k, i);
				dB[i] = alpha * dA[i] * B[i];
			}
			
			for(int i=0; i<numObjectives; i++){
				sum1 += dA[i] * B[i] + A[i]*dB[i];
				sum2 += B[i];
				sum3 += A[i] * B[i];
				sum4 += dB[i];
			}
			
			grad[k] = (sum1 * sum2 - sum3 * sum4) / (sum2 * sum2);
		}
		return grad;
	}
	
	public ArrayList <ReferencePoint> improve(ArrayList<ReferencePoint> lambdasList) {
		for(ReferencePoint lambda : lambdasList) Lambda.evaluateLambda(lambda);
		ReferencePoint bestLambda = lambdasList.stream().min(Comparator.comparing(ReferencePoint::getNumViolations)).get();
		System.out.println("Best constraint violation: " + bestLambda.getNumViolations());
		return lambdasList.stream().map(l -> this.improve(l, bestLambda)).collect(Collectors.toCollection(ArrayList::new));
	}
}
