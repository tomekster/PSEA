package utils.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.ReferencePoint;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import utils.math.structures.Pair;
import utils.math.structures.Point2D;

public class Geometry {

	public static double EPS = 1E-6;

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

		double max = -Double.MAX_VALUE;
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
		double dist = euclideanDistance(zero, resVector); 
		if(! (dist < Double.MAX_VALUE) ){
			System.out.println("Wrong distance error");
		}
		return dist;
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
	
	/**
	 * 
	 * @param A - point in n dimensions
	 * @param B - point in n dimensions
	 * @return n-dimensional vector B-A
	 */
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
	
	/**
	 * 
	 * @param p - point on plane given by equation sum_{1<=i<=n}(x_i) = 1
	 * @return p casted to equilateral triangle lied on XY plane
	 */
	public static double[] cast3dPointToPlane(double p[]){
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
		double c[] = new double[]{-1,1,0};
		double res[] = new double[]{pointLineDist(a, b), pointLineDist(a, c)};
		return res;
	}
	
	/**
	 * 
	 * @param dim - number of dimensions of point
	 * @param radius - distance of point from origin
	 * @return n-dimensional point lying on sphere with center in origin and radius r
	 */
	public static double[] randomPointOnSphere(int dim, double radius){
		MyRandom rand = MyRandom.getInstance();
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
	 * Maps input point from a hyperplane parallel to one used in NSGA-III.
	 * The one used here is given by equation SUM(x_i) = 1
	 * @param a
	 * @return
	 */
	public static double[] normalizeSum1(double[] a){
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
		double res[] = new double[len];
		for (int i = 0; i < len; i++) {
			res[i] = 1 / dimensions[i];
			if(Double.isNaN(res[i]) || Double.isInfinite(res[i])){
				res[i] = Double.MAX_VALUE/50;
			}
		}
		return res;
	}
	
	/**
	 * Takes two input vectors a, b of same size and returns their linear combination given by equation:
	 * 	(1-alpha) * a + alpha * b 
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
		for(int i =0; i < a.length; i++){
			res[i] = (1-alpha) * a[i] +  alpha * b[i];
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
		for(int colId=0; colId<M[0].length; colId++){
			res[colId] = 0;
			for(int rowId=0; rowId<q.length; rowId++){
				res[colId] += q[rowId] * M[rowId][colId]; 
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
	public static double[][] genHypTransMap(int n) {
		double res[][] = new double[n][n];
		for(int i=0; i<n; i++){
			for(int j=0; j<n; j++){
				if(j<=i){
					res[i][j] = 1;
				} else if(j==i+1){
					res[i][j] = -j; 
				} else{
					res[i][j] = 0;
				}
			}
//			res[i] = vectorNormalize(res[i]);
		}
		return res;
	}

	/**
	 * Normalize vector to length 1.0
	 * @param v Input vector - arbitrary length
	 * @return 	Output vector - length 1.0
	 */
	public static double[] vectorNormalize(double[] v) {
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
	
	public static double [] getRandomVectorOnHyperplane(int dim, double length){
		double p[] = new double[dim - 1];
		double q[] = new double[dim];
		p = Geometry.randomPointOnSphere(dim, length);
		for (int i = 0; i < dim - 1; i++) {
			q[i] = p[i];
		}
		q[dim - 1] = 0;
		q = Geometry.mapOnParallelHyperplane(q);
		return q;
	}
	
	public static double [] getRandomNeighbour(double[] centralPoint, double radius) {
		int dim = centralPoint.length;
		double q[] = getRandomVectorOnHyperplane(dim, radius);
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

	public static double[] lineCrossDTLZ1HyperplanePoint(double[] pointOnLine) {
		double crossPoint[] = new double[pointOnLine.length];
		double sum = 0;
		for (double d : pointOnLine) {
			sum += d;
		}
		for(int i=0; i< pointOnLine.length; i++){
			crossPoint[i] = pointOnLine[i] * (0.5 / sum);
		}
		return crossPoint;
	}

	public static double[] lineCrossDTLZ234HyperspherePoint(double[] pointOnLine) {
		double crossPoint[] = new double[pointOnLine.length], sqr_sum = 0;
		for (double d : pointOnLine) {
			sqr_sum += d*d;
		}
		double div = Math.sqrt(sqr_sum);
		for (int i = 0; i < pointOnLine.length; i++) {
			crossPoint[i] = pointOnLine[i] / div;
		}
		return crossPoint;
	}
	
	public static class Line2D implements Comparable <Line2D>, Point2D{
		Double a, b;
		boolean better;
		
		public Line2D(double a, double b){
			this(a,b,false);
		}
		public Line2D(double a, double b, boolean better){
			this.a = a;
			this.b = b;
			this.better = better;
		}
		public double evalX(double x) {
			return a*x + b;
		}
		public double crossX(Line2D l2){
			//if( Math.abs(a - l2.a) < EPS){
			if(Double.compare(a, l2.a) == 0){
				return Double.POSITIVE_INFINITY;
			}
			return (l2.b - b) / (a - l2.a);
		}
		
		public int compareTo(Line2D l) {
			if ( Double.compare(this.a, l.a) == 0) {
				return Double.compare(this.b, l.b);
			} else {
				return Double.compare(this.a, l.a) ;
			}
		}
		
		public boolean isBetter(){
			return better;
		}
		
		@Override
		public String toString(){
			return "(" + this.a + ", " + this.b + ")"; 
		}
		@Override
		public double getX() {
			return a;
		}
		@Override
		public double getY() {
			return b;
		}
	}
	
	public static ArrayList< Line2D> linesUpperEnvelope(ArrayList < Line2D > lines){
		lines.add(new Line2D(0, -(1e10)));
		Line2D linesForHull[] = new Line2D[lines.size()];
		lines.toArray(linesForHull);
		Point2D convexHull[] = convex_hull(linesForHull);
		ArrayList <Line2D> upperEnvelope = new ArrayList<>();
		for(Point2D p : convexHull){
			if(p.getY() == -(1e10)){
				continue;
			}
			else{
				upperEnvelope.add((Line2D) p);
			}
		}
		Collections.sort(upperEnvelope);
		return upperEnvelope;
	}

	// https://stackoverflow.com/a/7422652
	// Convex Hull Problem is a dual of finding Upper Envelope, where each point (a,b) corresponds to function y = ax + b
	
	public static double cross(Point2D O, Point2D A, Point2D B) {
		return (A.getX() - O.getX()) * (B.getY() - O.getY()) - (A.getY() - O.getY()) * (B.getX() - O.getX());
	}

	public static Point2D[] convex_hull(Point2D[] P) {

		if (P.length > 1) {
			int n = P.length, k = 0;
			Point2D[] H = new Point2D[2 * n];

			Arrays.sort(P);

			// Build lower hull
			for (int i = 0; i < n; ++i) {
				while (k >= 2 && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}

			// Build upper hull
			for (int i = n - 2, t = k + 1; i >= 0; i--) {
				while (k >= t && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}
			if (k > 1) {
				H = Arrays.copyOfRange(H, 0, k - 1); // remove non-hull vertices after k; remove k - 1 which is a duplicate
			}
			return H;
		} else if (P.length <= 1) {
			return P;
		} else{
			return null;
		}
	}
	
	
	public static Pair<double[], double[]> getSimplexSegment(double[] point, double[] grad) {
		assert (Math.abs( Arrays.stream(point).sum() - 1) < Geometry.EPS);
		assert(Math.abs( Arrays.stream(grad).sum()) < Geometry.EPS);
		
		int numDim = point.length;
		double p1[] = new double[numDim], p2[] = new double[numDim];
		double t1 = Double.MAX_VALUE, t2 = Double.MAX_VALUE;
		
		for(int i=0; i<numDim; i++){
			assert point[i] >= -Geometry.EPS && point[i] <= 1+Geometry.EPS;
			if(point[i] < 0) point[i]=0;
			if(point[i] > 1) point[i]=1;
			if(grad[i] < 0){
				t1 = Double.min( t1, -point[i]/grad[i]);
//				t2 = Double.min( t2, -(1-point[i])/grad[i]);
			}
			else if(grad[i] > 0){
//				t1 = Double.min( t1, (1-point[i])/grad[i]);
				t2 = Double.min( t2, point[i]/grad[i]);
			}
		}
		
		for(int i=0; i<numDim; i++){
			p1[i] = point[i] + t1 * grad[i];
			p2[i] = point[i] - t2 * grad[i];
		}
		assert( Math.abs( Arrays.stream(p1).sum() - 1) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(p2).sum() - 1) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(p1).min().getAsDouble() ) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(p2).min().getAsDouble() ) < Geometry.EPS );
		
		for(int i=0; i < numDim; i++){
			assert p1[i] > -Geometry.EPS;
			assert p1[i] < 1 + Geometry.EPS;
			assert p2[i] > -Geometry.EPS;
			assert p2[i] < 1 + Geometry.EPS;
			if(p1[i] > 1) p1[i] = 1;
			if(p1[i] < 0) p1[i] = 0;
			if(p2[i] > 1) p2[i] = 1;
			if(p2[i] < 0) p2[i] = 0;
		}
		assert( Math.abs( Arrays.stream(p1).sum() - 1) < Geometry.EPS );
		assert( Math.abs( Arrays.stream(p2).sum() - 1) < Geometry.EPS );
		
		return new Pair <double[], double[]>(p1, p2);
	}
	
	/**
	 * 
	 * @param numDim - number of dimensions of resulting point
	 * @return n-dimensional point with dimensions summing up to 1, picked from uniform distribution.
	 */
	public static double[] getRandomVectorSummingTo1(int numDim) {
		ArrayList <Double> breakPoints = new ArrayList<>();
		ArrayList <Double> dimensions = new ArrayList<>();
		breakPoints.add(0.0);
		breakPoints.add(1.0);
		for(int i=0; i<numDim-1; i++){ 
			breakPoints.add(MyRandom.getInstance().nextDouble()); 
		}
		Collections.sort(breakPoints);

		for(int i=0; i < numDim; i++){
			dimensions.add(breakPoints.get(i+1) - breakPoints.get(i));
		}
		
		Collections.shuffle(dimensions);
		double dim[] = new double[numDim];
		for(int i=0; i<numDim; i++){
			dim[i] = dimensions.get(i);
		}
		
		assert( Math.abs(Arrays.stream(dim).sum() - 1) < Geometry.EPS);
		return dim;
	}
	
	public static double[] dir2point(double direction[]){
		return normalizeSum1(invert(direction));
	}
	
	public static double dirDist(double dir1[], double dir2[]){
		return euclideanDistance(dir2point(dir1), dir2point(dir2));
	}
	
	public static Hyperplane generateNewHyperplane(int dim, double size, double center[]){
		//Fenerate regular hyperplane
		Hyperplane hyperplane = new Hyperplane(dim);
		
		//Move every reference point to location corresponding to hyperplane centered in origin, scale according to size parameter and move to location corresponding to new center
		for(ReferencePoint rp : hyperplane.getReferencePoints()){
			for(int i=0; i<hyperplane.getDim(); i++){
				rp.setDim(i, (rp.getDim(i) - 1.0 / hyperplane.getDim()) * size + center[i]);
			}
		}
		
		//Find smallest nonpositive value for every dimension for all reference points
		double negativeCoordinates[] = new double[hyperplane.getDim()];
		for(ReferencePoint rp : hyperplane.getReferencePoints()){
			for(int i=0; i<hyperplane.getDim(); i++){
				negativeCoordinates[i] = Double.min(negativeCoordinates[i], rp.getDim(i));
			}
		}
		
		//Adjust all reference point so that all coordinates are nonnegative
		for(ReferencePoint rp : hyperplane.getReferencePoints()){
			for(int i=0; i<hyperplane.getDim(); i++){
				rp.setDim(i, rp.getDim(i) - negativeCoordinates[i] );
			}
		}
		return hyperplane;
	}
	
	/**
	 * 
	 * @param point
	 * @param pop
	 * @return Return distance between point and closest solution from pop (in objectives space)
	 */
	public static double getMinDist(double point[], Population pop){
		double min = Double.MAX_VALUE;
		for(Solution s : pop.getSolutions()){
			double d = Double.min(min, Geometry.euclideanDistance(point, s.getObjectives()));
			if(d < min){
				min = d;
			}
		}
		return min;
	}
	
	/**
	 * 
	 * @param point
	 * @param pop
	 * @return Computes average distance between point and solutions from pop (in objectives space)
	 */
	public static double getAvgDist(double point[], Population pop){
		double avg = 0;
		for(Solution s : pop.getSolutions()){
			avg += Geometry.euclideanDistance(point, s.getObjectives());
		}
		return avg / pop.size();
	}
	
	public static double variance(double a[]){
		double mean = 0, res = 0;
		for(double v : a) mean += v;
		mean /= a.length;
		for(double v : a) res += (v-mean) * (v-mean);
		return res;
	}
	
	public static void assertEqualDoubleArrays(double a[], double b[]){
		assertTrue(a.length == b.length);
		for(int i=0; i < a.length; i++){
			assertEquals(a[i], b[i], Geometry.EPS);
		}
	}
	
	/**
	 * @return Returns the Euclidean distance between two farthest solutions in given population pop.
	 */
	public static double maxDist(Population pop) {
		double maxDist = 0;
		for(Solution s1 : pop.getSolutions()){
			for(Solution s2 : pop.getSolutions()) maxDist = Double.max(maxDist, Geometry.euclideanDistance(s1.getObjectives(), s2.getObjectives()));
		}
		return maxDist;
	}
}
