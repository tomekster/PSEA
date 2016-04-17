package igd;

import java.util.ArrayList;

import core.Population;
import core.Problem;
import core.Solution;
import core.hyperplane.ReferencePoint;

public class TargetFrontGenerator {

	public static Population generate(ArrayList<ReferencePoint> referencePoints,
			Problem problem) {

		Population res = new Population();
		
		switch (problem.getName()) {
		case "DTLZ1":
			for (ReferencePoint r : referencePoints) {
				double var[] = new double[0];
				double obj[] = new double[r.getNumDimensions()];
				double sum = 0;
				for (int i = 0; i < r.getNumDimensions(); i++) {
					sum += r.getDim(i);
				}
				for(int i=0; i< r.getNumDimensions(); i++){
					obj[i] = r.getDim(i) * (0.5 / sum);
				}
				res.addSolution(new Solution(var, obj));
			}
			break;
		case "DTLZ2":
		case "DTLZ3":
		case "DTLZ4":
			double sqr_sum;
			for (ReferencePoint r : referencePoints) {
				sqr_sum = 0;
				for (double d : r.getDimensions()) {
					sqr_sum += d*d;
				}
				
				double div = Math.sqrt(sqr_sum);
				
				double var[] = new double[0];
				double obj[] = new double[r.getNumDimensions()];
				for (int i = 0; i < r.getNumDimensions(); i++) {
					obj[i] = r.getDim(i) / div;
				}
				res.addSolution(new Solution(var, obj));
			}
			break;
		}
		
		return res;
	}
}