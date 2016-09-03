package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;

import core.Population;
import core.Solution;
import utils.NSGAIIIRandom;

public class SolutionDirections extends Hyperplane {

	public SolutionDirections(int M) {
		super(M);
	}
	
	/**
	 * Set Niche count of every RP to 0 and clear list o associated solutions
	 */
	public void resetAssociations() {
		for (ReferencePoint rp : referencePoints) {
			rp.resetAssociation();
		}
	}
	
	public void modifySolutionDirections(int generation, int totalNumGenerations, int populationSize, Population top50ChebyshevSolutions) {
		assert referencePoints.size() == populationSize - (referencePoints.size() % 2);
		assert top50ChebyshevSolutions.size() == populationSize/2;
		
		double alpha = (double) generation / totalNumGenerations;
		double radius = 0.25 * (1 - alpha);
		ArrayList<ReferencePoint> newReferencePoints = new ArrayList<>();

		ArrayList <ReferencePoint> associatedWithTop50 = new ArrayList<>();
		ArrayList <ReferencePoint> notAssociatedWithTop50 = new ArrayList<>(); 
		
		int numAssociations = 0;
		for (ReferencePoint rp : referencePoints){
			boolean associated = false;
			numAssociations += rp.getAssociatedSolutionsQueue().size();
			for(Association as : rp.getAssociatedSolutionsQueue()){
				if(isTop50(as.getSolution(), top50ChebyshevSolutions)){
					associated = true;
					break;
				}
			}
			
			if(associated){
				associatedWithTop50.add(rp);
			} else{
				notAssociatedWithTop50.add(rp);
			}
		}
		
		assert numAssociations == populationSize;
		assert associatedWithTop50.size() + notAssociatedWithTop50.size() == referencePoints.size();
		assert !associatedWithTop50.isEmpty();
		assert !notAssociatedWithTop50.isEmpty();
		
		Collections.shuffle(associatedWithTop50);
		Collections.shuffle(notAssociatedWithTop50);

		for(int i=0; i<Integer.min(populationSize/2, notAssociatedWithTop50.size()); i++){
			int associatedId = NSGAIIIRandom.getInstance().nextInt(associatedWithTop50.size());
			int notAssociatedId = NSGAIIIRandom.getInstance().nextInt(notAssociatedWithTop50.size());
			newReferencePoints.add(getRandomNeighbour(associatedWithTop50.get(associatedId), radius));
			notAssociatedWithTop50.remove(notAssociatedId);
		}
		newReferencePoints.addAll(associatedWithTop50);
		newReferencePoints.addAll(notAssociatedWithTop50);
		
		assert newReferencePoints.size() == populationSize - (referencePoints.size() % 2);
		
		this.referencePoints = newReferencePoints;
	}

	private boolean isTop50(Solution candidate, Population top50) {
		for(Solution s : top50.getSolutions()){
			if(candidate.equals(s)){
				return true;
			}
		}
		return false;
	}
}
