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
	private PriorityQueue<Association> nichedAssociations;
	private PriorityQueue<Association> lastFrontAssociations;

	public ReferencePoint(int numDim) {
		super(new double[1], new double [numDim]);
		this.nichedAssociations = new PriorityQueue<Association>();
		this.lastFrontAssociations = new PriorityQueue<Association>();
	}

	public ReferencePoint(ReferencePoint rp) {
		this(rp.getNumDimensions());
		this.obj = rp.getDim().clone();
		this.nichedAssociations = new PriorityQueue<Association> (rp.getNichedAssociationsQueue());
		this.lastFrontAssociations = new PriorityQueue<Association> (rp.getLastFrontAssociationsQueue());
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

	public ReferencePoint copy() {
		ReferencePoint rp = new ReferencePoint(this);
		return rp;
	}

	public void setDim(double[] q) {
		obj = q.clone();
	}

	public void incrDim(int pos, double d) {
		obj[pos] += d; 
	}
}
