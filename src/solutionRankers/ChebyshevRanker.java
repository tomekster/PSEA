package solutionRankers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import core.Population;
import core.Solution;
import utils.Pair;

public class ChebyshevRanker{
	
	//TODO - test Tchebyshev function  
	private double lambda[];
	private double rho;
	private double refPoint[];
		
	public ChebyshevRanker(double refPoint[], double lambda[], double rho){
		if(refPoint.length != lambda.length){
			throw new RuntimeException();
		}
		this.refPoint = refPoint;
		this.lambda = lambda;
		this.rho = rho;
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
	
	public static double eval(Solution s, double refPoint[], double lambda[], double rho){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = -Double.MAX_VALUE;
		for(int i=0; i<s.getNumObjectives(); i++){
			res = Double.max(lambda[i] * (s.getObjective(i) - refPoint[i]), res);
		}
		double sum = 0;
		for(int i=0; i<s.getNumObjectives(); i++){
			sum += lambda[i] * (s.getObjective(i) - refPoint[i]);
		}
		sum *= rho;
		res += sum;
		
		return res;
	}
	
	public Pair<Solution, Double> getMinChebVal(Population pop){
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
}
