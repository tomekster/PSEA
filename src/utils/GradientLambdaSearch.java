package utils;

import java.util.ArrayList;
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
	
	private ReferencePoint improve(ReferencePoint lambda) {
		double grad[] = getTotalPCGradient(lambda);
		// If gradient is empty (for example lambda reproduces all comparisons) then no improvement is needed
		if(Geometry.getLen(grad) < Geometry.EPS){
			return lambda;
		}
		
//		double neigh[] = Geometry.getRandomNeighbour(lambda.getDim(), 1.0);
//		double grad[] = new double[numObjectives];
//		for(int i=0; i<numObjectives; i++){
//			grad[i] = neigh[i] - lambda.getDim(i);
//		}
				
		Pair <double[], double[]> simplexSegment = Geometry.getSimplexSegment(lambda.getDim(), grad);
		double l1[] = simplexSegment.first, l2[] = simplexSegment.second;
		
		double m1 = 1, m2 = 1;
		
		for(int i=0; i<numObjectives; i++){
			assert l1[i] > -Geometry.EPS;
			assert l1[i] < 1 + Geometry.EPS;
			assert l2[i] > -Geometry.EPS;
			assert l2[i] < 1 + Geometry.EPS;
			if(l1[i] < m1) m1 = l1[i];
			if(l2[i] < m2) m2 = l2[i];
		}
		assert Math.abs(m1) < Geometry.EPS;
		assert Math.abs(m2) < Geometry.EPS;
		
		//Each pair is (t, [+,-] id), where t represents "time" on segment l1, l2 counted from l1 to l2
		// while absolute value of id represents comparison id which changes when lambda crosses this point. Positive id indicates 
		//change from "not reproduced" to "reproduced" comparison, while negative id indicates opposite.
		ArrayList < Pair<Double, Integer> > switches = getComparisonSwitchPoints(l1, l2);
		
		ReferencePoint res = new ReferencePoint(Geometry.linearCombination(l1, l2, findBestTime(switches)));
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
		
		return (bestBeg + bestEnd)/2;
	}

	protected ArrayList<Pair<Double, Integer>> getComparisonSwitchPoints(double l1[], double l2[]) {
		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
		for(int cpId=0; cpId<PreferenceCollector.getInstance().getComparisons().size(); cpId++){
			Comparison cp = PreferenceCollector.getInstance().getComparisons().get(cpId);
			ArrayList <Line2D> lines = new ArrayList<>();
			for(int i=0; i<numObjectives; i++){
				lines.add(new Line2D(cp.getBetter().getObjective(i) * (l1[i] - l2[i]), cp.getBetter().getObjective(i) * l2[i], true ) );
				lines.add(new Line2D(cp.getWorse().getObjective(i) * (l1[i] - l2[i]), cp.getWorse().getObjective(i) * l2[i], false ) );
			}
			ArrayList <Line2D> upperEnvelope = Geometry.linesSetUpperEnvelope(lines);

			//Check comparisons on lambda1 (0 on time scale corresponding to lambda2) to properly initialize switches array
			int zeroComparison = ChebyshevRanker.compareSolutions(cp.getBetter(), cp.getWorse(), null, l2, 0);
			if( zeroComparison < 0){
				res.add(new Pair<Double, Integer>(.0, cpId+1));
			}
			else if(zeroComparison > 0){
				res.add(new Pair<Double, Integer>(.0, -(cpId+1)));
			}
			
			for(int i=1; i<upperEnvelope.size(); i++){
				Line2D line1 = upperEnvelope.get(i-1);
				Line2D line2 = upperEnvelope.get(i);
				if( !(line1.isBetter() ^ line2.isBetter())) continue;
				double crossX = line1.crossX(line2);
				if(crossX < 0 || crossX > 1) continue;
				if(line2.isBetter()){
					res.add(new Pair<Double, Integer>(crossX, -(cpId+1)));
				}
				else{
					res.add(new Pair<Double, Integer>(crossX, cpId+1));
				}
			}
		}
		Collections.sort(res, new Comparator<Pair<Double, Integer>>() {
			@Override
			public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
				return Double.compare(o1.first, o2.first);
			}
		});
		return res;
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
		return lambdasList.stream().map(l -> this.improve(l)).collect(Collectors.toCollection(ArrayList::new));
	}
}
