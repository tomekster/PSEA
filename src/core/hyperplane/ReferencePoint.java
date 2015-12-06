package core.hyperplane;

public class ReferencePoint {
	private double dimensions[];
	private int numDimensions;
	
	public ReferencePoint(int numDimensions) {
		this.numDimensions = numDimensions;
		this.dimensions = new double[numDimensions];
		for(int i=0; i< numDimensions; i++) this.dimensions[i] = 0.0;
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

	public int getNumDimensions() {
		return this.numDimensions;
	}

}
