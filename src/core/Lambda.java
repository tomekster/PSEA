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
	private int numLambdaPoints;
	private ArrayList <ReferencePoint> lambdaPoints;
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
				this.numLambdaPoints = 50;
			break;
			case(5):
				this.numLambdaPoints = 60;
			break;
			case(8):
				this.numLambdaPoints = 70;
			break;
		}
		lambdaPoints = new ArrayList<>();
		for (int i=0; i<numLambdaPoints; i++) {
			lambdaPoints.add(new ReferencePoint(Geometry.getRandomVectorSummingTo1(numObjectives)));
		}
		GLS = new GradientLambdaSearch(numObjectives);
	}

	/**
	 * Checks if chebyshev's function with given lambdaPoint can reproduce all comparisons.
	 * Sets ReferencePoint penalty, reward and numViolations fields.
	 * @param rp
	 */
	public static int evaluateLambdaPoint(ReferencePoint rp) {
		double direction[] = Geometry.point2dir(rp.getDim());
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PreferenceCollector.getInstance().getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = -1, b = -1;
			for(int i = 0; i<direction.length; i++){
				a = Double.max(a, direction[i] * better.getObjective(i));
				b = Double.max(b, direction[i] * worse.getObjective(i));
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

	public ArrayList<ReferencePoint> getLambdaPoints() {
		return this.lambdaPoints;
	}

	public void nextGeneration() {
		ArrayList <ReferencePoint> allLambdaPoints = new ArrayList<>();
		allLambdaPoints.addAll(lambdaPoints);
		for(int i=0; i<numLambdaPoints; i++) { 
			allLambdaPoints.add(new ReferencePoint(Geometry.getRandomVectorSummingTo1(this.numObjectives))); 
		}
		ArrayList <ReferencePoint> newLambdaPoints = selectNewLambdaPoints(GLS.improveLambdaPoints(allLambdaPoints));
		LOGGER.log(Level.INFO, "Best/worse CV:" + newLambdaPoints.stream().mapToInt(ReferencePoint::getNumViolations).min().getAsInt() + "/" + newLambdaPoints.stream().mapToInt(ReferencePoint::getNumViolations).max().getAsInt());
		this.lambdaPoints = newLambdaPoints;
	}
	
	protected ArrayList <ReferencePoint> selectNewLambdaPoints(ArrayList <ReferencePoint> lambdaPoints) {
		for(ReferencePoint rp : lambdaPoints){
			double lambdaPoint[] = rp.getDim();
			if(NSGAIIIRandom.getInstance().nextDouble() < 0.3){
				//TODO
				lambdaPoint = Geometry.getRandomNeighbour(lambdaPoint, 0.1); //Mutate lambda just a little bit randomly
			}
			rp.setDim(lambdaPoint);
			evaluateLambdaPoint(rp);
		}
		Collections.sort(lambdaPoints, new LambdaCVRanker());
		return new ArrayList<ReferencePoint>(lambdaPoints.subList(0, numLambdaPoints));
	}
	
	public boolean converged(){
		double min[] = new double[numObjectives];
		double max[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(ReferencePoint rp : lambdaPoints){
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
		for(ReferencePoint rp : lambdaPoints){
			res += rp.toString() + "\n" + rp.getNumViolations() + "\n";
		}
		return res;
	}
	
	public int getNumLambdas(){
		return numLambdaPoints;
	}

	public double[] getAverageLambdaPoint() {
		double res[] = new double[numObjectives];
		for(ReferencePoint rp : lambdaPoints){
			for(int i=0; i<numObjectives; i++){
				res[i] += rp.getDim(i);
			}
		}
		for(int i=0; i<numObjectives; i++){
			res[i] /= lambdaPoints.size();
		}
		return res;
	}
}
