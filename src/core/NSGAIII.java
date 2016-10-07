package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.ChebyshevDirections;
import core.hyperplane.ReferencePoint;
import core.hyperplane.SolutionDirections;
import igd.IGD;
import igd.TargetFrontGenerator;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.Comparison;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import solutionRankers.NonDominationRanker;
import utils.Geometry;
import utils.NSGAIIIRandom;
import utils.Pair;

public class NSGAIII extends EA {

	private final static Logger LOGGER = Logger.getLogger(NSGAIII.class.getName());

	private int elicitationInterval;

	private boolean interactive;
	private PreferenceCollector PC;
	private SolutionDirections solutionDirections;
	private ChebyshevDirections chebyshevDirections;
	private ChebyshevRanker chebyshevRanker;

	public NSGAIII(Problem problem, int numGenerations, boolean interactive, int elicitationInterval) {
		super(problem, numGenerations, 0);
		// Hyperplane is one of basic constructs used in NSGA-III algorithm. It is responsible 
		// for keeping solutions uniformly spread among objective space. 
		// In modified NSGA-III it is used to store information about directions which are interesting 
		// from DM's point of view, based on preference information elicitated during algorithm run 
		this.solutionDirections = new SolutionDirections(problem.getNumObjectives());
		
		// Chebyshev directions is a new idea in modified NSGA-III. It is supposed to store information 
		// about Chebyshev function directions which are coherent with user's
		// preferences
		this.chebyshevDirections = new ChebyshevDirections(problem.getNumObjectives());

		// Number of solutions in every generation. Depends on Hyperplane because number 
		// of solutions in population should be close to number of Reference Points on Hyperplane
		this.populationSize = this.solutionDirections.getReferencePoints().size();
		this.populationSize += this.populationSize % 2;
		this.population = createInitialPopulation();

		// Parameters of algorithm execution
		this.interactive = interactive;
		this.elicitationInterval = elicitationInterval;
		this.chebyshevRanker = ChebyshevRankerBuilder.getCentralChebyshevRanker(problem.getNumObjectives());
		
		// Structure for storing intermediate state of algorithm for further
		// analysis, display, etc.
		history.setNumSolutionDirections(solutionDirections.getReferencePoints().size());
		history.addGeneration(population.copy());
		history.addSolutionDirections(this.solutionDirections.getReferencePoints());
		history.addChebyshevDirections(this.chebyshevDirections.getReferencePoints());
		history.setTargetPoints(TargetFrontGenerator.generate(this.solutionDirections.getReferencePoints(), problem));
		this.PC = new PreferenceCollector();
		history.setPreferenceCollector(PC);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations + " generations.");

