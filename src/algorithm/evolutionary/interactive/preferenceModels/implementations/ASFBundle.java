package algorithm.evolutionary.interactive.preferenceModels.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import algorithm.evolutionary.interactive.artificialDM.implementations.AsfDM;
import algorithm.evolutionary.interactive.elicitationModels.ElicitatedPreferences;
import algorithm.evolutionary.interactive.preferenceModels.PreferenceModel;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.psea.GradientLambdaSearch;
import algorithm.rankers.ConstraintViolationRanker;
import utils.math.Geometry;
import utils.math.MyRandom;
import utils.math.structures.Pair;

public class ASFBundle extends PreferenceModel {
	//private final static Logger LOGGER = Logger.getLogger(ASFBundle.class.getName());
	
	private int bundleSize;
	
	private ArrayList <AsfDM> asfDMs;
	private GradientLambdaSearch GLS;
	
	private double[] referencePoint;
	
	public ASFBundle(double refPoint[], int bundleSize) {
		this.referencePoint = refPoint;
		this.bundleSize = bundleSize;
		
		asfDMs = new ArrayList<>();
		
		for (int i=0; i<bundleSize; i++) {
			asfDMs.add(new AsfDM(referencePoint, Geometry.getRandomVectorSummingTo1(refPoint.length)));
		}
		GLS = new GradientLambdaSearch(refPoint.length);
	}

	public void updateDMs(boolean mutation) {
		for(int i=0; i<bundleSize; i++) { 
			asfDMs.add(new AsfDM(referencePoint, Geometry.getRandomVectorSummingTo1(referencePoint.length))); //Add random lambdas to current bundle to increase the diversity 
		}
		ArrayList <AsfDM> newPreferenceModels = selectNewAsfDMs(GLS.improvePreferenceModels(this),mutation);
		this.asfDMs = newPreferenceModels;
	}
	
	protected ArrayList <AsfDM> selectNewAsfDMs(ArrayList <AsfDM> asfDMs, boolean mutation) {
		for(AsfDM asfDM : asfDMs){
			
			if(mutation){
				double lambda[] = asfDM.getLambda();
				if(MyRandom.getInstance().nextDouble() < lambdaMutationProbability){
					//TODO - mutation of lambdas (randomNeighbour)
					lambda = Geometry.getRandomNeighbour(lambda, lambdaMutationNeighborhoodRadius); //Mutate lambda just a little bit randomly
				}
				asfDM.setLambda(lambda);
			}
			
			Comparisons.getInstance().evaluateDM(asfDM);
		}
		Collections.sort(asfDMs, new ConstraintViolationRanker());
		return new ArrayList<AsfDM>(asfDMs.subList(0, bundleSize));
	}
	
	public boolean converged(double convergenceThreshold){
		double min[] = new double[this.referencePoint.length];
		double max[] = new double[this.referencePoint.length];
		for(int i=0; i<this.referencePoint.length; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(AsfDM asfDM : asfDMs){
			for(int i=0; i<this.referencePoint.length; i++){
				if(asfDM.getLambda(i) < min[i]){ min[i] = asfDM.getLambda(i); }
				if(asfDM.getLambda(i) > max[i]){ max[i] = asfDM.getLambda(i); }
			}
		}
		
		for(int i=0; i<this.referencePoint.length; i++){
			if( max[i] - min[i] > convergenceThreshold) return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		String res="";
		for(AsfDM asfDM : asfDMs){
			res += asfDM.toString() + "\n" + asfDM.getNumViolations() + "\n";
		}
		return res;
	}
	
	public int size(){
		return bundleSize;
	}
	
	public double[] getReferencePoint(){
		return referencePoint;
	}
	
	public void setReferencePoint(double [] refPoint){
		this.referencePoint = refPoint;
	}
	
	public ArrayList<AsfDM> getAsfDMs() {
		return this.asfDMs;
	}

	//TODO - average or inversed average?
	public double[] getAverageLambdaPoint() {
		double res[] = new double[referencePoint.length];
		for(AsfDM lambda : asfDMs){
			for(int i=0; i<referencePoint.length; i++){
				res[i] += lambda.getLambda(i);
			}
		}
		for(int i=0; i<referencePoint.length; i++){
			res[i] /= asfDMs.size();
		}
		return res;
	}

	public void addAsfDM(AsfDM asfDM) {
		this.asfDMs.add(asfDM);
	}
	
	public void addAsfDM(double[] lambda) {
		this.asfDMs.add(new AsfDM(this.referencePoint, lambda));
	}
	
	public ASFBundle copy(){
		ASFBundle res = new ASFBundle(this.referencePoint, this.bundleSize);
		res.setReferencePoint(this.referencePoint.clone());
		res.asfDMs.clear();
		for(AsfDM asfDM : asfDMs){
			res.addAsfDM(asfDM.getLambda().clone());
		}
		return res;
	}

	public void clear() {
		asfDMs.clear();
	}
	
	public Population <? extends Solution> sortSolutions(Population <? extends Solution> pop) {
		HashMap<Solution, Double> bordaPointsMap = getBordaPointsForSolutions(pop);
		
		ArrayList<Pair<Solution, Double>> pairs = new ArrayList<>();

		for (Solution s : bordaPointsMap.keySet()) {
			pairs.add(new Pair<Solution, Double>(s, bordaPointsMap.get(s)));
		}

		Collections.sort(pairs, new Comparator<Pair<Solution, Double>>() {
			@Override
			public int compare(final Pair<Solution, Double> o1, final Pair<Solution, Double> o2) {
				return Double.compare(o2.second, o1.second); // Sort DESC by Borda points
			}
		});

		Population <Solution> res = new Population <Solution> ();
		for (int i = 0; i < pairs.size(); i++) {
			res.addSolution( (Solution) pairs.get(i).first.copy());
		}
		return res;
	}

	public HashMap<Solution, Double> getBordaPointsForSolutions(Population <? extends Solution> pop) {
		HashMap<Solution, Double> bordaPointsMap = new HashMap<>();
		for(int i=0; i < pop.getSolutions().size(); i++){
			bordaPointsMap.put(pop.getSolution(i), .0);
		}
		
		for (AsfDM adm : getAsfDMs()) {
			adm.sort(pop.getSolutions());
			ArrayList <? extends Solution> ranking = pop.getSolutions();
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
		int v1=0, v2=0;
		for(AsfDM adm : getAsfDMs()){
			int cmp = adm.compare(s1, s2);
			if(cmp < 0) v1++;
			else if(cmp >0) v2++;
		}
		return Integer.compare(v1, v2);
	}

	@Override
	public void updateModel(ElicitatedPreferences ep) {
		// TODO Auto-generated method stub
		
	}
}
