package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.points.Lambda;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.LambdaCVRanker;
import utils.Geometry;
import utils.GradientLambdaSearch;
import utils.NSGAIIIRandom;

public class ASFBundle {
	private final static Logger LOGGER = Logger.getLogger(ASFBundle.class.getName());

	private static final double CONVERGENCE_THRESHOLD = 0.001;
	
	private static ASFBundle instance = null;

	private double[] referencePoint;

	private int numObjectives;
	private int bundleSize;
	private ArrayList <Lambda> lambdas;
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
		
		lambdas = new ArrayList<>();
		for (int i=0; i<bundleSize; i++) {
			lambdas.add(new Lambda(Geometry.getRandomVectorSummingTo1(numObjectives)));
		}
		GLS = new GradientLambdaSearch(numObjectives);
	}

	/**
	 * Checks if chebyshev's function with given lambdaPoint can reproduce all comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public int evaluateLambda(Lambda l) {
		double lambda[] = l.getDim();
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PreferenceCollector.getInstance().getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = ChebyshevRanker.eval(better, referencePoint, lambda);
			double b = ChebyshevRanker.eval(worse, referencePoint, lambda);

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

	public ArrayList<Lambda> getLambdas() {
		return this.lambdas;
	}

	public void nextGeneration() {
		for(int i=0; i<bundleSize; i++) { 
			lambdas.add(new Lambda(Geometry.getRandomVectorSummingTo1(this.numObjectives))); //Add random lambdas to current bundle to increase the diversity 
		}
		ArrayList <Lambda> newLambdas = selectNewLambdas(GLS.improveLambdas(this));
		LOGGER.log(Level.INFO, "Best/worse CV:" + newLambdas.stream().mapToInt(Lambda::getNumViolations).min().getAsInt() + "/" + newLambdas.stream().mapToInt(Lambda::getNumViolations).max().getAsInt());
		this.lambdas = newLambdas;
	}
	
	protected ArrayList <Lambda> selectNewLambdas(ArrayList <Lambda> lambdas) {
		for(Lambda l : lambdas){
			double lambda[] = l.getDim();
			if(NSGAIIIRandom.getInstance().nextDouble() < 0.3){
				//TODO
				lambda = Geometry.getRandomNeighbour(lambda, 0.1); //Mutate lambda just a little bit randomly
			}
			l.setDim(lambda);
			evaluateLambda(l);
		}
		Collections.sort(lambdas, new LambdaCVRanker());
		return new ArrayList<Lambda>(lambdas.subList(0, bundleSize));
	}
	
	public boolean converged(){
		double min[] = new double[numObjectives];
		double max[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(Lambda lambda : lambdas){
			for(int i=0; i<numObjectives; i++){
				if(lambda.getDim(i) < min[i]){ min[i] = lambda.getDim(i); }
				if(lambda.getDim(i) > max[i]){ max[i] = lambda.getDim(i); }
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
		for(Lambda lambda : lambdas){
			res += lambda.toString() + "\n" + lambda.getNumViolations() + "\n";
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
		for(Lambda lambda : lambdas){
			for(int i=0; i<numObjectives; i++){
				res[i] += lambda.getDim(i);
			}
		}
		for(int i=0; i<numObjectives; i++){
			res[i] /= lambdas.size();
		}
		return res;
	}
}
