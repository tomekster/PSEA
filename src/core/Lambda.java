package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import core.hyperplane.Hyperplane;
import core.points.ReferencePoint;
import core.points.Solution;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.LambdaCVRanker;
import utils.Geometry;
import utils.Pair;

public class Lambda extends EA {

	private final static Logger LOGGER = Logger.getLogger(Lambda.class.getName());

	private PreferenceCollector PC;
	private boolean elicitated;
	private int numObjectives;
	
	protected Lambda(int numObjectives, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator,
			MutationOperator mutationOperator) {
		super(selectionOperator, crossoverOperator, mutationOperator);
		this.PC = new PreferenceCollector();
		this.numObjectives = numObjectives;
		this.population = new Population();
		Hyperplane tmp = new Hyperplane(numObjectives);
		for (ReferencePoint rp : tmp.getReferencePoints()) {
			population.addSolution(rp);
		}
		if(population.size() % 2 != 0){
			double dim[] = new double[numObjectives];
			for(int i=0; i<numObjectives; i++){
				dim[i] = 1.0/numObjectives;
			}
			population.addSolution(new ReferencePoint(dim));
		}
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
	
	@Override
	protected Population selectNewPopulation(Population pop) {
		for (Solution s : pop.getSolutions()) {
			double norm[] = Geometry.normalize(s.getVariables());
			for (int i = 0; i < norm.length; i++) {
				s.setVariable(i, norm[i]);
			}
		}
		
		ArrayList <ReferencePoint> newLambdas = new ArrayList <>();

		// If new elicitation just happened - include initial uniform lambdas as potential candidates
		if(elicitated) {
			elicitated = false;
			Hyperplane tmp = new Hyperplane(numObjectives);
			for (ReferencePoint rp : tmp.getReferencePoints()) {
				evaluateLambda(rp);
				newLambdas.add(rp);
			}
		}

		for(Solution sol : pop.getSolutions()){
			ReferencePoint lambda = new ReferencePoint(sol.getVariables());
			evaluateLambda(lambda);
			newLambdas.add(lambda);
		}
		
//		int min = 1000000, max = -1000000;
//		for(ReferencePoint rp : newLambdas){
//			min = Integer.min(min, rp.getNumViolations());
//			max = Integer.max(max, rp.getNumViolations());
//		}
//		System.out.println("MIN: " + min);
//		System.out.println("MAX: " + max);
		
		Collections.sort(newLambdas, new LambdaCVRanker());

		Population result = new Population();
		for (int i = 0; i < population.size(); i++) {
			result.addSolution(newLambdas.get(i));
		}
		return result;
	}

	public Population selectKSolutionsByChebyshevBordaRanking(Population pop, int k) {
		HashMap<Solution, Integer> solutionBordaPointsMap = new HashMap<Solution, Integer>();
		for (Solution lambdaSolution : population.getSolutions()) {
			ReferencePoint lambda = (ReferencePoint) lambdaSolution;
			ArrayList<Solution> ranking = buildSolutionsRanking(lambda, pop);
			assert ranking.size() == pop.size();
			for (int i = 0; i < ranking.size(); i++) {
				Solution s = ranking.get(i);
				if (!solutionBordaPointsMap.containsKey(s)) {
					solutionBordaPointsMap.put(s, 0);
				}
				solutionBordaPointsMap.put(s, solutionBordaPointsMap.get(s) + (ranking.size() - i)/(lambda.getNumViolations() + 1));
			}
		}

		ArrayList<Pair<Solution, Integer>> pairs = new ArrayList<Pair<Solution, Integer>>();

		for (Solution s : solutionBordaPointsMap.keySet()) {
			pairs.add(new Pair<Solution, Integer>(s, solutionBordaPointsMap.get(s)));
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
		ArrayList <ReferencePoint> res = new ArrayList<>();
		for(Solution s : population.getSolutions()){
			res.add((ReferencePoint) s);
		}
		return res;
	}

	public PreferenceCollector getPreferenceCollector() {
		return this.PC;
	}

	public void setElicitated(boolean elicitated) {
		this.elicitated = elicitated;
	}
	
	public void setPopulation(Population pop){ 
		this.population = pop;
	}
}
