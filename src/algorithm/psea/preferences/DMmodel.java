package algorithm.psea.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import artificialDM.AsfDM;
import utils.math.structures.Pair;

public class DMmodel implements Comparator<Solution>{
	
	private ASFBundle asfBundle;

	public DMmodel(double [] idealPoint){
		asfBundle = new ASFBundle(idealPoint);
	}
	
	public Population sortSolutions(Population pop) {
		HashMap<Solution, Double> bordaPointsMap = getBordaPointsForSolutions(pop);
		
		ArrayList<Pair<Solution, Double>> pairs = new ArrayList<Pair<Solution, Double>>();

		for (Solution s : bordaPointsMap.keySet()) {
			pairs.add(new Pair<Solution, Double>(s, bordaPointsMap.get(s)));
		}

		Collections.sort(pairs, new Comparator<Pair<Solution, Double>>() {
			@Override
			public int compare(final Pair<Solution, Double> o1, final Pair<Solution, Double> o2) {
				return Double.compare(o2.second, o1.second); // Sort DESC by Borda points
			}
		});

		Population res = new Population();
		for (int i = 0; i < pairs.size(); i++) {
			res.addSolution(new Solution(pairs.get(i).first));
		}
		return res;
	}

	public HashMap<Solution, Double> getBordaPointsForSolutions(Population pop) {
		HashMap<Solution, Double> bordaPointsMap = new HashMap<>();
		for(int i=0; i < pop.getSolutions().size(); i++){
			bordaPointsMap.put(pop.getSolution(i), .0);
		}
		
		for (AsfDM adm : asfBundle.getAsfDMs()) {
			adm.sort(pop.getSolutions());
			ArrayList <Solution> ranking = pop.getSolutions();
			assert ranking.size() == pop.size();
			for (int i = 0; i < ranking.size(); i++) {
				Solution s = ranking.get(i);
				bordaPointsMap.put(s, bordaPointsMap.get(s) + ((double) (ranking.size() - i) )/(adm.getNumViolations() + 1));
			}
		}
		return bordaPointsMap;
	}

	@Override
	public int compare(Solution s1, Solution s2) {
		double v1=0, v2=0;
		for(AsfDM adm : asfBundle.getAsfDMs()){
			int cmp = adm.compare(s1, s2);
			if(cmp < 0) v1++;
			else if(cmp >0) v2++;
		}
		return Double.compare(v1, v2);
	}
	
	public ASFBundle getAsfBundle(){
		return asfBundle;
	}

	public void addAsfDM(AsfDM asfDM){
		asfBundle.addAsfDM(asfDM);
	}
	
	public void addAsfDM(double lambda[]){
		asfBundle.addAsfDM(lambda);
	}
	
	public void clearDMs() {
		asfBundle.clear();
	}
}
