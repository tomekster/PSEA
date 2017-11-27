package algorithm.implementations.psea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import algorithm.evolutionary.interactive.artificialDM.AsfDM;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.rankers.ConstraintViolationRanker;
import utils.math.AsfFunction;
import utils.math.Geometry;
import utils.math.MyRandom;
import utils.math.structures.Pair;
import utils.math.structures.Point;

public class ASFBundle implements Comparator<Solution> {
	//private final static Logger LOGGER = Logger.getLogger(ASFBundle.class.getName());
	
	private int bundleSize;
	
	private ArrayList <AsfDM> asfDMs;
	private GradientLambdaSearch GLS;
	
	private double lambdaMutationProbability;
	private double lambdaMutationNeighborhoodRadius;
	
	private Point referencePoint;
	private double asfDmRho;
	
	public ASFBundle(Point refPoint, int bundleSize, double lambdaMutationProbability, double lambdaMutationNeighborhoodRadius, double asfDmRho) {
		this.referencePoint = refPoint;
		this.bundleSize = bundleSize;
		
		this.lambdaMutationProbability = lambdaMutationProbability;
		this.lambdaMutationNeighborhoodRadius = lambdaMutationNeighborhoodRadius;
		this.asfDmRho = asfDmRho;
		
		asfDMs = new ArrayList<>();
		
		for (int i=0; i<bundleSize; i++) {
			asfDMs.add(new AsfDM(new AsfFunction(Geometry.getRandomVectorSummingTo1(refPoint.getNumDim()), asfDmRho, referencePoint)));
		}
		GLS = new GradientLambdaSearch(refPoint.getNumDim());
	}

	public void updateDMs(boolean mutation, ArrayList <Comparison> pc) {
		for(int i=0; i<bundleSize; i++) { 
			asfDMs.add(new AsfDM( new AsfFunction(Geometry.getRandomVectorSummingTo1(referencePoint.getNumDim()), asfDmRho, referencePoint))); //Add random lambdas to current bundle to increase the diversity 
		}
		ArrayList <AsfDM> newPreferenceModels = selectNewAsfDMs(GLS.improvePreferenceModels(this, pc),mutation, pc);
		this.asfDMs = newPreferenceModels;
	}
	
	protected ArrayList <AsfDM> selectNewAsfDMs(ArrayList <AsfDM> asfDMs, boolean mutation, ArrayList <Comparison> pc) {
		for(AsfDM asfDM : asfDMs){
			
			if(mutation){
				double lambda[] = asfDM.getAsfFunction().getLambda();
				if(MyRandom.getInstance().nextDouble() < lambdaMutationProbability){
					//TODO - mutation of lambdas (randomNeighbour)
					lambda = Geometry.getRandomNeighbour(lambda, lambdaMutationNeighborhoodRadius); //Mutate lambda just a little bit randomly
				}
				asfDM.getAsfFunction().setLambda(lambda);
			}
			asfDM.verifyModel(pc);
		}
		Collections.sort(asfDMs, new ConstraintViolationRanker());
		return new ArrayList<AsfDM>(asfDMs.subList(0, bundleSize));
	}
	
	public boolean converged(double convergenceThreshold){
		double min[] = new double[this.referencePoint.getNumDim()];
		double max[] = new double[this.referencePoint.getNumDim()];
		for(int i=0; i<this.referencePoint.getNumDim(); i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(AsfDM asfDM : asfDMs){
			for(int i=0; i < this.referencePoint.getNumDim(); i++){
				if(asfDM.getAsfFunction().getLambda(i) < min[i]){ min[i] = asfDM.getAsfFunction().getLambda(i); }
				if(asfDM.getAsfFunction().getLambda(i) > max[i]){ max[i] = asfDM.getAsfFunction().getLambda(i); }
			}
		}
		
		for(int i=0; i < this.referencePoint.getNumDim(); i++){
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
	
	public Point getReferencePoint(){
		return referencePoint;
	}
	
	public void setReferencePoint(Point refPoint){
		this.referencePoint = refPoint;
	}
	
	public ArrayList<AsfDM> getAsfDMs() {
		return this.asfDMs;
	}

	//TODO - average or inversed average?
	public double[] getAverageLambdaPoint() {
		double res[] = new double[referencePoint.getNumDim()];
		for(AsfDM asfDm : asfDMs){
			for(int i=0; i < referencePoint.getNumDim(); i++){
				res[i] += asfDm.getAsfFunction().getLambda(i);
			}
		}
		for(int i=0; i<referencePoint.getNumDim(); i++){
			res[i] /= asfDMs.size();
		}
		return res;
	}

	public void addAsfDM(AsfDM asfDM) {
		this.asfDMs.add(asfDM);
	}
	
	public void addAsfDM(double[] lambda) {
		this.asfDMs.add(new AsfDM(new AsfFunction(lambda, asfDmRho, referencePoint)));
	}
	
	public ASFBundle copy(){
		ASFBundle res = new ASFBundle(this.referencePoint, this.bundleSize, this.lambdaMutationProbability, this.lambdaMutationNeighborhoodRadius, this.asfDmRho);
		res.setReferencePoint(this.referencePoint.copy());
		res.asfDMs.clear();
		for(AsfDM asfDM : asfDMs){
			res.addAsfDM(asfDM.getAsfFunction().getLambda().clone());
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
	
	public double getAsfDmRho(){
		return this.asfDmRho;
	}
	
	public AsfDM createAsfDm(double lambda[]){
		return new AsfDM(new AsfFunction(lambda, asfDmRho, this.referencePoint));
	}
}
