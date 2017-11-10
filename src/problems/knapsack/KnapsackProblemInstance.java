package problems.knapsack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.Solution;
import artificialDM.AsfDM;
import problems.AsfDmProblem;
import problems.PermutationProblem;

public class KnapsackProblemInstance extends PermutationProblem implements AsfDmProblem {
	private ArrayList<Knapsack> knapsacks;

	/**
	 * paretoFront field stores pareto-optimal solutions only after getOptimalAsfDmSolution method was called.
	 * Stores only objective values since only those are available. 
	 * Variable field is set to NULL for all solutions. 
	 */
	private Population paretoFront = null;
	
	public KnapsackProblemInstance(int numItems, int numKnapsacks, int numConstraints, String name) {
		super(numItems, numKnapsacks, numConstraints, name);
		this.knapsacks = new ArrayList<>();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5243231475354789750L;

	@Override
	public void evaluate(Solution solution) {
		for (int i = 0; i < solution.getNumObjectives(); i++) {
			solution.setObjective(i, 0);
		}
		double totalWeights[] = new double[solution.getNumObjectives()]; // Store
																			// current
																			// load
																			// in
																			// each
																			// knapsack

		boolean solutionFitsInAllKnapsacks = true;
		for (double v : solution.getVariables()) {
			for (int i = 0; i < knapsacks.size(); i++) {
				Knapsack k = knapsacks.get(i);
				if (k.get((int) v).getWeight() + totalWeights[i] > k.getMaxWeight()) {
					solutionFitsInAllKnapsacks = false;
					break;
				}
			}
			if (!solutionFitsInAllKnapsacks) {
				break;
			}

			// If next element in permutation fits into all knapsacks, update
			// total weights used by adding element weight and update objective
			// vector of profits.
			for (int i = 0; i < knapsacks.size(); i++) {
				Knapsack k = knapsacks.get(i);
				totalWeights[i] += k.get((int) v).getWeight();
				solution.setObjective(i, solution.getObjective(i) - k.get(i).getProfit());
			}
		}
	}

	@Override
	public Population getReferenceFront() {
		Population refFront = new Population();
		try (BufferedReader br = new BufferedReader(new FileReader(Paths
				.get("/home/tomasz/Desktop/knapsack/", "knapsack." + numVariables + "." + numObjectives + ".pareto")
				.toFile()))) {
			double obj[] = null;
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				String vals[] = line.trim().split(" ");
				obj = new double[vals.length];
				for (int i = 0; i < vals.length; i++) {
					obj[i] = -Integer.parseInt(vals[i]);
				}

			}
			refFront.addSolution(new Solution(obj.clone(), obj.clone()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return refFront;
	}
	
	public ArrayList<Knapsack> getKnapsacks() {
		return this.knapsacks;
	}

	@Override
	public Solution getOptimalAsfDmSolution(AsfDM dm) {
		if(this.paretoFront == null){
			this.paretoFront = new Population();
			
			//Read the data
			try (BufferedReader br = new BufferedReader(
					new FileReader(Paths.get("/home/tomasz/Dropbox/experiments/knapsack/reference_front",
							"pareto_front_" + getNumVariables() + "_" + getNumObjectives()).toFile()))) {
				
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					String vals[] = line.trim().split(" ");
					double [] obj = new double[vals.length];
					for (int i = 0; i < vals.length; i++) {
						obj[i] = Integer.parseInt(vals[i]);
					}
					this.paretoFront.addSolution(new Solution(null, obj));
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Search for best solution for given ASF DM
		double bestVal = Double.NEGATIVE_INFINITY;
		Solution bestSol = null;
		for(Solution s : paretoFront.getSolutions()){
			double eval = dm.eval(s.getObjectives()); 
			if ( eval < bestVal) {
				bestVal = eval;
				bestSol = s;
			}
		}
		return new Solution(bestSol);
	}
}
