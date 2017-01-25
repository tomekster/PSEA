package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import core.points.ReferencePoint;
import core.points.Solution;

public class Geometry {

	public static double EPS = 1E-9;

	/**
	 * 
	 * @param P
	 *            point that we are computing distance for
	 * @param B
	 *            vector representing line
	 * @return distance between point A and line (0,0) <-> B
	 */
	public static double pointLineDist(double[] P, double[] B) {

		if (P.length != B.length) {
			throw new RuntimeException("Point and line do not have same dimensionality");
		}

		int numDim = P.length;

		if (numDim < 2) {
			throw new RuntimeException("Space needs to be at least two dimensional");
		}

		double max = Double.MIN_VALUE;
		for (double d : B) {
			max = Math.max(max, Math.abs(d));
		}
		if (max < EPS) {
			String s = "";
			for(int i=0; i<B.length; i++){
				s += B[i] + " ";
			}
			throw new RuntimeException("Line represented by degenerated vector (B = 0)\n B: [" + s + "]\n");
		}

		// Define zero point
		double zero[] = new double[numDim];

		double t = dot(P, B) / dot(B, B);

		double resVector[] = new double[numDim];
		for (int i = 0; i < numDim; i++) {
			resVector[i] = P[i] - t * B[i];
			zero[i] = 0.0;
		}
		return euclideanDistance(zero, resVector);
	}

	public static double euclideanDistance(double[] P1, double[] P2) {
		if (P1.length != P2.length) {
			throw new RuntimeException("Points have different dimensionality");
		}

		double sum = 0.0;
		double dx;
		for (int i = 0; i < P1.length; i++) {
			dx = P1[i] - P2[i];
			sum += dx * dx;
		}

		return Math.sqrt(sum);
	}
	
	public static double getLen(double[] vect){
		double sum = 0;
		for(int i=0; i<vect.length; i++) sum += vect[i] * vect[i];
		return Math.sqrt(sum);
	}

	public static double dot(double[] A, double[] B) {
		if (A.length != B.length) {
			throw new RuntimeException("Vectors have different dimensionality");
		}
		double res = 0;
		for (int i = 0; i < A.length; i++) {
			res += A[i] * B[i];
		}
		return res;
	}
	
	public static double[] getVect(double[] A, double[] B) {
		if (A.length != B.length) {
			throw new RuntimeException("Vectors have different dimensionality");
		}
		double res[] = new double[A.length];
		for (int i = 0; i < A.length; i++) {
			res[i] = B[i] - A[i];
		}
		return res;
	}
	
	public static Solution cast3dPointToPlane(double p[]){
		double tmp[] = {0,0};
		Solution res = new Solution(tmp, tmp);
		double a[] = p.clone();
		double sum = 0;
		for(double d : a){
			sum += d;
		}
		for(int i=0; i<p.length; i++){
			a[i] /= sum;
		}
		a[0] -= 1;
		double b[] = {-0.5, -0.5, 1};
		res.setObjective(0, pointLineDist(a, b));
		
		b = new double[]{-1,1,0};
		res.setObjective(1, pointLineDist(a, b));
		return res;
	}
	
	public static double[] randomPointOnSphere(int dim, double radius){
		NSGAIIIRandom rand = NSGAIIIRandom.getInstance();
		double res[] = new double[dim];
		double sum = 0;
		
		for(int i=0; i<dim; i++){
			double r = rand.nextGaussian(); 
			res[i] = r;
			sum += r * r;
		}
		sum = Math.sqrt(sum);
		for(int i=0; i<dim; i++){
			res[i] *= radius/sum;
		}
		return res;
	}
	
	/**
	 * Maps input point on hyperplane parallel to one used in NSGA-III.
	 * The one used here is given by equation SUM(x_i) = 1
	 * @param a
	 * @return
	 */
	public static double[] normalize(double[] a){
		double res[] = new double[a.length];
		double sum = 0;
		for(int i=0; i<a.length; i++){
			res[i] = a[i];
			sum += a[i];
		}
		
		for(int i=0; i<a.length; i++){
			res[i] /= sum;
		}
		return res;
	}

