package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import core.points.ReferencePoint;
import core.points.Solution;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.LambdaCVRanker;
import utils.Geometry;
import utils.GradientLambdaSearch;
import utils.NSGAIIIRandom;
import utils.Pair;

public class Lambda {

	private final static Logger LOGGER = Logger.getLogger(Lambda.class.getName());

	private static Lambda instance = null;
	
	private int numObjectives;
	private int numLambdas;
	private ArrayList <ReferencePoint> lambdas;
	private GradientLambdaSearch GLS;
	
	protected Lambda(){
		// Exists only to defeat instantiation.
	}
	
	public static Lambda getInstance(){
		if (instance == null){
			instance = new Lambda();
		}
		return instance;
	}
	
	public void init(int numObjectives, int numLambdas) {
		this.numObjectives = numObjectives;
		this.numLambdas = numLambdas;
		lambdas = new ArrayList<>();
		for (int i=0; i<numLambdas; i++) {
			lambdas.add(this.getRandomLambda());
		}
		GLS = new GradientLambdaSearch(numObjectives);
	}
	
	private ReferencePoint getRandomLambda() {
		ArrayList <Double> breakPoints = new ArrayList<>();
		ArrayList <Double> dimensions = new ArrayList<>();
		breakPoints.add(0.0);
		breakPoints.add(1.0);
		for(int i=0; i<numObjectives-1; i++){ 
			breakPoints.add(NSGAIIIRandom.getInstance().nextDouble()); 
		}
		Collections.sort(breakPoints);

		for(int i=0; i < numObjectives; i++){
			dimensions.add(breakPoints.get(i+1) - breakPoints.get(i));
		}
		
		Collections.shuffle(dimensions);
		double dims[] = new double[this.numObjectives];
		for(int i=0; i<dimensions.size();i++){
			dims[i] = dimensions.get(i);
		}
		assert( Math.abs(Arrays.stream(dims).sum() - 1) < Geometry.EPS);
		ReferencePoint rp = new ReferencePoint(dims);
		return rp;
	}

	/**
	 * Checks if chebyshev's function with given lambda can reproduce all comparisons.
	 * Sets lambda's penalty, reward and numViolations fields.
	 * @param lambda
	 */
	public static int evaluateLambda(ReferencePoint lambda) {
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PreferenceCollector.getInstance().getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = -1, b = -1;
			for(int i = 0; i<lambda.getNumDimensions(); i++){
				a = Double.max(a, lambda.getDim(i) * better.getObjective(i));
				b = Double.max(b, lambda.getDim(i) * worse.getObjective(i));
			}
			double eps = b-a;
			if(eps < 0){
				numViolations++;
				double newPenalty = penalty*(1-eps);
				assert newPenalty >= penalty;
				penalty = newPenalty;
			} else if(eps > 0){
				double newReward = reward*(1+eps);
				assert newReward >= reward;
				reward = newReward;
			}
		}
		
		lambda.setReward(reward);
		lambda.setPenalty(penalty);
		lambda.setNumViolations(numViolations);
		return numViolations;
	}
	
	protected ArrayList <ReferencePoint> selectNewLambdas(ArrayList <ReferencePoint> lambdasPop) {
		for(ReferencePoint rp : lambdasPop){
			double dim[] = rp.getDim();
			if(NSGAIIIRandom.getInstance().nextDouble() < 0.3){
				dim = Geometry.getRandomNeighbour(dim, 0.1); //Mutate lambda just a little bit randomly
			}
			rp.setDim(dim);
			evaluateLambda(rp);
		}
		Collections.sort(lambdasPop, new LambdaCVRanker());
		return new ArrayList<ReferencePoint>(lambdasPop.subList(0, numLambdas));
	}

	public ArrayList<ReferencePoint> getLambdas() {
		return this.lambdas;
	}

	public void lambdas(ArrayList <ReferencePoint> lambdas){ 
		this.lambdas = lambdas;
	}
	
	@Override
	public String toString(){
		String res="";
		for(ReferencePoint rp : lambdas){
			res += rp.toString() + "\n" + rp.getNumViolations() + "\n";
		}
		return res;
	}

	public void nextGeneration() {
		ArrayList <ReferencePoint> allLambdas = new ArrayList<>();
		allLambdas.addAll(lambdas);
		for(int i=0; i<numLambdas; i++){
			allLambdas.add(getRandomLambda());
		}

		ArrayList <ReferencePoint> newLambdas = selectNewLambdas(GLS.improve(allLambdas));
		System.out.println("Best/worse CV:" + newLambdas.stream().mapToInt(ReferencePoint::getNumViolations).min().getAsInt() + "/" + newLambdas.stream().mapToInt(ReferencePoint::getNumViolations).max().getAsInt());
		this.lambdas = newLambdas;
	}
	
	public void setLambdas(ArrayList<ReferencePoint> lambdas){
		this.lambdas = lambdas;
	}
	
	public boolean converged(){
		double min[] = new double[numObjectives];
		double max[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(ReferencePoint rp : lambdas){
			for(int i=0; i<numObjectives; i++){
				if(rp.getDim(i) < min[i]){ min[i] = rp.getDim(i); }
				if(rp.getDim(i) > max[i]){ max[i] = rp.getDim(i); }
			}
		}
		
		for(int i=0; i<numObjectives; i++){
			if( max[i] - min[i] > 0.001) return false;
		}
		return true;
	}
}
