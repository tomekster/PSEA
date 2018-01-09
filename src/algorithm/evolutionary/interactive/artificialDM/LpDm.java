package algorithm.evolutionary.interactive.artificialDM;

import java.util.Arrays;

import algorithm.evolutionary.solutions.Solution;
import utils.math.Geometry;
import utils.math.structures.Line;
import utils.math.structures.Point;
import utils.math.structures.Vector;

public class LpDm extends ReferencePointDm{

	int p;
	
	Point referencePoint;
	
	public LpDm(int p, Point refPoint) {
		this.p = p;
		this.referencePoint = refPoint.copy();
	}
	
	@Override
	public double eval(Solution a) {
		return Geometry.norm(new Vector(a.getObjectives()).subtract(getReferencePoint()).getDim() , p);
	}

	@Override
	public Point getReferencePoint() {
		return this.referencePoint;
	}
	
	@Override
	public String toString(){
		return "L" + p + "Descision Maker" +
				"\n refPoint=" + Arrays.toString(this.getReferencePoint().getDim());
	}

	@Override
	public Line getDirection() {
		double A[] = new double[referencePoint.getNumDim()];
		double B[] = new double[referencePoint.getNumDim()];
		for(int i=0; i<B.length; i++){
			B[i] = 1;
		}
		return new Line(new Point(A), new Point(B));
	}
	
}