		for (int generation = 0; generation < numGenerations; generation++) {
			nextGeneration();
			if(interactive){
				if(generation % elicitationInterval == 0) {
					System.out.println("GENERATION: " + generation);
					Population firstFront = NonDominationRanker.sortPopulation(population).get(0);
					if (firstFront.size() > 1){
						elicitate(firstFront);
					}
				}
				
				evolveChebyshevDirections(generation % elicitationInterval == 0);
				Population bestChebyshevSolutions = chebyshevDirections.selectKSolutionsByChebyshevBordaRanking(population, populationSize/2);
				NicheCountSelection.associate(population, solutionDirections);
				solutionDirections.modifySolutionDirections(generation, numGenerations, populationSize, bestChebyshevSolutions);
			}
			
			problem.evaluate(population);
			history.addGeneration(population.copy());
			history.addSolutionDirections(solutionDirections.getReferencePoints());
			history.addChebyshevDirections((ArrayList <ReferencePoint>)chebyshevDirections.getReferencePoints().clone());
			history.addBestChebVal(evaluateGeneration(population));
		}
	}

	private void evolveChebyshevDirections(boolean newElicitation) {			
		Population chebDirs = new Population();
		Population offspring = new Population();
		double arr[] = new double[1];
		for(ReferencePoint rp : chebyshevDirections.getReferencePoints()){
			Solution s = new Solution(rp.getDim(), arr);
			chebDirs.addSolution(s);
		}
		//If new elicitation just happened - use initial uniform cheb points distribution as offspring 
		if(newElicitation){
			ChebyshevDirections cd = new ChebyshevDirections(problem.getNumObjectives());
			for(ReferencePoint rp : cd.getReferencePoints()){
				chebDirs.addSolution(new Solution(rp.getDim(),arr));
			}
		}
		
		offspring = createChebOffspring(chebDirs);
		
		Population combinedPopulation = new Population();
		combinedPopulation.addSolutions(chebDirs);
		combinedPopulation.addSolutions(offspring);
		ArrayList <ReferencePoint> evaluatedCombinedPopulation = new ArrayList<>();
		for(Solution s : combinedPopulation.getSolutions()){
			ReferencePoint chebDir = new ReferencePoint(s.getVariables());
			evaluateChebDir(chebDir);
			evaluatedCombinedPopulation.add(chebDir);
		}
		Collections.sort(evaluatedCombinedPopulation, new Comparator<ReferencePoint>() {
			@Override
			public int compare(ReferencePoint o1, ReferencePoint o2) {
				if(o1.getNumViolations() == o2.getNumViolations()){ //Constraint violation: smaller = better
					return Double.compare(o1.getPenalty(), o2.getPenalty()); //Penalty: smaller = better
				}
				return Integer.compare(o1.getNumViolations(), o2.getNumViolations());
			}
		});
		
		ArrayList <ReferencePoint> result = new ArrayList<>();
		for(int i=0; i < chebyshevDirections.getReferencePoints().size(); i++){
			result.add(evaluatedCombinedPopulation.get(i));
		}
		
//		int tab[] = new int[3];
//		for(ReferencePoint rp : result){
//			if(rp.getNumViolations() < 3){
//				tab[rp.getNumViolations()]++;
//			}
//		}
		int min = 1000;
		for(ReferencePoint rp : result){
			if(rp.getNumViolations() < min) min = rp.getNumViolations();
		}
		System.out.println("Min violations: " + min);
		
		chebyshevDirections.setReferencePoints(result);
	}

	private Population createChebOffspring(Population chebDirs) {
		Population offspring = new Population();
		Population matingPopulation = new Population();

		double lowerBound[] = new double[problem.getNumObjectives()];
		double upperBound[] = new double[problem.getNumObjectives()];
		
		for(int i=0; i<problem.getNumObjectives(); i++){
			lowerBound[i] = Geometry.EPS;
			upperBound[i] = 1.0;
		}
		
		SelectionOperator chebSelectionOperator = new BinaryTournament();
		CrossoverOperator chebCrossoverOperator = new SBX(1.0, 30.0, lowerBound, upperBound);
		MutationOperator chebMutationOperator = new PolynomialMutation(1.0 / problem.getNumObjectives(), 20.0, lowerBound, upperBound);
		
		while (matingPopulation.size() < chebDirs.size() + (chebDirs.size() % 2)) {
			matingPopulation.addSolution(chebSelectionOperator.execute(chebDirs));
		}

		for (int i = 0; i < matingPopulation.size(); i += 2) {
			ArrayList<Solution> parents = new ArrayList<Solution>(2);
			parents.add(matingPopulation.getSolution(i));
			parents.add(matingPopulation.getSolution(i + 1));
			ArrayList<Solution> children = chebCrossoverOperator.execute(parents);

			chebMutationOperator.execute(children.get(0));
			chebMutationOperator.execute(children.get(1));

			offspring.addSolution(children.get(0));
			offspring.addSolution(children.get(1));
		}
		
		for(Solution s : offspring.getSolutions()){
			double norm[] = Geometry.normalize(s.getVariables());
			for(int i =0; i < norm.length; i++){
				s.setVariable(i, norm[i]);
			}
		}
		return offspring;
	}

	private void evaluateChebDir(ReferencePoint chebDir) {
		int numViolations = 0;
		double reward = 1, penalty = 1;
		for(Comparison c : PC.getComparisons()){
			Solution better = c.getBetter(), worse = c.getWorse();
			double a = -1, b = -1;
			for(int i = 0; i<chebDir.getNumDimensions(); i++){
				a = Double.max(a, chebDir.getDim(i) * better.getVariable(i));
				b = Double.max(b, chebDir.getDim(i) * worse.getVariable(i));
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
		
		chebDir.setReward(reward);
		chebDir.setPenalty(penalty);
		chebDir.setNumViolations(numViolations);
	}

	@Override
	public Population selectNewPopulation(Population pop) {
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(pop);

		Population allFronts = new Population();
		Population allButLastFront = new Population();
		Population lastFront = null;
		
		for (Population front : fronts) {
			if (allButLastFront.size() + front.size() >= populationSize) {
				lastFront = front;
				break;
			}
			for (Solution s : front.getSolutions()) {
				allButLastFront.addSolution(s.copy());
			}
		}
		
		// Note - allFronts, allButLastFront, lastFront should store
		// reference to the same Solution objects - not separate copies
		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);
			
		assert allFronts.size() >= populationSize;
		assert allButLastFront.size() <= populationSize;
		assert allButLastFront.size() + lastFront.size() >= populationSize;
		
		Population res;
		if (allFronts.size() == populationSize) {
			res = allFronts.copy();
		} else {
			res = new Population();
			Population kPoints = new Population();
			int K = populationSize - allButLastFront.size();
			kPoints = NicheCountSelection.selectKPoints(allFronts, allButLastFront, lastFront, K, solutionDirections);
			res.addSolutions(allButLastFront.copy());
			res.addSolutions(kPoints.copy());
		}
			
		return res;
	}

	public Pair<Solution, Double> evaluateGeneration(Population gen) {
		return chebyshevRanker.getMinChebVal(gen);
	}
	
	public double evaluateFinalResult(Population result){
		ArrayList<ReferencePoint> referencePoints = this.solutionDirections.getReferencePoints();
		double igd = IGD.execute(TargetFrontGenerator.generate(referencePoints, problem), result);
		return igd;
	}

	private void elicitateDialog(Population pop) {
		 NSGAIIIRandom rand = NSGAIIIRandom.getInstance();
		 int id1, id2;
		 id1 = rand.nextInt(pop.size());
		 do { 
			 id2 = rand.nextInt(pop.size()); 
		 } while (id2== id1);
		 Solution s1 = pop.getSolution(id1);
		 Solution s2 = pop.getSolution(id2);
		 
		// Object[] options = { "A: " + s1.objs(), "B: " + s2.objs() };
		// int n = JOptionPane.showOptionDialog(null, "Which Solution do you
		// prefer?", "Compare solutions",
		// JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
		// options, null);
		//
		// if (n == 0) {
		// PC.addComparison(s1, s2);
		// } else{
		// PC.addComparison(s2, s1);
		// }

		addComparison(s1, s2);
	}

	private void elicitate(Population firstFront) {
		int m = firstFront.getSolution(0).getNumObjectives();
		int extremeSolutionsIdx[] = new int[m];
		double minDimVal[] = new double[m];
		Arrays.fill(extremeSolutionsIdx, -1);
		Arrays.fill(minDimVal, 1000000);

		for (int i = 0; i < firstFront.size(); i++) {
			Solution s = firstFront.getSolutions().get(i);
			for (int j = 0; j < m; j++) {
				if (s.getObjectives()[j] < minDimVal[j]) {
					minDimVal[j] = s.getObjectives()[j];
					extremeSolutionsIdx[j] = i;
				}
			}
		}

		Solution s1 = null, s2 = null;
		int i, j;
		i = NSGAIIIRandom.getInstance().nextInt(firstFront.size());
		do{
			j = NSGAIIIRandom.getInstance().nextInt(firstFront.size());
		} while(i==j);
		
		s1 = firstFront.getSolution(i);
		s2 = firstFront.getSolution(j);
		
//Find maximal distance between solutions
//		double maxDist = 0, d;
//		for (int i = 0; i < m; i++) {
//			for (int j = i + 1; j < m; j++) {
//				d = Geometry.euclideanDistance(Geometry.normalize(firstFront.getSolution(i).getObjectives()),
//						Geometry.normalize(firstFront.getSolution(j).getObjectives()));
//				if (d > maxDist) {
//					maxDist = d;
//					s1 = firstFront.getSolution(i);
//					s2 = firstFront.getSolution(j);
//				}
//			}
//		}

		addComparison(s1, s2);
	}
	
	private void addComparison(Solution s1, Solution s2) {
		int comparisonResult = this.chebyshevRanker.compareSolutions(s1, s2); 
		if (comparisonResult == -1) {
			PC.addComparison(s1, s2);
		} else if (comparisonResult == 1){
			PC.addComparison(s2, s1);
		} else{
			System.out.println("Incomparable solutions - equal chebyshev function value");
		}
	}
}
