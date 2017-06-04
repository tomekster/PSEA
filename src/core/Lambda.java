package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.points.ReferencePoint;
import core.points.Solution;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.LambdaCVRanker;
import utils.Geometry;
import utils.GradientLambdaSearch;
import utils.NSGAIIIRandom;

public class Lambda {
	private final static Logger LOGGER = Logger.getLogger(Lambda.class.getName());
	
	private static Lambda instance = null;
	
	private int numObjectives;
	private int numLambdasDirections;
	private ArrayList <ReferencePoint> lambdaDirections;
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
	
	public void init(int numObjectives) {
		this.numObjectives = numObjectives;
		switch (numObjectives){
			case(3):
				this.numLambdasDirections = 50;
			break;
			case(5):
				this.numLambdasDirections = 60;
			break;
			case(8):
				this.numLambdasDirections = 70;
			break;
		}
		lambdaDirections = new ArrayList<>();
		for (int i=0; i<numLambdasDirections; i++) {
			lambdaDirections.add(new ReferencePoint(Geometry.getRandomVectorSummingTo1(numObjectives)));
		}
		GLS = new GradientLambdaSearch(numObjectives);
	}

	/**
	 * Checks if chebyshev's function with given lambdaDirection can reproduce all comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public static int evaluateDirection(ReferencePoint rp) {
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PreferenceCollector.getInstance().getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = -1, b = -1;
			for(int i = 0; i<rp.getNumDimensions(); i++){
				a = Double.max(a, rp.getDim(i) * better.getObjective(i));
				b = Double.max(b, rp.getDim(i) * worse.getObjective(i));
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
		
		rp.setReward(reward);
		rp.setPenalty(penalty);
		rp.setNumViolations(numViolations);
		return numViolations;
	}
	
	protected ArrayList <ReferencePoint> selectNewDirections(ArrayList <ReferencePoint> directionsPop) {
		for(ReferencePoint rp : directionsPop){
			double direction[] = rp.getDim();
			if(NSGAIIIRandom.getInstance().nextDouble() < 0.3){
				//TODO
				direction = Geometry.getRandomNeighbour(direction, 0.1); //Mutate lambda just a little bit randomly
			}
			rp.setDim(direction);
			evaluateDirection(rp);
		}
		Collections.sort(directionsPop, new LambdaCVRanker());
		return new ArrayList<ReferencePoint>(directionsPop.subList(0, numLambdasDirections));
	}

	public ArrayList<ReferencePoint> getLambdas() {
		return this.lambdaDirections;
	}

	public void nextGeneration() {
		ArrayList <ReferencePoint> allLambdaDirections = new ArrayList<>();
		allLambdaDirections.addAll(lambdaDirections);
		for(int i=0; i<numLambdasDirections; i++) { 
			allLambdaDirections.add(new ReferencePoint(Geometry.invert(Geometry.getRandomVectorSummingTo1(this.numObjectives)))); 
		}
		ArrayList <ReferencePoint> newLambdas = selectNewDirections(GLS.improve(allLambdaDirections));
		LOGGER.log(Level.INFO, "Best/worse CV:" + newLambdas.stream().mapToInt(ReferencePoint::getNumViolations).min().getAsInt() + "/" + newLambdas.stream().mapToInt(ReferencePoint::getNumViolations).max().getAsInt());
		this.lambdaDirections = newLambdas;
	}
	
	public boolean converged(){
		double min[] = new double[numObjectives];
		double max[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(ReferencePoint rp : lambdaDirections){
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
	
	@Override
	public String toString(){
		String res="";
		for(ReferencePoint rp : lambdaDirections){
			res += rp.toString() + "\n" + rp.getNumViolations() + "\n";
		}
		return res;
	}
	
	public int getNumLambdas(){
		return numLambdasDirections;
	}
}
