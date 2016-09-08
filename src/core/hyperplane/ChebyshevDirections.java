package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import core.Population;
import core.Solution;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import utils.Geometry;
import utils.Pair;
import utils.RACS;

public class ChebyshevDirections extends Hyperplane{
	public ChebyshevDirections(int M) {
		super(M);
	}
	
	public Population selectKChebyshevPoints(Population pop, int k) {
		boolean anyIsCoherent = false;
		HashMap <Solution, Integer> map = new HashMap <Solution, Integer>();
		HashMap <ReferencePoint, ArrayList <Solution>> solutionRankings = new HashMap<ReferencePoint, ArrayList <Solution>>();
		for(ReferencePoint cd : referencePoints){
			if(!cd.isCoherent()){
				continue;
			}
			anyIsCoherent = true;
			ArrayList <Solution> ranking = buildSolutionsRanking(cd, pop); 
			solutionRankings.put(cd, ranking);
			assert ranking.size() == pop.size();
			for(int i=0; i < ranking.size(); i++){
				Solution s = ranking.get(i);
				if(!map.containsKey(s)){
					map.put(s,0);
				}
				map.put(s, map.get(s) + ranking.size() - i);
			}
		}
		assert anyIsCoherent;

		ArrayList <Pair<Solution, Integer>> pairs = new ArrayList <Pair<Solution, Integer>> ();
		
		for(Solution s : map.keySet()){
			pairs.add(new Pair <Solution, Integer> (s,map.get(s)));
		}
		
		Collections.sort(pairs, new Comparator<Pair<Solution, Integer>>() {
			@Override
			public int compare(final Pair<Solution, Integer> o1, final Pair<Solution, Integer> o2 ){
				return Integer.compare(o2.second,o1.second); //Sort DESC
			}
		});
		
		Population res = new Population();
		for(int i=0; i<k; i++){
			res.addSolution(pairs.get(i).first);
		}
		return res;
	}
	
	public ArrayList<Solution> buildSolutionsRanking(ReferencePoint cd, Population pop){
		ArrayList < Pair<Solution, Double> > solutionValuePairs = new ArrayList < Pair<Solution, Double>>();
		for(Solution s : pop.getSolutions()){
			double chebyshevValue = ChebyshevRanker.eval(s, null, Geometry.invert(cd.getDim()), cd.getRho());
			solutionValuePairs.add( new Pair <Solution, Double>(s, chebyshevValue));
		}
		Collections.sort(solutionValuePairs, new Comparator<Pair<Solution, Double>>(){
			@Override
			public int compare(final Pair<Solution, Double> o1, final Pair<Solution, Double> o2){
				//Sort pairs by Chebyshev Function value ascending
				return Double.compare(o1.second, o2.second);
			}
		});
		
		ArrayList <Solution> ranking = new ArrayList<Solution>();
		for(Pair<Solution, Double> p : solutionValuePairs){
			ranking.add(p.first);
		}
		assert ranking.size() == pop.size();
		return ranking;
	}

	public void modifyChebyshevDirections(int generation, int totalNumGenerations, PreferenceCollector pc) {
		double alpha = (double) generation / totalNumGenerations;
		ArrayList<ReferencePoint> newReferencePoints = new ArrayList<>();

		for (ReferencePoint rp : referencePoints){ 
			if(rp.isCoherent()){
				newReferencePoints.add(rp);
			}
		}

		int numIncoherentPoints = referencePoints.size() - newReferencePoints.size();
		double radius = 0.25 * (1 - alpha);

		Collections.shuffle(newReferencePoints);
		ArrayList <ReferencePoint> newNeighbours = new ArrayList<>();
		int count =0;
		for(int i=0; i<numIncoherentPoints; i++){
			ReferencePoint centralRefPoint = newReferencePoints.get(i % newReferencePoints.size());
			ReferencePoint newNeighbour = getRandomNeighbour(centralRefPoint, radius);
			
			if(!RACS.checkCoherence(newNeighbour, pc)){
				count++;
				newNeighbour = coherentBinarySearch(centralRefPoint, newNeighbour, 0, 1, pc);
			}
			
			newNeighbours.add(newNeighbour);
		}
		System.out.println(count);
				
		this.referencePoints = newReferencePoints;
		this.referencePoints.addAll(newNeighbours);
		System.out.println("New neigh: " + newNeighbours.size());
	}
	
	private ReferencePoint coherentBinarySearch(ReferencePoint begRP, ReferencePoint endRP, double beg, double end, PreferenceCollector pc) {
		//TODO - NOTE just arbitrary threshold - can be customized
		double thresh = 1E-6;
		while(end - beg > thresh){
			double mid = (beg+end)/2;
			double [] midDim = Geometry.linearCombination(begRP.getDim(), endRP.getDim(), mid);
			ReferencePoint midRefPoint= new ReferencePoint(midDim);
			if(RACS.checkCoherence(midRefPoint, pc)){
				end = mid;
			} else {
				beg = mid;
			}
		}
		ReferencePoint res = new ReferencePoint(Geometry.linearCombination(begRP.getDim(), endRP.getDim(), end)); 
		return res;
	}

}
