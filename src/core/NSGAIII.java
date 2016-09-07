package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.hyperplane.ChebyshevDirections;
import core.hyperplane.ReferencePoint;
import core.hyperplane.SolutionDirections;
import igd.IGD;
import igd.TargetFrontGenerator;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
import solutionRankers.ChebyshevRankerBuilder;
import solutionRankers.NonDominationRanker;
import utils.NSGAIIIRandom;
import utils.RACS;

public class NSGAIII extends EA {

	private final static Logger LOGGER = Logger.getLogger(NSGAIII.class.getName());

	private int elicitationInterval;

	private boolean interactive;
	private PreferenceCollector PC;
	private SolutionDirections solutionDirections;
	private SolutionDirections originalHyperplane;
	private ChebyshevDirections chebyshevDirections;
	private double spreadingPointsPercent;
	private ChebyshevRanker chebyshevRanker;

	public NSGAIII(Problem problem, int numGenerations, boolean interactive, int elicitationInterval, double spreadingPointsPercent) {
		super(problem, numGenerations, 0);
		// Hyperplane is one of basic constructs used in NSGA-III algorithm. It is responsible 
		// for keeping solutions uniformly spread among objective space. 
		// In modified NSGA-III it is used to store information about directions which are interesting 
		// from DM's point of view, based on preference information elicitated during algorithm run 
		this.solutionDirections = new SolutionDirections(problem.getNumObjectives());
		
		this.originalHyperplane= new SolutionDirections(problem.getNumObjectives());
		
		// Chebyshev directions is a new idea in modified NSGA-III. It is supposed to store information 
		// about Chebyshev function directions which are coherent with user's preferences
		this.chebyshevDirections = new ChebyshevDirections(problem.getNumObjectives());

		// Number of solutions in every generation. Depends on Hyperplane because number 
		// of solutions in population should be close to number of Reference Points on Hyperplane
		this.populationSize = this.solutionDirections.getReferencePoints().size();
		this.populationSize += this.populationSize % 2;
		this.population = createInitialPopulation();

		// Parameters of algorithm execution
		this.interactive = interactive;
		this.elicitationInterval = elicitationInterval;
		this.spreadingPointsPercent = spreadingPointsPercent;
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

		boolean recheckCoherence = false;
		for (int generation = 0; generation < numGenerations; generation++) {
			nextGeneration();

			if(interactive){
				if(generation % elicitationInterval == 0) {
					System.out.println("GENERATION: " + generation);
					recheckCoherence = true;
					Population firstFront = NonDominationRanker.sortPopulation(population).get(0);
					if (firstFront.size() > 1){
						System.out.println(generation + " ELICITATE" );
						elicitate(firstFront);
					}
				}
				if (recheckCoherence) {
					RACS.checkIfRefPointsAreCoherent(chebyshevDirections.getReferencePoints(), this.PC);
					recheckCoherence = chebyshevDirections.modifyChebyshevDirections(generation,numGenerations);
				}
				
				Population bestChebyshevSolutions = chebyshevDirections.selectKChebyshevPoints(population, populationSize/2);
				// TODO - this looks bad. Associatie should not be called here in this way. You should refactor this in future. 
				// Associate needs to be called before modifySolutionDirections, since it uses associations
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

	@Override
	public Population selectNewPopulation(Population pop) {
		ArrayList<Population> fronts = NonDominationRanker.sortPopulation(pop);

		Population spreadingPoints;
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
			int numSpreadingPoints = (int) (this.spreadingPointsPercent * K); 
			//TODO verify if 2Phases help
			spreadingPoints = NicheCountSelection.selectKPoints(allFronts, allButLastFront, lastFront, numSpreadingPoints, originalHyperplane);
			//TODO - take care, risky operation since equals in Solution is overloaded. Might delete solutions which are not exactly equal but only very close. Might affect niching.
			allFronts.removeSolutions(spreadingPoints);
			
			allButLastFront.removeSolutions(spreadingPoints);
			allButLastFront.removeSolutions(lastFront);
			kPoints = NicheCountSelection.selectKPoints(allFronts, allButLastFront, lastFront, K - numSpreadingPoints, solutionDirections);
			res.addSolutions(allButLastFront.copy());
			res.addSolutions(spreadingPoints.copy());
			res.addSolutions(kPoints.copy());
		}
			
		return res;
	}

	public double evaluateGeneration(Population gen) {
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