	public static double[] invert(double[] dimensions) {
		int len = dimensions.length;
		double lambda[] = new double[len];
		for (int i = 0; i < len; i++) {
			lambda[i] = 1 / dimensions[i];
		}
		return lambda;
	}
	
	/**
	 * Takes two input vectors a, b of same size and returns their linear combination given by equation:
	 * 	alpha * a + (1-alpha) * b 
	 * @param a
	 * @param b
	 * @param alpha
	 * @return
	 */
	public static double[] linearCombination(double[] a, double[] b, double alpha){
		assert a.length == b.length;
		assert alpha >= 0;
		assert alpha <= 1;
		
		double res[] = new double[a.length];
		for(int i =0; i<a.length; i++){
			res[i] = alpha * a[i] +  (1-alpha) * b[i];
		}
		return res;
	}

	private static HashMap<Integer, double[][]> hyperplaneTransformationMaps = new HashMap<>();
	
	/**
	 * Maps hypersphere orthogonal to last dimension axis to hyperplane parallel to one used in NSGA-III. 
	 * The one used here is given by equation SUM(x_i) = 0; 
	 * 
	 * @param q - Input point lying on hyperplane orthogonal to last dimension axis
	 * @return 
	 */
	public static double[] mapOnParallelHyperplane(double[] q) {
		if(!hyperplaneTransformationMaps.containsKey(q.length)){
			hyperplaneTransformationMaps.put(q.length, genHypTransMap(q.length));
		}
		return transformHyperplanePoint(q, hyperplaneTransformationMaps.get(q.length));
	}
	
	/**
	 * Multiply point by matrix to map it to hyperplane
	 * @param q
	 * @param M
	 * @return
	 */
	private static double[] transformHyperplanePoint(double[] q, double[][] M) {
		assert q.length == M.length;
		double res[] = new double[q.length];
		for(int i=0; i<M[0].length; i++){
			res[i] = 0;
			for(int j=0; j<q.length; j++){
				res[i] += q[j] * M[j][i]; 
			}
		}
		
		//Just check
		double sum = 0;
		for(double d : res){
			sum += d;
		}
		assert sum < EPS;
		
		return res;
	}

