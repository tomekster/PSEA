package core;

public class Solution{

	private int numVariables;
	private double[] variables;
	private double[] lowerBound;
	private double[] upperBound;
	
	public Solution(int numVariables){
		this.numVariables = numVariables;
		this.variables = new double[numVariables];
		this.lowerBound = new double[numVariables];
		this.upperBound = new double[numVariables];
		for(int i=0 ; i<numVariables; i++){
			variables[i] = 0.0;
			lowerBound[i] = Double.MIN_VALUE;
			lowerBound[i] = Double.MAX_VALUE;
		}
	}
	
	public Solution(double vars[]){
		this.numVariables = vars.length;
		this.variables = new double[numVariables];
		this.lowerBound = new double[numVariables];
		this.upperBound = new double[numVariables];
		for(int i=0 ; i<numVariables; i++){
			variables[i] = vars[i];
			lowerBound[i] = Double.MIN_VALUE;
			lowerBound[i] = Double.MAX_VALUE;
		}
	}
	
	public Solution(Solution solution){
		this(solution.numVariables);
		for(int i=0 ; i<numVariables; i++){
			variables[i] = solution.getVariable(i);
			lowerBound[i] = Double.MIN_VALUE;
			lowerBound[i] = Double.MAX_VALUE;
		}
	}
	
	public int getNumVariables(){
		return this.numVariables;
	}
	
	public double getVariable(int pos){
		return variables[pos];
	}
	
	public void setVariable(int pos, double val){
		variables[pos] = val;
	}
	
	public double[] getLowerBound(){
		return lowerBound;
	}
	
	public void setLowerBound(double[] lowerBound){
		this.lowerBound = lowerBound;
	}
	
	public double[] getUpperBound(){
		return upperBound;
	}

	public void setUpperBound(double[] upperBound){
		this.upperBound = upperBound;
	}
	
	public double getLowerBound(int pos){
		return lowerBound[pos];
	}
	
	public double getUpperBound(int pos){
		return upperBound[pos];
	}

	public Solution copy() {
		return new Solution(this);
	}

}
