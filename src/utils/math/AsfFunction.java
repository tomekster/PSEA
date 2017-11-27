package utils.math;

import algorithm.evolutionary.solutions.Solution;
import utils.math.structures.Line;
import utils.math.structures.Point;
import utils.math.structures.Vector;

public class AsfFunction {
	private double lambda[];
	private Vector vector;
	private double rho;
	private Point refPoint;
	
	public AsfFunction(double lambda[], double rho, Point refPoint){
		this.refPoint = new Point(refPoint);
		this.vector = (new Vector(Geometry.invert(lambda))).subtract(refPoint);
		this.lambda = lambda;
		this.rho = rho;
	}

	public double eval(double obj[]){
		double res = Double.NEGATIVE_INFINITY;
		double sum = 0;
		for(int i=0; i<obj.length; i++){
			double mult = lambda[i] * Math.abs(obj[i] - refPoint.getDim(i));
			res = Double.max(mult, res);
			sum += mult;
		}
		res += sum * rho;
		return res;
	}
	
	public double eval(Solution s){
		return eval(s.getObjectives());
	}
	
	public Line getAsfLine(){
		return new Line(this.refPoint, this.refPoint.shift(this.vector));
	}
	
	public double[] getLambda() {
		return lambda;
	}
	
	public void setLambda(double[] lambda) {
		this.lambda = lambda;
	}
	
	public double getLambda(int i){
		return lambda[i];
	}
	
	public double getRho() {
		return rho;
	}

	public AsfFunction copy() {
		return new AsfFunction(lambda, rho, refPoint);
	}
	
	public int getNumDim(){
		return lambda.length;
	}
}
