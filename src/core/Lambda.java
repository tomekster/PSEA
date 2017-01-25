package core;

import java.util.ArrayList;
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
import utils.Geometry.Line2D;
import utils.MyMath;
import utils.NSGAIIIRandom;
import utils.Pair;

public class Lambda {

	private final static Logger LOGGER = Logger.getLogger(Lambda.class.getName());

	private PreferenceCollector PC;
	private int numObjectives;
	private int numLambdas;
	private ArrayList <ReferencePoint> lambdas;
	
	protected Lambda(int numObjectives, int numLambdas) {
		this.PC = new PreferenceCollector();
		this.numObjectives = numObjectives;
		this.numLambdas = numLambdas;
		lambdas = new ArrayList<>();
		for (int i=0; i<numLambdas; i++) {
			lambdas.add(this.getRandomLambda());
		}
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
		ReferencePoint rp = new ReferencePoint(dims);
		return rp;
	}

	/**
	 * Checks if chebyshev's function with given lambda can reproduce all comparisons.
	 * Sets lambda's penalty, reward and numViolations fields.
	 * @param lambda
	 */
	public void evaluateLambda(ReferencePoint lambda) {
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PC.getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = -1, b = -1;
			for(int i = 0; i<lambda.getNumDimensions(); i++){
				a = Double.max(a, (1/lambda.getDim(i)) * better.getObjective(i));
				b = Double.max(b, (1/lambda.getDim(i)) * worse.getObjective(i));
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
	}
	
	protected ArrayList <ReferencePoint> selectNewLambdas(ArrayList <ReferencePoint> lambdasPop) {
		for(ReferencePoint rp : lambdasPop){
			evaluateLambda(rp);
		}
		Collections.sort(lambdasPop, new LambdaCVRanker());
		return new ArrayList<ReferencePoint>(lambdasPop.subList(0, numLambdas));
	}

	public Population selectKSolutionsByChebyshevBordaRanking(Population pop, int k) {
		HashMap<Solution, Integer> bordaPointsMap = getBordaPointsForSolutions(pop);
		
		ArrayList<Pair<Solution, Integer>> pairs = new ArrayList<Pair<Solution, Integer>>();

		for (Solution s : bordaPointsMap.keySet()) {
			pairs.add(new Pair<Solution, Integer>(s, bordaPointsMap.get(s)));
		}

		Collections.sort(pairs, new Comparator<Pair<Solution, Integer>>() {
			@Override
			public int compare(final Pair<Solution, Integer> o1, final Pair<Solution, Integer> o2) {
				return Integer.compare(o2.second, o1.second); // Sort DESC by Borda points
			}
		});

		Population res = new Population();
		for (int i = 0; i < k; i++) {
			res.addSolution(pairs.get(i).first.copy());
		}
		return res;
	}

	private HashMap<Solution, Integer> getBordaPointsForSolutions(Population pop) {
		HashMap<Solution, Integer> bordaPointsMap = new HashMap<>();
		for (ReferencePoint lambda : lambdas) {
			ArrayList<Solution> ranking = buildSolutionsRanking(lambda, pop);
			assert ranking.size() == pop.size();
			for (int i = 0; i < ranking.size(); i++) {
				Solution s = ranking.get(i);
				if (!bordaPointsMap.containsKey(s)) {
					bordaPointsMap.put(s, 0);
				}
				bordaPointsMap.put(s, bordaPointsMap.get(s) + (ranking.size() - i)/(lambda.getNumViolations() + 1));
			}
		}
		return bordaPointsMap;
	}

	public static ArrayList<Solution> buildSolutionsRanking(ReferencePoint lambda, Population pop) {
		ArrayList<Pair<Solution, Double>> solutionValuePairs = new ArrayList<Pair<Solution, Double>>();
		for (Solution s : pop.getSolutions()) {
			double chebyshevValue = ChebyshevRanker.eval(s, null, Geometry.invert(lambda.getDim()), 0);
			solutionValuePairs.add(new Pair<Solution, Double>(s, chebyshevValue));
		}
		Collections.sort(solutionValuePairs, new Comparator<Pair<Solution, Double>>() {
			@Override
			public int compare(final Pair<Solution, Double> o1, final Pair<Solution, Double> o2) {
				// Sort pairs by Chebyshev Function value ascending (Decreasing quality)
				return Double.compare(o1.second, o2.second);
			}
		});

		ArrayList<Solution> ranking = new ArrayList<Solution>();
		for (Pair<Solution, Double> p : solutionValuePairs) {
			ranking.add(p.first);
		}
		assert ranking.size() == pop.size();
		return ranking;
	}

	public ArrayList<ReferencePoint> getLambdas() {
		return this.lambdas;
	}

	public PreferenceCollector getPreferenceCollector() {
		return this.PC;
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
		this.lambdas = selectNewLambdas(improve(allLambdas));
	}

	private ReferencePoint improve(ReferencePoint lambda) {
		//TODO
		//double grad[] = getTotalPCGradient(lambda);
		
		double neigh[] = Geometry.getRandomNeighbour(lambda.getDim(), 1.0);
		double grad[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			grad[i] = neigh[i] - lambda.getDim(i);
		}
				
		Pair <double[], double[]> simplexSegment = Geometry.getSimplexSegment(lambda.getDim(), grad);
		double l1[] = simplexSegment.first, l2[] = simplexSegment.second;
		
		double m1 = 1, m2 = 1;
		for(int i=0; i<numObjectives; i++){
			assert l1[i] >= 0;
			assert l1[i] <= 1;
			assert l2[i] >= 0;
			assert l2[i] <= 1;
			if(l1[i] < m1) m1 = l1[i];
			if(l2[i] < m2) m2 = l2[i];
		}
		assert Math.abs(m1) < Geometry.EPS;
		assert Math.abs(m2) < Geometry.EPS;
		
		//Each pair is (t, [+,-] id), where t represents "time" on segment l1, l2 counted from l1 to l2
		// while absolute value of id represents comparison id which changes when lambda crosses this point. Positive id indicates 
		//change from "reproduced" to "not reproduced" comparison, while negative id indicates opposite.
		ArrayList < Pair<Double, Integer> > switches = getComparisonSwitchPoints(l1, l2);
		
		Collections.sort(switches, new Comparator<Pair<Double, Integer>>() {
			@Override
			public int compare(Pair<Double, Integer> o1, Pair<Double, Integer> o2) {
				return Double.compare(o1.first, o2.first);
			}
		});
		return new ReferencePoint(Geometry.linearCombination(l1, l2, findBestTime(switches)));
	}

	private double findBestTime(ArrayList < Pair<Double, Integer> > switches) {
		int CV = 0, bestCV = Integer.MAX_VALUE;
		double bestBeg=0, bestEnd=1;
		
		for(int i=0; i<switches.size(); i++){
			Pair<Double, Integer> p = switches.get(i);
			if(p.second < 0) CV++;
			else if(p.second > 0) CV--;
			if(p.first > 0 && p.first<1 && p.first > switches.get(i-1).first && CV < bestCV){
				bestCV = CV;
				bestBeg = switches.get(i-1).first;
				bestEnd = switches.get(i).first;
			}
		}
		return bestBeg + bestEnd/2;
	}

	private ArrayList<Pair<Double, Integer>> getComparisonSwitchPoints(double l1[], double l2[]) {
		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
		for(int cpId=0; cpId<PC.getComparisons().size(); cpId++){
			Comparison cp = PC.getComparisons().get(cpId);
			ArrayList <Line2D> lines = new ArrayList<>();
			for(int i=0; i<numObjectives; i++){
				lines.add(new Line2D(cp.getBetter().getObjective(i) * (l1[i] - l2[i]), cp.getBetter().getObjective(i) * l2[i], true ) );
				lines.add(new Line2D(cp.getWorse().getObjective(i) * (l1[i] - l2[i]), cp.getWorse().getObjective(i) * l2[i], false ) );
			}
			ArrayList <Line2D> upperEnvelope = Geometry.linesSetUpperEnvelope(lines);

			//Check comparisons on lambda1 (0 on time scale) to properly initialize switches array
			if(ChebyshevRanker.compareSolutions(cp.getBetter(), cp.getBetter(), null, l1, 0) < 0){
				res.add(new Pair<Double, Integer>(.0, cpId));
			}
			else if(ChebyshevRanker.compareSolutions(cp.getBetter(), cp.getBetter(), null, l1, 0) > 0){
				res.add(new Pair<Double, Integer>(.0, -cpId));
			}
			
			for(int i=1; i<upperEnvelope.size(); i++){
				Line2D line1 = upperEnvelope.get(i-1);
				Line2D line2 = upperEnvelope.get(i);
				if( !(line1.isBetter() ^ line2.isBetter())) continue;
				double crossX = line1.crossX(line2);
				if(crossX < 0 || crossX > 1) continue;
				if(line2.isBetter()){
					res.add(new Pair<Double, Integer>(crossX, cpId+1));
				}
				else{
					res.add(new Pair<Double, Integer>(crossX, -(cpId+1)));
				}
			}
		}
		return res;
	}

	protected double[] getTotalPCGradient(ReferencePoint lambda) {
		double grad[] = new double[numObjectives];
		for(Comparison cp : PC.getComparisons()){
			Solution a = cp.getBetter(), b = cp.getWorse();
			if(ChebyshevRanker.eval(a, null, lambda.getDim(), 0.0) >= ChebyshevRanker.eval(b, null, lambda.getDim(), 0.0) ){
				for(int i=0; i < numObjectives; i++){
					grad[i] = MyMath.smoothMaxGrad(b.getObjectives(), lambda.getDim(), i) - MyMath.smoothMaxGrad(a.getObjectives(), lambda.getDim(), i) ;
				}
			}
		}
		
		double lambda2[] = lambda.getDim().clone();
		for(int i=0; i< grad.length; i++){
			lambda2[i] += grad[i];
		}
		lambda2 = Geometry.normalize(lambda2);
		for(int i=0; i< grad.length; i++) grad[i] = lambda2[i] - lambda.getDim(i);
		return grad;
	}

	private ArrayList <ReferencePoint> improve(ArrayList<ReferencePoint> lambdasList) {
		ArrayList <ReferencePoint> res = new ArrayList<>();
		for(ReferencePoint lambda : lambdasList){
			res.add(improve(lambda));
		}
		return res;
	}
	
	public void setLambdas(ArrayList<ReferencePoint> lambdas){
		this.lambdas = lambdas;
	}
}
