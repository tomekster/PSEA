package algorithm.rankers;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.VectorSolution;
import algorithm.psea.preferences.DMmodel;
import artificialDM.AsfDM;
import utils.math.Geometry;

public class WeightedBordaRankerTest {
	@Test
	public void Test(){
		double referencePoint[] = {0,0,0};
		
		double lambda1[] = {0.2, 0.2, 0.6};
		double lambda2[] = {0.1, 0.4, 0.5};
		double lambda3[] = {0.3, 0.6, 0.1};
		
		//Just to mark the solution number
		double var1[] = {1};
		double var2[] = {2};
		double var3[] = {3};
		
		double obj1[] = {1, 2, 3};
		double obj2[] = {2, 3, 1};
		double obj3[] = {3, 1 ,2};
		
		DMmodel wbr = new DMmodel(new double[]{0,0,0});
		wbr.getAsfBundle().setReferencePoint(referencePoint);
		wbr.clearDMs();
		
		AsfDM asfDM1 = new AsfDM(referencePoint, lambda1);
		AsfDM asfDM2 = new AsfDM(referencePoint, lambda2);
		AsfDM asfDM3 = new AsfDM(referencePoint, lambda3);
		
		asfDM1.setNumViolations(1);
		asfDM2.setNumViolations(0);
		asfDM3.setNumViolations(2);
		
		wbr.addAsfDM(asfDM1);
		wbr.addAsfDM(asfDM2);
		wbr.addAsfDM(asfDM3);
		
		Population pop = new Population();
		
		VectorSolution s1 = new VectorSolution(var1, obj1);
		VectorSolution s2 = new VectorSolution(var2, obj2);
		VectorSolution s3 = new VectorSolution(var3, obj3);
		
		pop.addSolution(s1);
		pop.addSolution(s2);
		pop.addSolution(s3);

		asfDM1.sort(pop.getSolutions());
		assertEquals(s2, pop.getSolution(0));
		assertEquals(s3, pop.getSolution(1));
		assertEquals(s1, pop.getSolution(2));
		
		asfDM2.sort(pop.getSolutions());
		assertEquals(s3, pop.getSolution(0));
		assertEquals(s2, pop.getSolution(1));
		assertEquals(s1, pop.getSolution(2));
		
		asfDM3.sort(pop.getSolutions());
		assertEquals(s3, pop.getSolution(0));
		assertEquals(s1, pop.getSolution(1));
		assertEquals(s2, pop.getSolution(2));
		
		
		pop.getSolutions().clear();
		pop.addSolution(s1);
		pop.addSolution(s2);
		pop.addSolution(s3);
		
		HashMap<VectorSolution, Double> bordaPoints = wbr.getBordaPointsForSolutions(pop);
		assertEquals(1.5 + 2.0/3, bordaPoints.get(s1), Geometry.EPS);
		assertEquals(3.5 + 1.0/3, bordaPoints.get(s2), Geometry.EPS);
		assertEquals(5, bordaPoints.get(s3), Geometry.EPS);
		
		Population sorted = wbr.sortSolutions(pop);
		assertEquals(3, sorted.getSolution(0).getVariable(0), Geometry.EPS);
		assertEquals(2, sorted.getSolution(1).getVariable(0), Geometry.EPS);
		assertEquals(1, sorted.getSolution(2).getVariable(0), Geometry.EPS);
	}
}
