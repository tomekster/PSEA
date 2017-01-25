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
		
		for(int i=1; i < breakPoints.size(); i++){
			dimensions.add(breakPoints.get(i) - breakPoints.get(i-1));
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
		allLambdas = improve(allLambdas);
		this.lambdas = selectNewLambdas(allLambdas);
		int tab[] = new int[100];
		for(ReferencePoint rp : lambdas){
			tab[rp.getNumViolations()]++;
		}
//		for(int i=0;i<50; i++){
//			System.out.print(i + ":" + tab[i] + " ");
//		}
//		System.out.println();
	}

	private ArrayList <ReferencePoint> improve(ArrayList<ReferencePoint> lambdasList) {
		ArrayList < Pair<ReferencePoint, ReferencePoint>> res = new ArrayList<>();
		double grad[] = new double[numObjectives];

		double mint1, mint2, maxt1, maxt2;
		mint1 = mint2 = Double.MAX_VALUE;
		maxt1 = maxt2 = -Double.MAX_VALUE;
		
		for(ReferencePoint lambda : lambdasList){
			double lambda2[] = lambda.getDim();
			for(Comparison cp : PC.getComparisons()){
				Solution a = cp.getBetter(), b = cp.getWorse();
				if(ChebyshevRanker.eval(a, null, lambda.getDim(), 0.0) > ChebyshevRanker.eval(b, null, lambda.getDim(), 0.0) ){
					for(int i=0; i < numObjectives; i++){
						grad[i] = MyMath.smoothMaxGrad(b.getObjectives(), lambda.getDim(), i) - MyMath.smoothMaxGrad(a.getObjectives(), lambda.getDim(), i) ;
						lambda2[i] += grad[i];
					}
				}
			}
			lambda2 = Geometry.normalize(lambda2);
			
		}
		
		return null;
	}
	
	public void setLambdas(ArrayList<ReferencePoint> lambdas){
		this.lambdas = lambdas;
	}
}
