package problems.knapsack;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.AsfDmProblem;
import problems.PermutationProblem;
import problems.knapsack.structures.Knapsack;
import utils.enums.OptimizationType;

public class KnapsackProblemInstance extends PermutationProblem implements AsfDmProblem {
	
	String linuxPath = "/media/tomasz/D3E6-0A4B/PSEA/knapsack";
	String windowsPath = "C:\\Users\\stern\\git\\PSEA\\benchmarks\\knapsack\\referencePF";
	String path;		
	
	private ArrayList<Knapsack> knapsacks;

	/**
	 * paretoFront field stores pareto-optimal solutions only after getOptimalAsfDmSolution method was called.
	 * Stores only objective values since only those are available. 
	 * Variable field is set to NULL for alls solutions. 
	 */
	private Population <Solution> paretoFront = null;
	
	public KnapsackProblemInstance(int numItems, int numKnapsacks, int numConstraints, String name) {
		super(numItems, numKnapsacks, numConstraints, name, OptimizationType.MAXIMIZATION);
		this.knapsacks = new ArrayList<>();
		path = this.linuxPath;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5243231475354789750L;

	@Override
	public void evaluate(VectorSolution <Integer> solution) {
		for (int i = 0; i < solution.getNumObjectives(); i++) {
			solution.setObjective(i, 0);
		}
		double totalWeights[] = new double[solution.getNumObjectives()]; // Store
																			// current
																			// load
																			// in
																			// each
																			// knapsack
		double totalProfits[] = new double[solution.getNumObjectives()];
		
		boolean solutionFitsInAllKnapsacks = true;
		for (Integer itemId : solution.getVariables()) {
			for (int i = 0; i < knapsacks.size(); i++) {
				Knapsack k = knapsacks.get(i);
				if (k.get(itemId).getWeight() + totalWeights[i] > k.getMaxWeight()) {
					solutionFitsInAllKnapsacks = false;
					break;
				}
			}
			if (!solutionFitsInAllKnapsacks) {
				break;
			}

			// If next element in permutation fits into all knapsacks, update
			// total weights by adding element weight and update objective
			// vector of profits.
			for(int i = 0; i < knapsacks.size(); i++) {
				Knapsack k = knapsacks.get(i);
				totalWeights[i] += k.get(itemId).getWeight();
				totalProfits[i] += k.get(itemId).getProfit();
			}
		}
		
		for(int i=0; i<knapsacks.size(); i++){
			solution.setObjective(i, totalProfits[i]);
		}
	}

	@Override
	public Population <Solution> getReferenceFront() {
		Population <Solution> refFront = new Population <>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(Paths
				.get(path, "knapsack." + numVariables + "." + numObjectives + ".pareto")
				.toFile()))) {
			double obj[] = null;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				String vals[] = line.trim().split(" ");
				obj = new double[vals.length];
				for (int i = 0; i < vals.length; i++) {
					obj[i] = Integer.parseInt(vals[i]);
				}
				refFront.addSolution(new Solution(obj.clone()));
			}
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

	//TODO - remove duplicated file reading code
	@Override
	public Solution getOptimalAsfDmSolution(AsfDm dm) {
		if(this.paretoFront == null){
			this.paretoFront = new Population <Solution>();
			
			//Read the data
			try (BufferedReader br = new BufferedReader(
					new FileReader(Paths.get(this.path, "knapsack." + getNumVariables() + "." + getNumObjectives() + ".pareto").toFile()))) {
				
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					String vals[] = line.trim().split(" ");
					double [] obj = new double[vals.length];
					for (int i = 0; i < vals.length; i++) {
						obj[i] = Integer.parseInt(vals[i]);
					}
					this.paretoFront.addSolution(new Solution(obj));
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Search for best solution for given ASF DM
		double bestVal = Double.POSITIVE_INFINITY;
		Solution bestSol = null;
		for(Solution s : paretoFront.getSolutions()){
			double eval = dm.eval(s);
			if ( eval < bestVal) {
				bestVal = eval;
				bestSol = s;
			}
		}
		return new Solution(bestSol);
	}
}
