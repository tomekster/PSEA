package utils.math.structures;

public class Line {
	private Point a, b;
	
	public Line(Point a, Point b){
		if(a.getNumDim() != b.getNumDim()){
			throw new IllegalArgumentException("Points in line initialization have different dimesnionalities");
		}
		this.a = a;
		this.b = b;
	}
	
	public Vector getVector(){
		double [] res = new double[a.getNumDim()];
		for(int i=0; i<a.getNumDim(); i++){
			res[i] = a.getDim(i) - b.getDim(i);
		}
		return new Vector(res);
	}
	
	public Point getA(){
		return a;
	}
	
	public Point getB(){
		return b;
	}
}
