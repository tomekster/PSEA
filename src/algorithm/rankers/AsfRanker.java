package algorithm.rankers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import utils.math.structures.Pair;

public class AsfRanker implements Serializable, Comparator<Solution>{
	
	private final static double DEFAULT_RHO = 0.0001;
	private static final long serialVersionUID = -6759292394593558688L;
	//TODO - add tests for Chebyshev function
	private double lambda[];
	private static double rho;
	private double refPoint[];
	private String name;

	public AsfRanker(double refPoint[], double lambda[], String name){
		if(refPoint.length != lambda.length){
			throw new RuntimeException();
		}
		this.refPoint = refPoint;
		this.lambda = lambda;
		AsfRanker.rho = DEFAULT_RHO;
		this.name = name;
	}
	
	public AsfRanker(double lambda[]){
		this(new double[lambda.length], lambda, "");
	}
	
	
	public Population sortPopulation(Population pop){
		Collections.sort(pop.getSolutions(), this);
		return pop;
	}
	
	public static int compareSolutions(Solution s1, Solution s2, double refPoint[], double lambda[]){
		double val1 = eval(s1.getObjectives(), refPoint, lambda);
		double val2 = eval(s2.getObjectives(), refPoint, lambda);
		if ( val1 < val2 )
			return -1;
		else if ( val1 > val2 )
			return 1;
		else
			return 0;
	}
	
	public double eval(Solution s){
		return eval(s.getObjectives());
	}
	public double eval(double obj[]){
		return eval(obj, this.refPoint, this.lambda);
	}
	
	public static double eval(Solution s, double refPoint[], double lambda[]){
		return eval(s.getObjectives(), refPoint, lambda);
	}
	
	public static double eval(double obj[], double refPoint[], double lambda[]){
		return classicEval(obj, refPoint, lambda);
//		return sternalEval(s, refPoint, lambda);
//		return slowinskiEval(s, refPoint, lambda);
//		return lpEval(s, refPoint, lambda, 4);
	}
	
	public static double classicEval(double obj[], double refPoint[], double lambda[]){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = -Double.MAX_VALUE;
		double sum = 0;
		for(int i=0; i<obj.length; i++){
			double mult = lambda[i] * (obj[i] - refPoint[i]);
			res = Double.max(mult, res);
			sum += mult;
		}
		res += sum * rho;
		return res;
	}
	
	public static double sternalEval(Solution s, double refPoint[], double lambda[]){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = -Double.MAX_VALUE;
		double sum = 0;
		for(int i=0; i<s.getNumObjectives(); i++){
			double mult = lambda[i] * (s.getObjective(i) - refPoint[i]);
			res = Double.max(mult, res);
			sum += mult;
		}
		res -= sum * rho;
		return res;
	}
	
	public static double slowinskiEval(Solution s, double refPoint[], double lambda[]){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = -Double.MAX_VALUE;
		double sum = 0;
		double mult[] = new double[s.getNumObjectives()];
		for(int i=0; i<s.getNumObjectives(); i++){
			mult[i] = lambda[i] * (s.getObjective(i) - refPoint[i]);
			res = Double.max(mult[i], res);
			sum += mult[i];
		}
		res -= ( 1+sum) / (1 + s.getNumObjectives() * res);

		res += sum * rho;
		return res;
	}
	
	public static double lpEval(Solution s, double refPoint[], double lambda[], double p){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = 0;
		double sum = 0;
		double mult[] = new double[s.getNumObjectives()];
		for(int i=0; i<s.getNumObjectives(); i++){
			mult[i] = lambda[i] * (s.getObjective(i) - refPoint[i]);
			res += Math.pow(mult[i], p);
			sum += mult[i];
		}
		res = Math.pow(res, 1/p);
		res += sum * rho;
		return res;
	}
	
	public Pair<Solution, Double> getBestSolutionVal(Population pop){
		Pair<Solution, Double> res = null;
		double minChebVal = Double.MAX_VALUE;
		for(Solution s : pop.getSolutions()){
			double val = this.eval(s);
			if(val < minChebVal){
				minChebVal = val;
				res = new Pair<>(s, val); 
			}
		}
		assert res != null;
		return res;
	}

	public double[] getLambda() {
		return lambda;
	}

	public static double getRho() {
		return AsfRanker.rho;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compare(Solution s1, Solution s2) {
		if (eval(s1) < eval(s2))
			return -1;
		else if (eval(s1) > eval(s2))
			return 1;
		else
			return 0;
	}
}
