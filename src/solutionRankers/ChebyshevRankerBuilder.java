package solutionRankers;

public class ChebyshevRankerBuilder{
	public static ChebyshevRanker getCentralChebyshevRanker(int dim){
		double refPoint[] = new double[dim];
		double lambda[] = new double[dim];
		double rho = 0;
		
		for(int i=0; i<dim; i++){
			refPoint[i] = 0;
			lambda[i] = 0.5;
		}
		
		return new ChebyshevRanker(refPoint, lambda, rho);
	}
	
	public static ChebyshevRanker getMinYZChebyshevRanker(int dim){
		double refPoint[] = new double[dim];
		double lambda[] = new double[dim];
		double rho = 0;
		
		for(int i=0; i<dim; i++){
			refPoint[i] = 0;
			lambda[i] = 1000000;
		}
		lambda[0] = 1;
		
		return new ChebyshevRanker(refPoint, lambda, rho);
	}
	
	public static ChebyshevRanker getMinXZChebyshevRanker(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		double rho = 0;
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[1] = 1;
		
		return new ChebyshevRanker(refPoint, lambda, rho);
	}
}
