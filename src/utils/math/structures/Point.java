package utils.math.structures;

public class Point {
	
	double [] vals;
	
	public Point(int numDim){
		this.vals = new double[numDim];
	}
	
	public Point(double [] vals){
		this.vals = vals.clone();
	}
	
	public Point(Point p){
		this(p.getDim());
	}
	
	public int getNumDim(){
		return vals.length;
	}

	public double getDim(int i) {
		return vals[i];
	}

	public double[] getDim() {
		return this.vals;
	}
	
	public Point shift(Vector v){
		if(getNumDim() != v.getNumDim() ){
			throw new IllegalArgumentException("Point and vector do not have the same dimensionality.");
		}
		
		double[] res = new double[getNumDim()];
		for(int i=0; i<res.length; i++){
			res[i] = getDim(i) + v.getDim(i);
		}
		
		return new Point(res);
	}
	
	public Point copy(){
		return new Point(this);
	}
}
