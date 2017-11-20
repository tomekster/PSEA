package algorithm.implementations.nsgaiii.hyperplane;

import java.io.Serializable;
import java.util.PriorityQueue;

public class ReferencePoint implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1962134424334714602L;
	private PriorityQueue<Association> nichedAssociations;
	private PriorityQueue<Association> lastFrontAssociations;

	double dim[];
	
	public ReferencePoint(int numDim) {
		this.dim = new double [numDim];
		this.nichedAssociations = new PriorityQueue<Association>();
		this.lastFrontAssociations = new PriorityQueue<Association>();
	}

	public ReferencePoint(ReferencePoint rp) {
		this(rp.getNumDimensions());
		this.dim = rp.getDim().clone();
		this.nichedAssociations = new PriorityQueue<Association> (rp.getNichedAssociationsQueue());
		this.lastFrontAssociations = new PriorityQueue<Association> (rp.getLastFrontAssociationsQueue());
	}
	
	public ReferencePoint(double []dimensions) {
		this(dimensions.length);
		this.dim = dimensions.clone();
	}

	public int getNicheCount() {
		return this.nichedAssociations.size();
	}

	public int getNumDimensions() {
		return dim.length;
	}

	public double[] getDim() {
		return dim;
	}
	
	public double getDim(int i){
		return dim[i];
	}
	
	public void setDim(int i, double val){
		dim[i] = val;
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
		dim = q.clone();
	}

	public void incrDim(int pos, double d) {
		dim[pos] += d; 
	}
}
