package core.hyperplane;

import java.util.PriorityQueue;

import utils.MyComparator;

public class ReferencePoint {
	private double dimensions[];
	private int numDimensions;
	private int nicheCount;
	private PriorityQueue<Association> associatedSolutions;

	public ReferencePoint(int numDimensions) {
		this.numDimensions = numDimensions;
		this.dimensions = new double[numDimensions];
		this.associatedSolutions = new PriorityQueue<Association>(MyComparator.associationComparator);
		for (int i = 0; i < numDimensions; i++)
			this.dimensions[i] = 0.0;
	}

	public ReferencePoint(ReferencePoint rp) {
		this(rp.getNumDimensions());
		this.dimensions = rp.dimensions.clone();
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

	public double[] getDimensions() {
		return this.dimensions;
	}

	public void setNicheCount(int i) {
		this.nicheCount = 0;
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
		for (double d : dimensions) {
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
}
