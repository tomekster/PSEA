package algorithm.evolutionary.interactive.artificialDM;

import java.util.Arrays;

import algorithm.evolutionary.solutions.Solution;
import utils.math.Geometry;
import utils.math.structures.Point;
import utils.math.structures.Vector;

public class LpDm extends RferencePointDm{

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
}