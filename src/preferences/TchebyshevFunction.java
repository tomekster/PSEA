package preferences;

import java.util.ArrayList;

import core.Solution;

public class TchebyshevFunction {
	private ArrayList <Double> lambda;
	private int rho;
	Solution refPoint;
	
	//TODO - test Tchebyshev function  
	public double eval(Solution s){
		double res=-Double.MIN_VALUE;
		for(int i=0; i<s.getNumVariables(); i++){
			res = Double.max(lambda.get(i) * (s.getVariable(i) - refPoint.getVariable(i)), res);
		}
		double sum = 0;
		for(int i=0; i<s.getNumVariables(); i++){
			sum += lambda.get(i) * (s.getVariable(i) - refPoint.getVariable(i));
		}
		sum *= rho;
		res += sum;
		
		return res;
	}
	
	public double lineval(Solution s){
		double sum = 0;
		for(int i=0; i<s.getNumVariables(); i++){
			sum += s.getVariable(i) - refPoint.getVariable(i);
		}
		sum *= rho;
		
		return sum;
	}
	
}
