package utils;

import java.util.ArrayList;

import core.Population;
import core.Solution;
import core.hyperplane.ReferencePoint;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import preferences.Comparison;
import preferences.PreferenceCollector;

public class RACS {
	public static ArrayList<Population> execute(Population population, ArrayList <ReferencePoint> referencePoints, PreferenceCollector pc) {
		ArrayList<Population> fronts = new ArrayList<Population>();
		ArrayList<Solution> front;
		ArrayList <double[]> coherentDirections = new ArrayList<double[]>(); 

		boolean included[] = new boolean[population.size()];
		for(int i=0; i<population.size(); i++){
			included[i] = false;
		}
		
		for(ReferencePoint rp : referencePoints){
			double lambda[] = new double[rp.getNumDimensions()];
			for(int i=0; i < rp.getNumDimensions(); i++){
				lambda[i] = 1/rp.getDim(i);
				if(lambda[i] == Double.NEGATIVE_INFINITY) lambda[i] = -Double.MAX_VALUE;
				if(lambda[i] == Double.POSITIVE_INFINITY) lambda[i] = Double.MAX_VALUE;
			}	
			
			double eps = RATSLP(lambda, pc, population, -1);
			if(eps > 0){
				coherentDirections.add(lambda.clone());
			}
		}
		
		boolean addedToFront = true;
		while(addedToFront){
			front = new ArrayList <Solution>();
			for(int i=0; i<population.size(); i++){
				if(included[i]) { continue; }
				for(double lambda[] : coherentDirections){
					double eps = RATSLP(lambda, pc, population, i);
					if(eps > 0){
						front.add(population.getSolution(i));
						included[i] = true;
					}
				}
			}
			if( front.isEmpty() ){
				addedToFront = false;
				for(int i=0; i<population.size(); i++){
					if(!included[i]){
						front.add(population.getSolution(i));
					}
				}
			}
			Population frontPop = new Population();
			for(Solution s : front){ frontPop.addSolution(s); }
			fronts.add(frontPop);
		}
		System.out.println("#All directions = " + referencePoints.size());
		System.out.println("#CoherentDirections = " + coherentDirections.size());
		System.out.println("#Fronts = " + fronts.size());
		for(int i =0 ; i<fronts.size(); i++){
			Population p = fronts.get(i);
			System.out.println("\t Front i size = " + p.size());
		}
		
		return fronts;
	}

	public static double RATSLP(double[] lambda, PreferenceCollector pc, Population pop, int solutionXId) {
		double epsVal = -Double.MAX_VALUE;
		try{
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.BooleanParam.PreInd, false);
			cplex.setOut(null);
			IloNumVar eps  = cplex.numVar(-Double.MAX_VALUE, Double.MAX_VALUE);
			IloNumVar rho  = cplex.numVar(0, 1000000);
			
			cplex.addMaximize(eps);
			for(Comparison cmp : pc.getComparisons()){
				Solution a = cmp.getBetter();
				Solution b = cmp.getWorse();
				
				
//				System.out.println(a + " >> " + b );
//				System.out.println("eps + " + "rho*" + Geometry.dot(a.getObjectives(), lambda) + " + " + getMax(a,lambda)  + " <= ");
//				System.out.println("rho*" + Geometry.dot(b.getObjectives(), lambda) + " + " + getMax(b,lambda) );
				
				cplex.addLe( cplex.sum( cplex.prod(1.0, eps), 	cplex.prod(rho,Geometry.dot(a.getObjectives(), lambda)), cplex.constant(getMax(a,lambda)) ), 
							 cplex.sum( 						cplex.prod(rho,Geometry.dot(b.getObjectives(), lambda)), cplex.constant(getMax(b,lambda)) ));	
			}
			
			if(solutionXId >= 0){
				for(int i=0; i<pop.getSolutions().size(); i++){
					if(i == solutionXId){ continue; }
					Solution x = pop.getSolution(solutionXId);
					Solution y = pop.getSolution(i);
					cplex.addLe( cplex.sum( cplex.prod(1.0, eps), 	cplex.prod(rho,Geometry.dot(x.getObjectives(), lambda)), cplex.constant(getMax(x,lambda)) ), 
							 cplex.sum( 						cplex.prod(rho,Geometry.dot(y.getObjectives(), lambda)), cplex.constant(getMax(y,lambda)) ));
				}
			}
			
			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

				epsVal = cplex.getValue(eps);
				double rhoVal = cplex.getValue(rho);
				cplex.output().println("EpsValue = " + epsVal);
				cplex.output().println("RhoValue = " + rhoVal);
			}
			cplex.end();
		} catch(IloException e){
			System.out.println("Concert exception cought: " + e);
		}
		return epsVal;
	}

	private static double getMax(Solution a, double[] lambda) {
		double max = -Double.MAX_VALUE;
		for(int i=0; i<a.getNumObjectives(); i++){
			max = Double.max(max,lambda[i] * a.getObjective(i));
		}			
		return max;
	}
}
