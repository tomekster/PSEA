package preferences;

import java.util.Arrays;

import core.Solution;

public class TchebyshevFunction {
	
	//TODO - test Tchebyshev function  
	public static double eval(Solution s, double lambda[], double rho, double refPoint[]){
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
	
	public double lineval(Solution s, double rho, double refPoint[]){
		double sum = 0;
		for(int i=0; i<s.getNumObjectives(); i++){
			sum += s.getObjective(i) - refPoint[i];
		}
		sum *= rho;
		
		return sum;
	}
	
	/***
	 * Method simulates decident with most preferred solution corresponding to central 
	 * point of hyperplane
	 * 
	 * @param s1
	 * @param s2
	 * @return 
	 */
	public static boolean decidentCentralPointCompare(Solution s1, Solution s2){
		int dim = s1.getNumObjectives();
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		double rho = 0;
		
		for(int i=0; i<dim; i++){
			lambda[i] = 0.5;
			refPoint[i] = 0;
		}
		return eval(s1, lambda, rho, refPoint) < eval(s2, lambda, rho, refPoint);
	}
	
	/***
	 * Method simulates decident with most preferred solution corresponding to point of 
	 * hyperplane maximizing value at X axis (first objective)
	 * 
	 * @param s1
	 * @param s2
	 * @return 
	 */
	public static boolean decidentPrefereXCompare(Solution s1, Solution s2){
		int dim = s1.getNumObjectives();
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		double rho = 0;
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[0] = 1;
		
		for(int i=0; i<dim; i++){
			System.out.println(s1.getObjective(i) + ", " + s2.getObjective(i));
		}
		System.out.println( eval(s1, lambda, rho, refPoint) + ", " + eval(s2, lambda, rho, refPoint));
		
		return eval(s1, lambda, rho, refPoint) < eval(s2, lambda, rho, refPoint);
	}
	
	public static boolean decidentPrefereYCompare(Solution s1, Solution s2){
		int dim = s1.getNumObjectives();
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		double rho = 0;
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[1] = 1;
		
		return eval(s1, lambda, rho, refPoint) < eval(s2, lambda, rho, refPoint);
	}
	
}
