package core.hyperplane;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ReferencePoint {
	private double dimensions[];
	private int numDimensions;
	private int nicheCount;
	private boolean coherent;
	private PriorityQueue<Association> associatedSolutions;
	//Rho value maximizing eps in RACS Linear Programming task
	private double rho;
	//Maximum eps achievable in RACS Linear Programming task
	private double eps;

	public ReferencePoint(int numDimensions) {
		this.numDimensions = numDimensions;
		this.dimensions = new double[numDimensions];
		this.associatedSolutions = new PriorityQueue<Association>( new Comparator <Association>() {
			@Override
			public int compare(Association o1, Association o2) {
				return Double.compare(o1.getDist(), o2.getDist());
			}
		});
		this.coherent = false;
		for (int i = 0; i < numDimensions; i++){
			this.dimensions[i] = 0.0;
		}
	}

	public ReferencePoint(ReferencePoint rp) {
		this(rp.getNumDimensions());
		this.dimensions = rp.getDim().clone();
		this.associatedSolutions = new PriorityQueue<Association> (rp.getAssociatedSolutionsQueue());
		this.coherent = rp.isCoherent();
		this.nicheCount = rp.getNicheCount();
	}
	
	public ReferencePoint(double []dimensions) {
		this(dimensions.length);
		this.dimensions = dimensions.clone();
	}

	public double getDim(int index) {
		return this.dimensions[index];
	}
	
	public void setDim(int index, double val) {
		this.dimensions[index] = val;
	}
	
	public void incrDim(int index, double value) {
		this.dimensions[index] += value;
	}

	public void decrDim(int index, double value) {
		this.dimensions[index] += value;
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
		return this.numDimensions;
	}

	public double[] getDim() {
		return this.dimensions;
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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Dim:");
		for (double d : dimensions) {
			sb.append(" " + d);
		}
		return sb.toString();
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
	
	public void setDimensions(double dim[]){
		this.dimensions = dim;
	}
	
	public double getRho(){
		return this.rho;
	}

	public void setRho(double rho) {
		this.rho = rho;
	}
	
	public double getEps(){
		return this.eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}
}
