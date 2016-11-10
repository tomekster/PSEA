package igd;

import java.util.ArrayList;

import core.Population;
import core.Problem;
import core.points.ReferencePoint;
import core.points.Solution;
import utils.Geometry;

public class TargetFrontGenerator {

	public static Population generate(ArrayList<ReferencePoint> referencePoints,
			Problem problem) {

		Population res = new Population();
		
		switch (problem.getName()) {
		case "DTLZ1":
			for (ReferencePoint r : referencePoints) {
				double var[] = new double[0];
				double obj[] = Geometry.lineCrossDTLZ1HyperplanePoint(r.getDim());
				res.addSolution(new Solution(var, obj));
			}
			break;
		case "DTLZ2":
		case "DTLZ3":
		case "DTLZ4":
			for (ReferencePoint r : referencePoints) {
				double var[] = new double[0];
				double obj[] = Geometry.lineCrossDTLZ234HyperspherePoint(r.getDim()); 
				res.addSolution(new Solution(var, obj));
			}
			break;
		default:
		}
		
		return res;
	}
}