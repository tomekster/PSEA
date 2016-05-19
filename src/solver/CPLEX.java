package solver;

import core.Population;
import core.Solution;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import preferences.Comparison;
import preferences.PreferenceCollector;

public class CPLEX {

	public boolean solveLpForX(Solution X, int maxDim, Population pop, PreferenceCollector pc){
		int numObj = pc.getComparisons().get(0).getBetter().getNumObjectives();
		try{
			IloCplex cplex = new IloCplex();
			IloNumVar[] lambda  = cplex.numVarArray(numObj,0,Double.MAX_VALUE);
			IloNumVar[] eps  = cplex.numVarArray(1, Double.MIN_VALUE, Double.MAX_VALUE);
			//Lambdas have to sum to 1
			IloLinearNumExpr exprLambdaSum = cplex.linearNumExpr();
			for(int i=0; i<numObj; i++){
				exprLambdaSum.addTerm(1.0, lambda[i]);
			}
			cplex.addEq(exprLambdaSum, 1);
			
			// LHS for all constraints
			IloLinearNumExpr lhs = cplex.linearNumExpr();
			lhs.addTerm(X.getObjective(maxDim), lambda[maxDim]);
			
			//Maximum dimension constraints for X
			for(int i=0; i<numObj; i++){
				if(i != maxDim){
					IloLinearNumExpr rhs = cplex.linearNumExpr();
					rhs.addTerm(X.getObjective(i), lambda[i]);
					cplex.addGe(lhs, rhs);
				}
			}
			
			//Maximum dimension constraints for Y
			for(Solution Y : pop.getSolutions()){
				if(Y == X) continue;
				for(int i=0; i<numObj; i++){
					IloLinearNumExpr rhs = cplex.linearNumExpr();
					rhs.addTerm(Y.getObjective(i), lambda[i]);
					cplex.addGe(lhs, rhs);
				}
			}
			
			//Preference constraints
			for(Comparison c : pc.getComparisons()){
				Solution a = c.getBetter();
				Solution b = c.getWorse();
				
				
			}
		} catch(IloException e){
			System.out.println("Concert exceptiion cought: " + e);
		}
		return false;
	}

	public void solvePreferenceLP(Population pop, PreferenceCollector pc) {
		int numComparisons = pc.getComparisons().size();
		if (numComparisons == 0) {
			System.err.println("No comparisons - cannot construct LP");
			return;
		}
		int numObj = pc.getComparisons().get(0).getBetter().getNumObjectives();

		for (Solution X : pop.getSolutions()) {
			for (int maxDim = 0; maxDim < numObj; maxDim++) {
				boolean frontMember = solveLpForX(X, maxDim, pop, pc);
			}
		}
	}

	public void exampleLP() {
		try {
			IloCplex cplex = new IloCplex();

			double[] lb = { 0.0, 0.0, 0.0 };
			double[] ub = { 40.0, Double.MAX_VALUE, Double.MAX_VALUE };
			IloNumVar[] x = cplex.numVarArray(3, lb, ub);

			double[] objvals = { 1.0, 2.0, 3.0 };
			cplex.addMaximize(cplex.scalProd(x, objvals));

			cplex.addLe(cplex.sum(cplex.prod(-1.0, x[0]), cplex.prod(1.0, x[1]), cplex.prod(1.0, x[2])), 20.0);
			cplex.addLe(cplex.sum(cplex.prod(1.0, x[0]), cplex.prod(-3.0, x[1]), cplex.prod(1.0, x[2])), 30.0);

			if (cplex.solve()) {
				cplex.output().println("Solution status = " + cplex.getStatus());
				cplex.output().println("Solution value  = " + cplex.getObjValue());

				double[] val = cplex.getValues(x);
				int ncols = cplex.getNcols();
				for (int j = 0; j < ncols; ++j)
					cplex.output().println("Column: " + j + " Value = " + val[j]);
			}
			cplex.end();
		} catch (IloException e) {
			System.out.println("Concert exceptiion cought: " + e);
		}
	}
}
