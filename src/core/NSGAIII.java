package core;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.ReferencePoint;
import exceptions.DegeneratedMatrixException;
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
	private NicheCountSelection nicheCountSelection;
	private NSGAIIIHistory history;
	private boolean interactive;
	private PreferenceCollector PC;
	private boolean recheckCoherence;

	public NSGAIII(Problem problem, int numGenerations, boolean interactive, int elicitationInterval) {
		this.problem = problem;
		this.numGenerations = numGenerations;
		this.interactive = interactive;
		this.elicitationInterval = elicitationInterval;
		this.nicheCountSelection = new NicheCountSelection(problem.getNumObjectives());
		this.populationSize = nicheCountSelection.getPopulationSize();
		this.population = createInitialPopulation();
		this.selectionOperator = new BinaryTournament();
		this.crossoverOperator = new SBX(1.0, 30.0, problem.getLowerBound(), problem.getUpperBound());
		this.mutationOperator = new PolynomialMutation(1.0 / problem.getNumVariables(), 20.0, problem.getLowerBound(),
				problem.getUpperBound());
		this.history = new NSGAIIIHistory(numGenerations);
		problem.evaluate(population);
		history.addGeneration(population.copy());
		history.addReferencePoints(nicheCountSelection.getHyperplane().getReferencePoints());
		history.setTargetPoints(
				TargetFrontGenerator.generate(nicheCountSelection.getHyperplane().getReferencePoints(), problem));
		history.setPreferenceCollector(PC);
		this.PC = new PreferenceCollector();
	}

	public double judgeResult(Population result) {
		ArrayList<ReferencePoint> referencePoints = nicheCountSelection.getHyperplane().getReferencePoints();
		double igd = IGD.execute(TargetFrontGenerator.generate(referencePoints, problem), result);
		return igd;
	}

	public void run() {
		LOGGER.setLevel(Level.INFO);
		LOGGER.info("Running NSGAIII for " + problem.getName() + ", for " + problem.getNumObjectives()
				+ " objectives, and " + numGenerations + " generations.");
		
		this.recheckCoherence = false;
		for (int i = 0; i < numGenerations; i++) {
			if (interactive && i % elicitationInterval == elicitationInterval - 1) {
				NSGAIIIRandom rand = NSGAIIIRandom.getInstance();
				Population firstFront = NonDominatedSort.execute(population).get(0);
				if (firstFront.size() > 1) {
					int id1, id2;
					id1 = rand.nextInt(firstFront.size());
					do {
						id2 = rand.nextInt(firstFront.size());
					} while (id2 == id1);
					Solution s1 = firstFront.getSolution(id1);
					Solution s2 = firstFront.getSolution(id2);
					System.out.println("Preference elicitation:");
					System.out.println("s1:");
					System.out.println(s1.toString());
					System.out.println("s2:");
					System.out.println(s2.toString());
					elicitate(s1, s2);
					this.recheckCoherence = true;
				}
				System.out.println("GENERATION: " + (i + 1));
			}
			if(this.recheckCoherence){
				//nicheCountSelection.getHyperplane().cloneReferencePoints();
				RACS.markCoherent(nicheCountSelection.getHyperplane().getReferencePoints(), this.PC); //Sets ReferencePoints 'coherent' field
				this.recheckCoherence = false;
			}
			if(nicheCountSelection.getHyperplane().modifyReferencePoints((double)(i)/numGenerations)){
				recheckCoherence = true;
			}
			
			try {
				nextGeneration();
			} catch (DegeneratedMatrixException e) {
				LOGGER.warning("Degenerated matrix at " + (i + 1) + " generation");
				this.numGenerations = i;
				e.printStackTrace();
			}
			problem.evaluate(population);
			history.addGeneration(population.copy());
			history.addReferencePoints(nicheCountSelection.getHyperplane().getReferencePoints());
			history.setPreferenceCollector(PC);
		}

	}

	private void elicitate(Solution s1, Solution s2) {
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

		if (TchebyshevFunction.decidentCenterCompare(s1,s2)) {
		//if (TchebyshevFunction.decidentMajorXCompare(s1,s2)) {
			PC.addComparison(s1, s2);
		} else {
			PC.addComparison(s2, s1);
		}
	}

	public Population nextGeneration() throws DegeneratedMatrixException {
		Population offspring = createOffspring(population);
		Population combinedPopulation = new Population();
		
		combinedPopulation.addSolutions(population);
		combinedPopulation.addSolutions(offspring);
		
		problem.evaluate(combinedPopulation);
		
		//ArrayList<Population> fronts = NonDominatedSort.execute(combinedPopulation);
		ArrayList<Population> fronts = RACS.racsDomSort(combinedPopulation, nicheCountSelection.getHyperplane().getReferencePoints(), PC);
		
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

		allFronts.addSolutions(allButLastFront);
		allFronts.addSolutions(lastFront);

		if (allFronts.size() == populationSize) {
			population = allFronts.copy();
		} else {
			population = new Population();
			int K = populationSize - allButLastFront.size();
			Population kPoints = nicheCountSelection.selectKPoints(allFronts, allButLastFront, lastFront, K);
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

	private Population createInitialPopulation() {
		Population population = new Population();
		for (int i = 0; i < populationSize; i++) {
			population.addSolution(problem.createSolution());
		}
		return population;
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
