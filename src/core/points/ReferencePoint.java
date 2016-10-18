package core.points;

import java.util.Comparator;
import java.util.PriorityQueue;

import core.hyperplane.Association;

public class ReferencePoint extends Solution{
	private int nicheCount;
	private boolean coherent;
	private PriorityQueue<Association> associatedSolutions;
	//Maximum eps achievable in RACS Linear Programming task
	private double eps;
	private double rho;
	private double reward;
	private double penalty;
	private int numViolations;

	public ReferencePoint(int numVariables) {
		super(new double [numVariables], new double[1]);
		this.associatedSolutions = new PriorityQueue<Association>( new Comparator <Association>() {
			@Override
			public int compare(Association o1, Association o2) {
				return Double.compare(o1.getDist(), o2.getDist());
			}
		});
		this.coherent = false;
	}

	public ReferencePoint(ReferencePoint rp) {
		this(rp.getNumDimensions());
		this.variables = rp.getDim().clone();
		this.associatedSolutions = new PriorityQueue<Association> (rp.getAssociatedSolutionsQueue());
		this.coherent = rp.isCoherent();
		this.nicheCount = rp.getNicheCount();
	}
	
	public ReferencePoint(double []dimensions) {
		this(dimensions.length);
		this.variables = dimensions.clone();
	}

	public void incrNicheCount() {
		this.nicheCount++;
	}
	
	public void decrNicheCount() {
		this.nicheCount--;
	}

	public int getNicheCount() {
		return nicheCount;
	}

	public int getNumDimensions() {
		return variables.length;
	}

	public double[] getDim() {
		return variables;
	}
	
	public double getDim(int i){
		return getVariable(i);
	}
	
	public void setDim(int i, double val){
		setVariable(i, val);
	}

	public void setNicheCount(int i) {
		this.nicheCount = i;
	}

	public void resetAssociation() {
		this.nicheCount = 0;
		this.associatedSolutions.clear();
	}

	public void addAssociation(Association association) {
		this.associatedSolutions.add(association);
	}

	public PriorityQueue<Association> getAssociatedSolutionsQueue() {
		return associatedSolutions;
	}

	public boolean isCoherent() {
		return coherent;
	}

	public void setCoherent(boolean coherent) {
		this.coherent = coherent;
	}

	public ReferencePoint copy() {
		ReferencePoint rp = new ReferencePoint(this);
		return rp;
	}
	
	public double getEps(){
		return this.eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public double getReward(){
		return this.reward;
	}
	
	public void setReward(double reward) {
		this.reward = reward;
	}

	public double getPenalty(){
		return this.penalty;
	}
	
	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	public int getNumViolations(){
		return this.numViolations;
	}
	
	public void setNumViolations(int numViolations) {
		this.numViolations = numViolations;
	}

	public void setDim(double[] q) {
		variables = q;
	}

	public void incrDim(int pos, double d) {
		variables[pos] += d; 
	}

	public void setRho(Double rho) {
		this.rho = rho;
	}
}
