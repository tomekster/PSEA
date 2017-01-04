package core.points;

import java.util.Comparator;
import java.util.PriorityQueue;

import core.hyperplane.Association;

public class ReferencePoint extends Solution{
	private boolean coherent;
	private PriorityQueue<Association> nichedAssociations;
	private PriorityQueue<Association> lastFrontAssociations;
	//Maximum eps achievable in RACS Linear Programming task
	private double eps;
	private double rho;
	private double reward;
	private double penalty;
	private int numViolations;

	public ReferencePoint(int numVariables) {
		super(new double [numVariables], new double[1]);
		this.nichedAssociations = new PriorityQueue<Association>( new Comparator <Association>() {
			@Override
			public int compare(Association o1, Association o2) {
				return Double.compare(o1.getDist(), o2.getDist());
			}
		});
		this.lastFrontAssociations = new PriorityQueue<Association>( new Comparator <Association>() {
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
		this.nichedAssociations = new PriorityQueue<Association> (rp.getNichedAssociationsQueue());
		this.lastFrontAssociations = new PriorityQueue<Association> (rp.getLastFrontAssociationsQueue());
		this.coherent = rp.isCoherent();
	}
	
	public ReferencePoint(double []dimensions) {
		this(dimensions.length);
		this.variables = dimensions.clone();
	}

	public int getNicheCount() {
		return this.nichedAssociations.size();
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
	
	public void resetAssociation() {
		this.nichedAssociations.clear();
		this.lastFrontAssociations.clear();
	}

	public void addNichedAssociation(Association association) {
		this.nichedAssociations.add(association);
	}

	public PriorityQueue<Association> getNichedAssociationsQueue() {
		return nichedAssociations;
	}
	
	public void addLastFrontAssociation(Association association) {
		this.lastFrontAssociations.add(association);
	}

	public PriorityQueue<Association> getLastFrontAssociationsQueue() {
		return lastFrontAssociations;
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
