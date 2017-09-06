package algorithm.psea.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import algorithm.psea.AsfPreferenceModel;
import algorithm.psea.GradientLambdaSearch;
import algorithm.rankers.AsfRanker;
import algorithm.rankers.ConstraintViolationRanker;
import problems.Problem;
import utils.math.Geometry;
import utils.math.MyRandom;

public class ASFBundle {
	private final static Logger LOGGER = Logger.getLogger(ASFBundle.class.getName());

	private static final double CONVERGENCE_THRESHOLD = 0.001;
	
	private static ASFBundle instance = null;

	private double[] referencePoint;

	private int numObjectives;
	private int bundleSize;
	private ArrayList <AsfPreferenceModel> asfPreferenceModels;
	private GradientLambdaSearch GLS;
	
	protected ASFBundle(){
		// Exists only to defeat instantiation.
	}
	
	public static ASFBundle getInstance(){
		if (instance == null){
			instance = new ASFBundle();
		}
		return instance;
	}
	
	public void init(Problem problem) {
		this.numObjectives = problem.getNumObjectives();
		switch (numObjectives){
			case(3):
				this.bundleSize = 50;
			break;
			case(5):
				this.bundleSize = 60;
			break;
			case(8):
				this.bundleSize = 70;
			break;
		}
		this.referencePoint = problem.findIdealPoint();
		
		asfPreferenceModels = new ArrayList<>();
		for (int i=0; i<bundleSize; i++) {
			asfPreferenceModels.add(new AsfPreferenceModel(Geometry.getRandomVectorSummingTo1(numObjectives)));
		}
		GLS = new GradientLambdaSearch(numObjectives);
	}

	/**
	 * Checks if chebyshev's function with given lambdaPoint can reproduce all comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public int evaluatePreferenceModel(AsfPreferenceModel l) {
		double lambda[] = l.getLambda();
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PreferenceCollector.getInstance().getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = AsfRanker.eval(better, referencePoint, lambda);
			double b = AsfRanker.eval(worse, referencePoint, lambda);

			double eps = b-a;
			if(a >= b){
				numViolations++;
				double newPenalty = penalty * (1-eps);
				assert newPenalty >= penalty;
				penalty = newPenalty;
			} else if(a < b){
				double newReward = reward * (1+eps);
				assert newReward >= reward;
				reward = newReward;
			}
		}
		
		l.setReward(reward);
		l.setPenalty(penalty);
		l.setNumViolations(numViolations);
		return numViolations;
	}

	public ArrayList<AsfPreferenceModel> getPreferenceModels() {
		return this.asfPreferenceModels;
	}

	public void nextGeneration() {
		for(int i=0; i<bundleSize; i++) { 
			asfPreferenceModels.add(new AsfPreferenceModel(Geometry.getRandomVectorSummingTo1(this.numObjectives))); //Add random lambdas to current bundle to increase the diversity 
		}
		ArrayList <AsfPreferenceModel> newPreferenceModels = selectNewPreferenceModels(GLS.improvePreferenceModels(this));
//		LOGGER.log(Level.INFO, "Best/worse CV:" + newLambdas.stream().mapToInt(AsfPreferenceModel::getNumViolations).min().getAsInt() + "/" + newLambdas.stream().mapToInt(AsfPreferenceModel::getNumViolations).max().getAsInt());
		this.asfPreferenceModels = newPreferenceModels;
	}
	
	protected ArrayList <AsfPreferenceModel> selectNewPreferenceModels(ArrayList <AsfPreferenceModel> preferenceModels) {
		for(AsfPreferenceModel l : preferenceModels){
			double lambda[] = l.getLambda();
			if(MyRandom.getInstance().nextDouble() < 0.3){
				//TODO - mutation of lambdas (randomNeighbour)
				lambda = Geometry.getRandomNeighbour(lambda, 0.1); //Mutate lambda just a little bit randomly
			}
			l.setDim(lambda);
			evaluatePreferenceModel(l);
		}
		Collections.sort(preferenceModels, new ConstraintViolationRanker());
		return new ArrayList<AsfPreferenceModel>(preferenceModels.subList(0, bundleSize));
	}
	
	public boolean converged(){
		double min[] = new double[numObjectives];
		double max[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(AsfPreferenceModel asfPreferenceModel : asfPreferenceModels){
			for(int i=0; i<numObjectives; i++){
				if(asfPreferenceModel.getLambda(i) < min[i]){ min[i] = asfPreferenceModel.getLambda(i); }
				if(asfPreferenceModel.getLambda(i) > max[i]){ max[i] = asfPreferenceModel.getLambda(i); }
			}
		}
		
		for(int i=0; i<numObjectives; i++){
			if( max[i] - min[i] > CONVERGENCE_THRESHOLD) return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		String res="";
		for(AsfPreferenceModel asfPreferenceModel : asfPreferenceModels){
			res += asfPreferenceModel.toString() + "\n" + asfPreferenceModel.getNumViolations() + "\n";
		}
		return res;
	}
	
	public int getNumLambdas(){
		return bundleSize;
	}
	
	public double[] getReferencePoint(){
		return referencePoint;
	}

	public double[] getAverageLambdaPoint() {
		double res[] = new double[numObjectives];
		for(AsfPreferenceModel lambda : asfPreferenceModels){
			for(int i=0; i<numObjectives; i++){
				res[i] += lambda.getLambda(i);
			}
		}
		for(int i=0; i<numObjectives; i++){
			res[i] /= asfPreferenceModels.size();
		}
		return res;
	}
}
