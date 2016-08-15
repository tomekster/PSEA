package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.Hyperplane;
import core.hyperplane.ReferencePoint;
import history.NSGAIIIHistory;
import igd.IGD;
import igd.TargetFrontGenerator;
import operators.CrossoverOperator;
import operators.MutationOperator;
import operators.SelectionOperator;
import operators.impl.crossover.SBX;
import operators.impl.mutation.PolynomialMutation;
import operators.impl.selection.BinaryTournament;
import preferences.PreferenceCollector;
import preferences.TchebyshevFunction;
import utils.Geometry;
import utils.NSGAIIIRandom;
import utils.NonDominatedSort;
import utils.RACS;

public class NSGAIII implements Runnable {

	private final static Logger LOGGER = Logger.getLogger(NSGAIII.class.getName());

	private Problem problem;
	private Population population;
	private int populationSize, numGenerations, elicitationInterval;

	private SelectionOperator selectionOperator;
	private CrossoverOperator crossoverOperator;
	private MutationOperator mutationOperator;
	private NSGAIIIHistory history;
	private boolean interactive;
	private PreferenceCollector PC;
	private Hyperplane hyperplane;
	private int numPrepGen;

	public NSGAIII(Problem problem, int numGenerations, boolean interactive, int elicitationInterval, int numPrepGen) {
		// Defines problem that NSGAIII will solve
		this.problem = problem;

		// Hyperplane is one of basic constructs used in NSGA-III algorithm.
		// It is responsible of keeping solutions uniformly spread among
		// objective space
		this.hyperplane = new Hyperplane(problem.getNumObjectives());

		// Number of solutions in every generation. Depends on Hyperplane
		// because number of solutions
		// in population should be close to number of Reference Points on
		// Hyperplane
		this.populationSize = this.hyperplane.getReferencePoints().size();
		this.populationSize += this.populationSize % 2;
		this.population = createInitialPopulation();

		// Standard genetic operations used in evolutionary algorithms
		this.selectionOperator = new BinaryTournament();
		this.crossoverOperator = new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound());
		this.mutationOperator = new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(),
				problem.getUpperBound());

		// Parameters of algorithm execution
		this.numGenerations = numGenerations;
		this.interactive = interactive;
		this.elicitationInterval = elicitationInterval;
		this.numPrepGen = Integer.max(elicitationInterval, numPrepGen);

		// Structure for storing intermediate state of algorithm for further
		// analysis, display, etc.
		this.history = new NSGAIIIHistory(numGenerations);
		history.addGeneration(population.copy());
		history.addReferencePoints(this.hyperplane.getReferencePoints());
		history.setTargetPoints(TargetFrontGenerator.generate(this.hyperplane.getReferencePoints(), problem));
		history.setPreferenceCollector(PC);
		this.PC = new PreferenceCollector();
	}

	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations + " generations.");

		boolean recheckCoherence = false;
		boolean firstPhase;
		for (int generation = 0; generation < numGenerations; generation++) {
			firstPhase = generation < numPrepGen;
			
			if (interactive && firstPhase){
				if( (generation - numPrepGen) % elicitationInterval == 0) {
					System.out.println("GENERATION: " + generation);
					recheckCoherence = true;
					Population firstFront = NonDominatedSort.execute(population).get(0);
					if (firstFront.size() > 1){
						System.out.println(generation + " ELICITATTE" );
						elicitate(firstFront);
					}
				}
				
				if (recheckCoherence) {
					RACS.checkIfRefPointsAreCoherent(hyperplane.getReferencePoints(), this.PC);
					recheckCoherence = hyperplane.modifyReferencePoints(generation,numGenerations);
				}
			}

			nextGeneration(firstPhase);

			problem.evaluate(population);
			history.addGeneration(population.copy());
			history.addReferencePoints(hyperplane.getReferencePoints());
			history.setPreferenceCollector(PC);
		}
	}

	private Population createInitialPopulation() {
		Population population = new Population();
		for (int i = 0; i < populationSize; i++) {
			population.addSolution(problem.createSolution());
		}
		problem.evaluate(population);
		return population;
	}

	public Population nextGeneration(boolean firstPhase) {
		Population offspring = createOffspring(population);
		Population combinedPopulation = new Population();

		combinedPopulation.addSolutions(population);
		combinedPopulation.addSolutions(offspring);

		problem.evaluate(combinedPopulation);

			ArrayList<Population> fronts = NonDominatedSort.execute(combinedPopulation);

			Population allFronts = new Population();
			Population allButLastFront = new Population();
			Population lastFront = null;

			//TODO - refactor allFronts, allButLastFront and lastFront finding loop like in Hyperplane.getfronts... 
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

		
			if (allFronts.size() == populationSize) {
				population = allFronts.copy();
			} else {
				population = new Population();
				Population kPoints = new Population();
				int K = populationSize - allButLastFront.size();
				//TODO verify if 2Phases help
				if (true) {
					kPoints = NicheCountSelection.selectKPoints(allFronts, allButLastFront, lastFront, K,
						hyperplane);
				} else{
					System.out.println("popSize: " + populationSize);
					kPoints = hyperplane.selectKPoints(lastFront, K);
				}
				population.addSolutions(allButLastFront.copy());
				population.addSolutions(kPoints.copy());
			}
			
		return population;
	}

	private Population createOffspring(Population population) {
		Population offspring = new Population();
		Population matingPopulation = new Population();

		while (matingPopulation.size() < populationSize) {
			matingPopulation.addSolution(selectionOperator.execute(population));
		}

		for (int i = 0; i < populationSize; i += 2) {
			ArrayList<Solution> parents = new ArrayList<Solution>(2);
			parents.add(matingPopulation.getSolution(i));
			parents.add(matingPopulation.getSolution(i + 1));
			ArrayList<Solution> children = crossoverOperator.execute(parents);

			mutationOperator.execute(children.get(0));
			mutationOperator.execute(children.get(1));

			offspring.addSolution(children.get(0));
			offspring.addSolution(children.get(1));
		}
		return offspring;
	}

	public double evaluateFinalResult(Population result) {
		ArrayList<ReferencePoint> referencePoints = this.hyperplane.getReferencePoints();
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

		//if (TchebyshevFunction.decidentCentralPointCompare(s1, s2)) {
		if (TchebyshevFunction.decidentPrefereXCompare(s1, s2)) {
			PC.addComparison(s1, s2);
		} else {
			PC.addComparison(s2, s1);
		}
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
		double maxDist = 0, d;
		for (int i = 0; i < m; i++) {
			for (int j = i + 1; j < m; j++) {
				d = Geometry.euclideanDistance(Geometry.normalize(firstFront.getSolution(i).getObjectives()),
						Geometry.normalize(firstFront.getSolution(j).getObjectives()));
				if (d > maxDist) {
					maxDist = d;
					s1 = firstFront.getSolution(i);
					s2 = firstFront.getSolution(j);
				}
			}
		}

		if (TchebyshevFunction.decidentCentralPointCompare(s1, s2)) {
//		if (TchebyshevFunction.decidentPrefereXCompare(s1, s2)) {
			PC.addComparison(s1, s2);
		} else {
			PC.addComparison(s2, s1);
		}
	}

	public int getNumGenerations() {
		return numGenerations;
	}

	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}

	public SelectionOperator getSelectionOperator() {
		return selectionOperator;
	}

	public void setSelectionOperator(SelectionOperator selectionOperator) {
		this.selectionOperator = selectionOperator;
	}

	public CrossoverOperator getCrossoverOperator() {
		return crossoverOperator;
	}

	public void setCrossoverOperator(CrossoverOperator crossoverOperator) {
		this.crossoverOperator = crossoverOperator;
	}

	public MutationOperator getMutationOperator() {
		return mutationOperator;
	}

	public void setMutationOperator(MutationOperator mutationOperator) {
		this.mutationOperator = mutationOperator;
	}

	public Problem getProblem() {
		return problem;
	}

	public Population getPopulation() {
		return population;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public NSGAIIIHistory getHistory() {
		return history;
	}

	public void setHistory(NSGAIIIHistory history) {
		this.history = history;
	}
}
