package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import core.Population;
import core.Solution;
import utils.Pair;

public class ChebyshevDirections extends Hyperplane{
	public ChebyshevDirections(int M) {
		super(M);
	}
	
	public ArrayList<Population> getFrontsByReferencePointRankings(Population pop, int popSize) {
		for(ReferencePoint cd : referencePoints){
			if(!cd.isCoherent()){
				continue;
			}
			cd.buildSolutionsRanking(pop);
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
				if(usedSolutions.size() == popSize){
					break;
				}
			}
			if(!front.empty()){
				fronts.add(front);
			}
		}
		return fronts;
	}
	
	public Population selectKChebyshevPoints(Population pop, int k) {
		boolean anyIsCoherent = false;
		HashMap <Solution, Integer> map = new HashMap();
		for(ReferencePoint cd : referencePoints){
			if(!cd.isCoherent()){
				continue;
			}
			anyIsCoherent = true;
			cd.buildSolutionsRanking(pop);
			assert cd.getRanking().size() == pop.size();
			
			for(int i=0; i < cd.getRanking().size(); i++){
				Solution s = cd.getRanking().get(i);
				if(!map.containsKey(s)){
					map.put(s,0);
				}
				map.put(s, map.get(s) + cd.getRanking().size() - i);
			}
		}
		assert anyIsCoherent;

		ArrayList <Pair<Solution, Integer>> pairs = new ArrayList();
		
		for(Solution s : map.keySet()){
			pairs.add(new Pair(s,map.get(s)));
		}
		
		Collections.sort(pairs, new Comparator<Pair<Solution, Integer>>() {
			@Override
			public int compare(final Pair<Solution, Integer> o1, final Pair<Solution, Integer> o2 ){
				return Integer.compare(o2.second,o1.second); //Sort DESC
			}
		});
		
		ArrayList <Population> fronts = new ArrayList <Population>();
		Population res = new Population();
		for(int i=0; i<k; i++){
			res.addSolution(pairs.get(i).first);
		}
		return res;
	}
	
	public boolean modifyChebyshevDirections(int generation, int totalNumGenerations) {
		double alpha = (double) generation / totalNumGenerations;
		ArrayList<ReferencePoint> newReferencePoints = new ArrayList<>();

		for (ReferencePoint rp : referencePoints){ 
			if(rp.isCoherent()){
				newReferencePoints.add(rp);
			}
		}
		if(newReferencePoints.isEmpty()){
			return false;
		}
		
		int numIncoherentPoints = referencePoints.size() - newReferencePoints.size();
		double radius = 0.25 * (1 - alpha);

		Collections.shuffle(newReferencePoints);
		ArrayList <ReferencePoint> neighbours = new ArrayList<>();
		for(int i=0; i<numIncoherentPoints; i++){
			neighbours.add(getRandomNeighbour(newReferencePoints.get(i % newReferencePoints.size()), radius));
		}
		
		this.referencePoints = newReferencePoints;
		this.referencePoints.addAll(neighbours);
		return true;
	}
		
}
