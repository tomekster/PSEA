package core.hyperplane;

import java.util.PriorityQueue;

import utils.MyComparator;

public class ReferencePoint {
	private double normDimensions[];
	private double dimensions[];
	private int numDimensions;
	private int nicheCount;
	private boolean coherent;
	private PriorityQueue<Association> associatedSolutions;

	public ReferencePoint(int numDimensions) {
		this.numDimensions = numDimensions;
		this.dimensions = new double[numDimensions];
		this.normDimensions = new double[numDimensions];
		this.associatedSolutions = new PriorityQueue<Association>(MyComparator.associationComparator);
		this.coherent = false;
		for (int i = 0; i < numDimensions; i++){
			this.normDimensions[i] = 0.0;
		}
	}

	public ReferencePoint(ReferencePoint rp) {
		this.numDimensions = rp.getNumDimensions();
		this.dimensions = rp.getNormDimensions().clone();
		this.normDimensions = rp.getNormDimensions().clone();
		this.associatedSolutions = new PriorityQueue<Association> (rp.getAssociatedSolutionsQueue());
		this.coherent = rp.isCoherent();
		this.nicheCount = rp.getNicheCount();
	}

	public double getDim(int index) {
		return this.dimensions[index];
	}
	
	public void setDim(int index, double val) {
		this.dimensions[index] = val;
	}
	
	public double getNormDim(int index) {
		return this.normDimensions[index];
	}

	public void setNormDim(int index, double val) {
		this.normDimensions[index] = val;
	}

	public void incrNormDim(int index, double value) {
		this.normDimensions[index] += value;
	}

	public void decrNormDim(int index, double value) {
		this.normDimensions[index] += value;
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

	public double[] getDimensions() {
		return this.dimensions;
	}

	public double[] getNormDimensions() {
		return this.normDimensions;
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
		sb.append("[");
		for (double d : normDimensions) {
			sb.append(d + ", ");
		}
		sb.replace(sb.length() - 2, sb.length(), "]\n");
		if (!associatedSolutions.isEmpty()) {
			sb.append("Associations: [\n");
			for (Association as : associatedSolutions) {
				sb.append(as.toString() + ",\n");
			}
			sb.replace(sb.length() - 2, sb.length(), "]");
		} else {
			sb.append("Associations: none");
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
	
	public void setNormDimensions(double dim[]){
		this.normDimensions = dim;
	}
}
