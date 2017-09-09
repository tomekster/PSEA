package algorithm.psea.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import algorithm.psea.GradientLambdaSearch;
import algorithm.rankers.ConstraintViolationRanker;
import artificialDM.AsfDM;
import problems.Problem;
import utils.math.Geometry;
import utils.math.MyRandom;

public class ASFBundle {
	private final static Logger LOGGER = Logger.getLogger(ASFBundle.class.getName());

	private static final double CONVERGENCE_THRESHOLD = 0.001;
	
	private static ASFBundle instance = null;

	private int numObjectives;
	private int bundleSize;
	private ArrayList <AsfDM> asfDMs;
	private GradientLambdaSearch GLS;
	
	private double[] referencePoint;
	
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
		double idealPoint[] = problem.findIdealPoint();
		
		asfDMs = new ArrayList<>();
		for (int i=0; i<bundleSize; i++) {
			asfDMs.add(new AsfDM(idealPoint, Geometry.getRandomVectorSummingTo1(numObjectives)));
		}
		GLS = new GradientLambdaSearch(numObjectives);
	}

	public ArrayList<AsfDM> getAsfDMs() {
		return this.asfDMs;
	}

	public void nextGeneration() {
		for(int i=0; i<bundleSize; i++) { 
			asfDMs.add(new AsfDM(referencePoint, Geometry.getRandomVectorSummingTo1(this.numObjectives))); //Add random lambdas to current bundle to increase the diversity 
		}
		ArrayList <AsfDM> newPreferenceModels = selectNewAsfDMs(GLS.improvePreferenceModels(this));
//		LOGGER.log(Level.INFO, "Best/worse CV:" + newLambdas.stream().mapToInt(AsfPreferenceModel::getNumViolations).min().getAsInt() + "/" + newLambdas.stream().mapToInt(AsfPreferenceModel::getNumViolations).max().getAsInt());
		this.asfDMs = newPreferenceModels;
	}
	
	protected ArrayList <AsfDM> selectNewAsfDMs(ArrayList <AsfDM> asfDMs) {
		for(AsfDM asfDM : asfDMs){
			double lambda[] = asfDM.getLambda();
			if(MyRandom.getInstance().nextDouble() < 0.3){
				//TODO - mutation of lambdas (randomNeighbour)
				lambda = Geometry.getRandomNeighbour(lambda, 0.1); //Mutate lambda just a little bit randomly
			}
			asfDM.setLambda(lambda);
			PreferenceCollector.getInstance().evaluateDM(asfDM);
		}
		Collections.sort(asfDMs, new ConstraintViolationRanker());
		return new ArrayList<AsfDM>(asfDMs.subList(0, bundleSize));
	}
	
	public boolean converged(){
		double min[] = new double[numObjectives];
		double max[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			min[i] = Double.MAX_VALUE;
			max[i] = -Double.MAX_VALUE;
		}
		
		for(AsfDM asfDM : asfDMs){
			for(int i=0; i<numObjectives; i++){
				if(asfDM.getLambda(i) < min[i]){ min[i] = asfDM.getLambda(i); }
				if(asfDM.getLambda(i) > max[i]){ max[i] = asfDM.getLambda(i); }
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
		for(AsfDM asfDM : asfDMs){
			res += asfDM.toString() + "\n" + asfDM.getNumViolations() + "\n";
		}
		return res;
	}
	
	public int getSize(){
		return bundleSize;
	}
	
	public double[] getReferencePoint(){
		return referencePoint;
	}

	//TODO - average or inversed average?
	public double[] getAverageLambdaPoint() {
		double res[] = new double[numObjectives];
		for(AsfDM lambda : asfDMs){
			for(int i=0; i<numObjectives; i++){
				res[i] += lambda.getLambda(i);
			}
		}
		for(int i=0; i<numObjectives; i++){
			res[i] /= asfDMs.size();
		}
		return res;
	}

	public void addAsfDM(double[] lambda) {
		this.asfDMs.add(new AsfDM(this.referencePoint, lambda));
	}
}
