package problems.zdt;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.ReferencePointDm;
import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.evolutionary.solutions.VectorSolution;
import problems.ContinousProblem;
import problems.KnowsOptimalAsfSolution;
import utils.enums.OptimizationType;
import utils.math.structures.Pair;
import utils.math.structures.Point;

/**
 * Class representing problem ZDT3
 */
public class ZDT3 extends ContinousProblem implements KnowsOptimalAsfSolution{

	private static final long serialVersionUID = 4832412006664171576L;

	/**
	 * Constructor. Creates default instance of problem ZDT3 (30 decision
	 * variables)
	 */
	public ZDT3() {
		this(30);
	}

	/**
	 * Constructor. Creates a instance of ZDT3 problem.
	 *
	 * @param numberOfVariables
	 *            Number of variables.
	 */
	public ZDT3(Integer numberOfVariables) {
		super(numberOfVariables, 2, 0, "ZDT3", OptimizationType.MINIMIZATION);
	}

	/** Evaluate() method */
	public void evaluate(VectorSolution<Double> solution) {
		double[] f = new double[numObjectives];

		f[0] = solution.getVariable(0);
		double g = this.evalG(solution);
		double h = this.evalH(f[0], g);
		f[1] = h * g;

		solution.setObjective(0, f[0]);
		solution.setObjective(1, f[1]);
	}

	/**
	 * Returns the value of the ZDT2 function G.
	 *
	 * @param solution
	 *            Solution
	 */
	private double evalG(VectorSolution<Double> solution) {
		double g = 0.0;
		for (int i = 1; i < solution.getNumVariables(); i++) {
			g += solution.getVariable(i);
		}
		double constant = 9.0 / (solution.getNumVariables() - 1);
		g = constant * g;
		g = g + 1.0;
		return g;
	}

	/**
	 * Returns the value of the ZDT3 function H.
	 *
	 * @param f
	 *            First argument of the function H.
	 * @param g
	 *            Second argument of the function H.
	 */
	public double evalH(double f, double g) {
		double h;
		h = 1.0 - Math.sqrt(f / g) - (f / g) * Math.sin(10.0 * Math.PI * f);
		return h;
	}

	@Override
	public void setBoundsOnVariables() {
		for (int i = 0; i < getNumVariables(); i++) {
			setLowerBound(i, 0.0);
			setUpperBound(i, 1.0);
		}
	}

	@Override
	public Population<Solution> getReferenceFront() {
		ArrayList <Pair <Double, Double> > segments = new ArrayList<>();
		segments.add(new Pair<Double, Double>(0., 0.0830015349));
		segments.add(new Pair<Double, Double>(0.1822287280, 0.2577623634));
		segments.add(new Pair<Double, Double>(0.4093136748, 0.4538821041));
		segments.add(new Pair<Double, Double>(0.6183967944, 0.6525117038));
		segments.add(new Pair<Double, Double>(0.8233317983, 0.8518328654));
		
		double obj[] = new double[2];
		Population <Solution> refFront = new Population<>();
		
		for(int i=0; i<segments.size(); i++){
			double b = segments.get(i).first;
			double e = segments.get(i).first;
			
			for(int j=0; j<=20; j++){
				double alfa = j/20.0;
				double x = b * (1-alfa) + e * alfa;
				double y = 1 - Math.sqrt(x) - x * Math.sin(10 * Math.PI * x);
				obj[0] = x;
				obj[1] = y;
				refFront.addSolution(new Solution(obj));
			}
		}
		return refFront;
	}

	@Override
	public Solution getOptimalSolution(ReferencePointDm dm) {
		Population <Solution> paretoFront = getReferenceFront();
		return dm.getBestSolutionVal(paretoFront);
	}

	@Override
	public Point getTrueIdealPoint() {
		Population <Solution> paretoFront = getReferenceFront();
		double maxObj[] = new double[numObjectives];
		for(int i=0; i<numObjectives; i++){
			for(Solution s : paretoFront.getSolutions()){
				maxObj[i] = Double.max(maxObj[i], s.getObjective(i));
			}
		}
		return new Point(maxObj);
	}
}