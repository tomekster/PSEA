package core.hyperplane;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.jfree.data.function.NormalDistributionFunction2D;

import core.Solution;
import utils.Geometry;
import utils.MyComparator;
import utils.NSGAIIIRandom;

public class Hyperplane {

	private ArrayList<ReferencePoint> referencePoints;
	private int dim;

	public Hyperplane(int M, ArrayList<Integer> partitions) {
		this.dim = M;
		referencePoints = new ArrayList<ReferencePoint>();
		generateReferencePoints(M, partitions);
	}

	private void generateReferencePoints(int M, ArrayList<Integer> partitions) {
		ArrayList<ReferencePoint> boundaryLayer = new ArrayList<>();
		ArrayList<ReferencePoint> insideLayer = new ArrayList<>();

		int p = partitions.get(0);
		generateRecursive(new ReferencePoint(M), 1.0 / p, 0, p, boundaryLayer);
		referencePoints.addAll(boundaryLayer);
		if (partitions.size() > 1) {
			p = partitions.get(1);
			ReferencePoint rp = new ReferencePoint(M);
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

	public void resetAssociations() {
		for (ReferencePoint rp : referencePoints) {
			rp.resetAssociation();
		}
	}

	public void modifyReferencePoints(boolean[] coherent, double alpha) {
		ArrayList<ReferencePoint> newReferencePoints = new ArrayList<>();

		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(MyComparator.referencePointComparatorDesc);
		for (int i = 0; i < referencePoints.size(); i++) {
			if (coherent[i]) {
				newReferencePoints.add(referencePoints.get(i));
				refPQ.add(referencePoints.get(i));
			}
		}
		int numIncoherentPoints = referencePoints.size() - newReferencePoints.size();
		double radius = NSGAIIIRandom.getInstance().nextDouble() * (Math.E - Math.exp(alpha)) / (Math.E - 1) * 0.5;
		for (int i = 0; i < numIncoherentPoints; i++) {
			ReferencePoint largestNicheCountRefPoint = refPQ.poll();
			ReferencePoint n = getRandomNeighbour(largestNicheCountRefPoint, radius);
			newReferencePoints.add(n);
			
			largestNicheCountRefPoint.decrNicheCount();
			refPQ.add(largestNicheCountRefPoint);
		}
		this.referencePoints = newReferencePoints;
	}

	private ReferencePoint getRandomNeighbour(ReferencePoint rp, double radius) {
		ReferencePoint res = new ReferencePoint(dim);
		double p[] = new double[dim];
		boolean positive;
		do {
			positive = true;
			p = Geometry.randomPointOnSphere(dim, radius);
			for (int i = 0; i < dim; i++) {
				p[i] += rp.getDim(i);
				if (p[i] < 0) {
					positive = false;
					break;
				}
			}
			p = Geometry.normalize(p);
		} while (!positive);
		for(int i=0; i<dim; i++){
			res.setDim(i, p[i]);
		}
		return res;
	}

}
