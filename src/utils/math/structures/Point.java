package utils.math.structures;

public class Point {
	private static final long serialVersionUID = -7245293372099881248L;
	
	double [] vals;
	
	public Point(double [] vals){
		this.vals = vals.clone();
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
}
