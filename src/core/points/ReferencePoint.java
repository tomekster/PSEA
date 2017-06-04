package core.points;

import java.io.Serializable;
import java.util.PriorityQueue;

import core.hyperplane.Association;
import utils.Geometry;

public class ReferencePoint extends Solution implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1962134424334714602L;
	private boolean coherent;
	private PriorityQueue<Association> nichedAssociations;
	private PriorityQueue<Association> lastFrontAssociations;
	private double reward;
	private double penalty;
	private int numViolations;

	public ReferencePoint(int numDim) {
		super(new double[1], new double [numDim]);
		this.nichedAssociations = new PriorityQueue<Association>();
		this.lastFrontAssociations = new PriorityQueue<Association>();
		this.coherent = false;
	}

	public ReferencePoint(ReferencePoint rp) {
		this(rp.getNumDimensions());
		this.obj = rp.getDim().clone();
		this.nichedAssociations = new PriorityQueue<Association> (rp.getNichedAssociationsQueue());
		this.lastFrontAssociations = new PriorityQueue<Association> (rp.getLastFrontAssociationsQueue());
		this.coherent = rp.isCoherent();
	}
	
	public ReferencePoint(double []dimensions) {
		this(dimensions.length);
		this.obj = dimensions.clone();
	}

	public int getNicheCount() {
		return this.nichedAssociations.size();
	}

	public int getNumDimensions() {
		return obj.length;
	}

	public double[] getDim() {
		return obj;
	}
	
	public double[] getPoint(){
		return Geometry.normalize(Geometry.invert(this.getDim()));
	}
	
	public double getDim(int i){
		return getObjective(i);
	}
	
	public void setDim(int i, double val){
		setObjective(i, val);
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
		obj = q.clone();
	}

	public void incrDim(int pos, double d) {
		obj[pos] += d; 
	}
}
