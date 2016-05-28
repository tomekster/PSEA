package preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import core.Solution;

public class TchebyshevFunction {
	private ArrayList <Double> lambda;
	private int rho;
	Solution refPoint;
	
	//TODO - test Tchebyshev function  
	public double eval(Solution s){
		double res = -Double.MAX_VALUE;
		for(int i=0; i<s.getNumObjectives(); i++){
			res = Double.max(lambda.get(i) * (s.getObjective(i) - refPoint.getObjective(i)), res);
		}
		double sum = 0;
		for(int i=0; i<s.getNumObjectives(); i++){
			sum += lambda.get(i) * (s.getObjective(i) - refPoint.getObjective(i));
		}
		sum *= rho;
		res += sum;
		
		return res;
	}
	
	public double lineval(Solution s){
		double sum = 0;
		for(int i=0; i<s.getNumObjectives(); i++){
			sum += s.getObjective(i) - refPoint.getObjective(i);
		}
		sum *= rho;
		
		return sum;
	}
	public static double decidentEvaluate(Solution s){
		double max = -Double.MAX_VALUE;
		for(double d : s.getObjectives()){
			max = Double.max(max, d);
		}
		return max * 0.5;
	}
	
}
