package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;

import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
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
			assert bestRefPoint != null;
			if(lastFront){
				bestRefPoint.addLastFrontAssociation(new Association(s, minDist));
			} else{
				bestRefPoint.addNichedAssociation(new Association(s, minDist));
			}
		}
	}

	public void modifySolutionDirections(int generation, Population pop, int totalNumGenerations, int populationSize) {
		resetAssociations();
		associate(pop,false);
		
		assert referencePoints.size() == populationSize - (referencePoints.size() % 2);
		assert pop.size() == populationSize;
		
		Population top50solutions = new Population();
		for(int i=0; i<populationSize/2; i++){
			top50solutions.addSolution(pop.getSolution(i));
		}

		double alpha = (double) generation / totalNumGenerations;
		//TODO arbitrary function - can be modified
		double radius = 0.25 * (1 - alpha);
		
		ArrayList<ReferencePoint> newReferencePoints = new ArrayList<>();
		ArrayList<ReferencePoint> associatedWithTop50 = new ArrayList<>();
		ArrayList<ReferencePoint> notAssociatedWithTop50 = new ArrayList<>();

		int numAssociations = 0;
		for (ReferencePoint rp : referencePoints) {
			boolean associated = false;
			numAssociations += rp.getNichedAssociationsQueue().size();
			for (Association as : rp.getNichedAssociationsQueue()) {
				if (isTop50(as.getSolution(), top50solutions)) {
					associated = true;
					break;
				}
			}

			if (associated) {
				associatedWithTop50.add(rp);
			} else {
				notAssociatedWithTop50.add(rp);
			}
		}
		assert numAssociations == populationSize;
		assert associatedWithTop50.size() + notAssociatedWithTop50.size() == referencePoints.size();
		assert!associatedWithTop50.isEmpty();
		assert!notAssociatedWithTop50.isEmpty();

		Collections.shuffle(associatedWithTop50);
		Collections.shuffle(notAssociatedWithTop50);

		for (int i = 0; i < Integer.min(populationSize / 2, notAssociatedWithTop50.size()); i++) {
			int associatedId = NSGAIIIRandom.getInstance().nextInt(associatedWithTop50.size());
			int notAssociatedId = NSGAIIIRandom.getInstance().nextInt(notAssociatedWithTop50.size());
			newReferencePoints.add(Geometry.getRandomNeighbour(dim, associatedWithTop50.get(associatedId), radius));
			notAssociatedWithTop50.remove(notAssociatedId);
		}
		newReferencePoints.addAll(associatedWithTop50);
		newReferencePoints.addAll(notAssociatedWithTop50);

		assert newReferencePoints.size() == populationSize - (referencePoints.size() % 2);

		this.referencePoints = newReferencePoints;
	}

	private boolean isTop50(Solution candidate, Population top50) {
		for (Solution s : top50.getSolutions()) {
			if(candidate.sameSolution(s)){
				return true;
			}
		}
		return false;
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

	public double getNumNiched() {
		int res = 0;
		for(ReferencePoint rp : referencePoints){
			 res += rp.getNicheCount() > 0 ? 1 : 0;
		}
		return res;
	}
}
