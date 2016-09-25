package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import core.Population;
import core.Solution;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import utils.Geometry;
import utils.Pair;
import utils.RACS;

public class ChebyshevDirections extends Hyperplane{
	private final static Logger LOGGER = Logger.getLogger(ChebyshevDirections.class.getName());
	
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

	public boolean modifyChebyshevDirections(int generation, int totalNumGenerations, PreferenceCollector pc) {
		//double alpha = (double) generation / totalNumGenerations;
		ArrayList<ReferencePoint> coherentReferencePoints = new ArrayList<>();
		ArrayList<ReferencePoint> incoherentReferencePoints = new ArrayList<>();

		for (ReferencePoint rp : referencePoints){ 
			if(rp.isCoherent()){
				coherentReferencePoints.add(rp);
			} else{
				incoherentReferencePoints.add(rp);
			}
		}
		//TODO - NOTE maybe should be handled differently - for now false means "Do not continue execution"
		if(coherentReferencePoints.isEmpty()){
			LOGGER.info("No coherent chebyshev points");
			return false;
		}

		//Find mass center of coherent points
		double[] massCenter = new double[coherentReferencePoints.get(0).getNumDimensions()];
		double[] min = new double[coherentReferencePoints.get(0).getNumDimensions()];
		double[] max = new double[coherentReferencePoints.get(0).getNumDimensions()];
		for(int i=0; i<coherentReferencePoints.get(0).getNumDimensions(); i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(ReferencePoint rp : coherentReferencePoints){
			for(int i=0; i< rp.getNumDimensions(); i++){
				min[i] = Math.min(min[i], rp.getDim(i));
				max[i] = Math.max(max[i], rp.getDim(i));
				massCenter[i] += rp.getDim(i);
			}
		}
		for(int i=0; i < massCenter.length; i++){
			System.out.print(Math.round(min[i] * 100)/100.0 + ", ");
		}
		System.out.println();
		for(int i=0; i < massCenter.length; i++){
			massCenter[i] /= coherentReferencePoints.size();
			System.out.print(Math.round(massCenter[i] * 100)/100.0 + ", ");
		}
		System.out.println();
		for(int i=0; i < massCenter.length; i++){
		System.out.print(Math.round(max[i] * 100)/100.0 + ", ");
		}
		System.out.println();
		
		if(!RACS.checkCoherence(new ReferencePoint(massCenter), pc)){
			LOGGER.info("Chebyshef points center of mass is not coherent");
		}
		//double radius = 0.25 * (1 - alpha);

		//Collections.shuffle(newReferencePoints);
		for(ReferencePoint rp : incoherentReferencePoints){
			ReferencePoint newCoherentRP = coherentBinarySearch(massCenter, rp.getDim(), 0, 1, pc);
			
			/*
			ReferencePoint newNeighbour = getRandomNeighbour(centralRefPoint, radius);
			if(!RACS.checkCoherence(newNeighbour, pc)){
				count++;
				newNeighbour = coherentBinarySearch(centralRefPoint.getDim(), newNeighbour.getDim(), 0, 1, pc);
			}
			*/
			
			coherentReferencePoints.add(newCoherentRP);
		}
				
		this.referencePoints = coherentReferencePoints;
		return true;
	}
	
	private ReferencePoint coherentBinarySearch(double[] begP, double[] endP, double beg, double end, PreferenceCollector pc) {
		//TODO - NOTE just arbitrary threshold - can be customized
		double thresh = 1E-6;
		while(end - beg > thresh){
			double mid = (beg+end)/2;
			double [] midDim = Geometry.linearCombination(begP, endP, mid);
			ReferencePoint midRefPoint= new ReferencePoint(midDim);
			if(RACS.checkCoherence(midRefPoint, pc)){
				end = mid;
			} else {
				beg = mid;
			}
		}
		ReferencePoint res = new ReferencePoint(Geometry.linearCombination(begP, endP, end)); 
		return res;
	}

}