	/**
	 * Method geneartes transformation matrix which transforms n-1 dimesional hyperplane, 
	 * orthogonal to n-th dimension axis, to hyperplane which crosses axis in points 
	 * (1,0,0,...), (0,1,0,...), (0,0,1,...). 
	 * 
	 * 
	 * The axis is of the form 
	 * |1 -1  0  0  0 ... |
	 * |1  1 -2  0  0 ... |
	 * |1  1  1 -3  0 ... |
	 * 			....
	 * 
	 * Each row has to normalized to length one
	 * 	
	 * @param n - resulting matrix size n x n
	 * @return
	 */
	private static double[][] genHypTransMap(int n) {
		double res[][] = new double[n][n];
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				if(j<=i){
					res[i][j] = 1;
				} else if(j==i+1){
					res[i][j] = -(i+1); 
				} else{
					res[i][j] = 0;
				}
			}
			res[i] = vectorNormalize(res[i]);
		}
		return res;
	}

	/**
	 * Normalize vector to length 1.0
	 * @param v Input vector - arbitrary length
	 * @return 	Output vector - length 1.0
	 */
	private static double[] vectorNormalize(double[] v) {
		int n = v.length;
		double sum = 0;
		double[] res = new double[n];
		
		for(int i=0; i<n; i++){
			sum += v[i] * v[i];
		}
		
		double denom = Math.sqrt(sum);
		for(int i=0; i<n; i++){
			res[i] = v[i] / denom;
		}
		return res;
	}
	
	public static long choose(long total, long choose){
	    if(total < choose)
	        return 0;
	    if(choose == 0 || choose == total)
	        return 1;
	    return choose(total-1,choose-1)+choose(total-1,choose);
	}
	
	public static double[] nonnegativeSegmentPoint(double[] beg, double[] pos) {
		double v[] = new double[beg.length];
		double res[] = new double[beg.length];
		double mult = 0;
		for(int i=0; i<beg.length; i++){
			v[i] = pos[i] - beg[i];
			if(beg[i] < 0){
				mult = Double.max(mult, -beg[i] / v[i]);
			}
		}
		for(int i=0; i < beg.length; i++){
			res[i] = beg[i] + mult * v[i];
		}
		return res;
	}
	
	public static double[] getRandomNeighbour(double[] centralPoint, double radius) {
		int dim = centralPoint.length;
		ReferencePoint newPoint = new ReferencePoint(dim);
		double p[] = new double[dim - 1];
		double q[] = new double[dim];
		p = Geometry.randomPointOnSphere(dim - 1, radius);
		for (int i = 0; i < dim - 1; i++) {
			q[i] = p[i];
		}
		q[dim - 1] = 0;
		q = Geometry.mapOnParallelHyperplane(q);
		for (int i = 0; i < dim; i++) {
			q[i] += centralPoint[i];
		}
		for (int i = 0; i < dim; i++) {
			if (q[i] < 0) {
				q = Geometry.nonnegativeSegmentPoint(q, centralPoint);
				break;
			}
		}
		return q;
	}

	public static double[] lineCrossDTLZ1HyperplanePoint(double[] lambda) {
		double point[] = new double[lambda.length];
		double sum = 0;
		for (double d : lambda) {
			sum += d;
		}
		for(int i=0; i< lambda.length; i++){
			point[i] = lambda[i] * (0.5 / sum);
		}
		return point;
	}

	public static double[] lineCrossDTLZ234HyperspherePoint(double[] lambda) {
		double point[] = new double[lambda.length], sqr_sum = 0;
		for (double d : lambda) {
			sqr_sum += d*d;
		}
		double div = Math.sqrt(sqr_sum);
		for (int i = 0; i < lambda.length; i++) {
			point[i] = lambda[i] / div;
		}
		return point;
	}
	
	public static class Line2D implements Comparable{
		Double a, b;
		public Line2D(double a, double b){
			this.a = a;
			this.b = b;
		}
		public Line2D(Pair <Double, Double> line){
			this.a = line.first;
			this.b = line.second;
		}
		public double evalX(double x) {
			return a*x + b;
		}
		public double crossX(Line2D l2){
			if( Math.abs(a - l2.a) < EPS){
				return Double.POSITIVE_INFINITY;
			}
			return (l2.b - b) / (a - l2.a);
		}
		@Override
		public int compareTo(Object arg0) {
			Line2D l2 = (Line2D) arg0;
			if( a < l2.a ) return -1;
			else if( Math.abs(a - l2.a) < EPS ){
				return (b > l2.b ? -1 : 1);
			}
			else{
				return 1;
			}
		}
	}
	
	public static ArrayList< Line2D> linesSetUpperEnvelope(ArrayList < Line2D > lines){
		Collections.sort(lines);
		Stack <Line2D> stack = new Stack<>();
		stack.push(lines.get(0));
		stack.push(lines.get(1));
		for(int i=2; i<lines.size(); i++){
			Line2D l1 = lines.get(i);
			Line2D l2 = stack.pop();
			Line2D l3 = stack.peek();
			stack.push(l2);
			
			if(Math.abs(l1.a - l2.a) < EPS ) continue;
			else {
				while(stack.size() > 1 && l2.crossX(l3) >= l1.crossX(l3)){
					stack.pop();
					if(stack.size() > 1){
						l2 = stack.pop();
						l3 = stack.peek();
						stack.push(l2);
					}
				}
				stack.push(l1);
			}
		}
		
		return new ArrayList<>(stack);
	}
}
