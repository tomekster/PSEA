package solutionRankers;

import utils.Geometry;

public class ChebyshevRankerBuilder{
	public static ChebyshevRanker get1CentralChebyshevRanker(int dim){
		double refPoint[] = new double[dim];
		double lambda[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1;
			refPoint[i] = 0;
		}
		
		lambda = Geometry.normalize(lambda);
		
		return new ChebyshevRanker(refPoint, lambda, "1CentralChebyshevRanker");
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
	
	public static ChebyshevRanker get2LeftMostImportant(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1;
			refPoint[i] = 0;
		}
		lambda[0] = dim-1;
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "2LeftMostImportant");
	}
	
	public static ChebyshevRanker get3CentralMostImportant(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1;
			refPoint[i] = 0;
		}
		lambda[dim/2] = dim-1;
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "3CentralMostImportant");
	}
	
	public static ChebyshevRanker get4RightMostImportant(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1;
			refPoint[i] = 0;
		}
		lambda[dim-1] = dim-1;
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "4RightMostImportant");
	}
	
	public static ChebyshevRanker get5LeftIrrelevant(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[0] = 1;
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "5LeftIrrelevant");
	}
	public static ChebyshevRanker get6CentralIrrelevant(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[dim/2] = 1;
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "get6CentralIrrelevant");
	}
	public static ChebyshevRanker get7RightIrrelevant(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = 1000000;
			refPoint[i] = 0;
		}
		lambda[dim-1] = 1;
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "7RightIrrelevant");
	}
	public static ChebyshevRanker get8LinearyIncreasing(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = i+1;
			refPoint[i] = 0;
		}
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "8LinearyIncreasing");
	}
	public static ChebyshevRanker get9LinearyDecreasing(int dim){
		double lambda[] = new double[dim];
		double refPoint[] = new double[dim];
		
		for(int i=0; i<dim; i++){
			lambda[i] = dim-i;
			refPoint[i] = 0;
		}
		lambda = Geometry.normalize(lambda);
		return new ChebyshevRanker(refPoint, lambda, "9LinearyDecreasing");
	}
}
