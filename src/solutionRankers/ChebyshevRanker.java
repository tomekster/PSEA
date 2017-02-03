package solutionRankers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import core.Population;
import core.points.Solution;
import utils.Pair;

public class ChebyshevRanker{
	
	//TODO - test Tchebyshev function  
	private double lambda[];
	private double rho;
	private double refPoint[];
	private String name;

	public ChebyshevRanker(double refPoint[], double lambda[], double rho, String name){
		if(refPoint.length != lambda.length){
			throw new RuntimeException();
		}
		this.refPoint = refPoint;
		this.lambda = lambda;
		this.rho = rho;
		this.setName(name);
	}
	
	public ChebyshevRanker(double lambda[]){
		this(new double[lambda.length], lambda, 0 , "");
	}
	
	public double eval(Solution s){
		return eval(s, this.refPoint, this.lambda, this.rho);
	}
	
	public Population sortPopulation(Population pop){
		Collections.sort(pop.getSolutions(), new Comparator <Solution>() {
			@Override
			public int compare(Solution s1, Solution s2) {
				return compareSolutions(s1, s2);
			}
		});
		return pop;
	}
	
	public int compareSolutions(Solution s1, Solution s2){
		if (eval(s1) < eval(s2))
			return -1;
		else if (eval(s1) > eval(s2))
			return 1;
		else
			return 0;
	}
	
	public static int compareSolutions(Solution s1, Solution s2, double refPoint[], double lambda[], double rho){
		if (eval(s1, refPoint, lambda,rho) < eval(s2, refPoint, lambda, rho))
			return -1;
		else if (eval(s1, refPoint, lambda,rho) > eval(s2, refPoint, lambda, rho))
			return 1;
		else
			return 0;
	}
	
	public static double eval(Solution s, double refPoint[], double lambda[], double rho){
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
		res += sum * rho;
		return res;
	}
	
	public Pair<Solution, Double> getBestSolutionVal(Population pop){
		Pair<Solution, Double> res = null;
		double minChebVal = Double.MAX_VALUE;
		for(Solution s : pop.getSolutions()){
			double val = this.eval(s);
			if(val < minChebVal){
				res = new Pair<>(s, minChebVal = val); 
			}
		}
		assert res != null;
		return res;
	}

	public double[] getLambda() {
		return lambda;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
