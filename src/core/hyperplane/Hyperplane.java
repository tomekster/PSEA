package core.hyperplane;

import java.util.ArrayList;

import utils.Geometry;

public abstract class Hyperplane {

	protected ArrayList<ReferencePoint> referencePoints;
	protected int dim;

	public Hyperplane(int M) {
		this.dim = M;
		referencePoints = new ArrayList<ReferencePoint>();
		generateReferencePoints();
		for(ReferencePoint rp : referencePoints){
			for(int i=0; i<rp.getNumDimensions(); i++){
				rp.setDim(i, Double.max(Geometry.EPS, rp.getDim(i)));
			}
		}
	}

	private void generateReferencePoints() {
		ArrayList<ReferencePoint> boundaryLayer = new ArrayList<>();
		ArrayList<ReferencePoint> insideLayer = new ArrayList<>();

		ArrayList<Integer> partitions = getNumPartitions(dim);
		int p = partitions.get(0);
		generateRecursive(new ReferencePoint(dim), 1.0 / p, 0, p, boundaryLayer);
		referencePoints.addAll(boundaryLayer);
		if (partitions.size() > 1) {
			p = partitions.get(1);
			ReferencePoint rp = new ReferencePoint(dim);
			generateRecursive(rp, 0.5 / p, 0, p, insideLayer);
			
			referencePoints.addAll(insideLayer);
		}
	}

	private void generateRecursive(ReferencePoint rp, double step, int startDim, int left,
			ArrayList<ReferencePoint> layer) {
		if (left == 0) {
			layer.add(rp);
			return;
		}

		if (startDim == rp.getNumDimensions() - 1) {
			rp.incrDim(startDim, step * left);
			layer.add(rp);
			return;
		}

		for (int i = 0; i <= left; i++) {
			generateRecursive(new ReferencePoint(rp), step, startDim + 1, left - i, layer);
			rp.incrDim(startDim, step);
		}
	}

	public ArrayList<ReferencePoint> getReferencePoints() {
		return this.referencePoints;
	}

	protected ReferencePoint getRandomNeighbour(ReferencePoint centralPoint, double radius) {
		ReferencePoint newPoint = new ReferencePoint(dim);
		double p[] = new double[dim - 1];
		double q[] = new double[dim];
		p = Geometry.randomPointOnSphere(dim-1, radius);
		for(int i=0; i<dim-1; i++){
			q[i] = p[i];
		}
		q[dim-1] = 0;
		q = Geometry.mapOnHyperplane(q);
		for (int i = 0; i < dim; i++) {
			q[i] += centralPoint.getDim(i);
		}
		for(int i=0; i<dim; i++){
			if(q[i] < 0){
				q = binarySearchNonnegative(centralPoint.getDim(), q, 0, 1);
				break;
			}
		}
		newPoint.setDimensions(q);
		return newPoint;
	}

	private double[] binarySearchNonnegative(double[] pos, double[] cur, double beg, double end) {
		//TODO - NOTE just arbitrary threshold - can be customized
		double thresh = 1E-6;
		while(end - beg > thresh){
			double mid = (beg+end)/2;
			double [] midDim = Geometry.linearCombination(pos, cur, mid);
			//Check if any of dimensions has negative value
			double min = Double.MIN_VALUE;
			for(double d : midDim){
				min = Double.min(min, d);
			}
			if(min >= 0){
				end = mid;
			} else {
				beg = mid;
			}
		}
		return Geometry.linearCombination(pos, cur, end); 
	}

	public void cloneReferencePoints() {
		ArrayList <ReferencePoint> newReferencePoints = new ArrayList<>();
		for(ReferencePoint rp : referencePoints){
			newReferencePoints.add(rp.copy());
		}
		this.referencePoints = newReferencePoints;
	}

	private ArrayList<Integer> getNumPartitions(int numObjectives) {
		ArrayList<Integer> res = new ArrayList<>();
		switch (numObjectives) {
		case 2:
			res.add(2);
			break;
		case 3:
			res.add(12);
			break;
		case 5:
			res.add(6);
			break;
		case 8:
		case 10:
			res.add(3);
			res.add(2);
			break;
		case 15:
			res.add(2);
			res.add(1);
			break;
		default:
			throw new RuntimeException("Undefined number of hyperplane partitions for given problem dimensionality ("
					+ numObjectives + ")");
		}
		return res;
	}
	
	public int getDim(){
		return this.dim;
	}
}
