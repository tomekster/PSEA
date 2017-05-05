package solutionRankers;

import utils.Geometry;

public class ChebyshevRankerBuilder{
	public static ChebyshevRanker getCentralChebyshevRanker(int dim){
		double refPoint[] = new double[dim];
		double lambda[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 0.5;
			refPoint[i] = 0;
		}
		
		lambda = Geometry.normalize(lambda);
		
		return new ChebyshevRanker(refPoint, lambda, "CentralChebyshevRanker");
	}
	
	public static ChebyshevRanker getMinYZChebyshevRanker(int dim){
		double refPoint[] = new double[dim];
		double lambda[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[0] = 1;
		
		lambda = Geometry.normalize(lambda);
		
		return new ChebyshevRanker(refPoint, lambda, "MinYZChebyshevRanker");
	}
	
	public static ChebyshevRanker getMinXZChebyshevRanker(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[1] = 1;
		
		lambda = Geometry.normalize(lambda);
		
		return new ChebyshevRanker(refPoint, lambda, "MinXZChebyshevRanker");
	}
}
