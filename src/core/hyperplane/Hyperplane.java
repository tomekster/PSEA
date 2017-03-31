package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;

import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
import solutionRankers.NonDominationRanker;
import utils.Geometry;
import utils.NSGAIIIRandom;

public class Hyperplane {

	protected ArrayList<ReferencePoint> referencePoints;
	protected int dim;

	public Hyperplane(int M) {
		this.dim = M;
		referencePoints = new ArrayList<ReferencePoint>();
		generateReferencePoints();
		for (ReferencePoint rp : referencePoints) {
			for (int i = 0; i < rp.getNumDimensions(); i++) {
				rp.setDim(i, Double.max(Geometry.EPS, rp.getDim(i)));
			}
		}
	}
	
	public Hyperplane(int M, int p) {
		this.dim = M;
		referencePoints = new ArrayList<ReferencePoint>();
		generateReferencePoints(p);
		for (ReferencePoint rp : referencePoints) {
			for (int i = 0; i < rp.getNumDimensions(); i++) {
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
	
	private void generateReferencePoints(int p) {
		ArrayList<ReferencePoint> boundaryLayer = new ArrayList<>();
		generateRecursive(new ReferencePoint(dim), 1.0 / p, 0, p, boundaryLayer);
		referencePoints.addAll(boundaryLayer);
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

	/**
	 * Set Niche count of every RP to 0 and clear list o associated solutions
	 */
	public void resetAssociations() {
		for (ReferencePoint rp : referencePoints) {
			rp.resetAssociation();
		}
	}
	
	public void associate(Population nichedSolutions, Population lastFrontSolutions){
		resetAssociations();
		associate(nichedSolutions, false);
		associate(lastFrontSolutions, true);
	}
	
	public void associate(Population population, boolean lastFront) {
		for(Solution s : population.getSolutions()){
			double minDist = Double.MAX_VALUE;
			ReferencePoint bestRefPoint = null;
			assert !referencePoints.isEmpty();
			for (ReferencePoint curRefPoint : referencePoints) {
				double dist = Geometry.pointLineDist(s.getObjectives(), curRefPoint.getDim());
				assert dist != Double.NaN;
				if (dist < minDist) {
					minDist = dist;
					bestRefPoint = curRefPoint;
				}
			}
			if(bestRefPoint == null){
				System.out.println("Best ref point error");
			}
			//TODO
			assert bestRefPoint != null;
			if(lastFront){
				bestRefPoint.addLastFrontAssociation(new Association(s, minDist));
			} else{
				bestRefPoint.addNichedAssociation(new Association(s, minDist));
			}
		}
	}

	public int getDim() {
		return this.dim;
	}
	
	public ArrayList<ReferencePoint> getReferencePoints() {
		return this.referencePoints;
	}
	
	public void setReferencePoints(ArrayList <ReferencePoint> rp){
		this.referencePoints = rp;
	}

	public int getNumNiched() {
		int res = 0;
		for(ReferencePoint rp : referencePoints){
			for(Association a : rp.getNichedAssociationsQueue()){
				if( ! a.getSolution().isDominated() ){
					res ++;
					break;
				}
			}
		}
		return res;
	}
}
