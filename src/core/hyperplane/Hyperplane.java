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
		double p[] = new double[dim];
		boolean positive;
		do {
			positive = true;
			p = Geometry.randomPointOnSphere(dim, radius);
			for (int i = 0; i < dim; i++) {
				p[i] += centralPoint.getDim(i);
				if (p[i] < 0) {
					positive = false;
					break;
				}
			}
			p = Geometry.normalize(p);
		} while (!positive);
		newPoint.setDimensions(p);
		return newPoint;
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
