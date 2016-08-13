package core.hyperplane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import core.Population;
import core.Solution;
import utils.Geometry;
import utils.MyComparator;

public class Hyperplane {

	private ArrayList<ReferencePoint> referencePoints;
	private int dim;

	public Hyperplane(int M) {
		this.dim = M;
		referencePoints = new ArrayList<ReferencePoint>();
		generateReferencePoints();
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

	/**
	 * Set Niche count of every RP to 0 and clear list o associated solutions
	 */
	public void resetAssociations() {
		for (ReferencePoint rp : referencePoints) {
			rp.resetAssociation();
		}
	}

	public boolean modifyReferencePoints(int generation, int totalNumGenerations) {
		double alpha = (double) generation / totalNumGenerations;
		ArrayList<ReferencePoint> newReferencePoints = new ArrayList<>();

		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(MyComparator.referencePointComparatorDesc);
		for (ReferencePoint rp : referencePoints){ 
			if(rp.isCoherent()){
				newReferencePoints.add(rp);
				refPQ.add(rp);
			}
		}
		if(refPQ.isEmpty() || refPQ.size() == referencePoints.size()){
			return false;
		}
		
		int numIncoherentPoints = referencePoints.size() - newReferencePoints.size();
		//double radius = NSGAIIIRandom.getInstance().nextDouble() * (Math.E - Math.exp(alpha)) / (Math.E - 1) * 0.5;
		double radius = 0.25 * (1 - alpha);
		for (int i = 0; i < numIncoherentPoints; i++) {
			ReferencePoint largestNicheCountRefPoint = refPQ.poll();
			ReferencePoint n = getRandomNeighbour(largestNicheCountRefPoint, radius);
			newReferencePoints.add(n);
			largestNicheCountRefPoint.decrNicheCount();
			refPQ.add(largestNicheCountRefPoint);
		}
		this.referencePoints = newReferencePoints;
		return true;
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
		res.setDimensions(p);
		return res;
	}

	public ArrayList<Population> getFrontsByReferencePoitnRankings(Population pop, int popSize) {
		
		int numCoherent = 0;
		for(ReferencePoint rp : referencePoints){
			if(!rp.isCoherent()){
				continue;
			}
			numCoherent += 1;
			rp.buildRanking(pop);
		}
		
		Set <Solution> usedSolutions = new HashSet<Solution>();
		ArrayList <Population> fronts = new ArrayList <Population>();
		int frontId = -1;
		while(usedSolutions.size() < popSize){
			frontId += 1;
			Population front = new Population();
			for(ReferencePoint rp : referencePoints){
				if(rp.isCoherent()){
					Solution s = rp.getRankingElement(frontId);
					if(usedSolutions.contains(s)){
						continue;
					} else{
						front.addSolution(s);
						usedSolutions.add(s);
					}
				}
			}
			if(!front.empty()){
				fronts.add(front);
			}
		}
		return fronts;
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
