package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solutions.VectorSolution;
import algorithm.nsgaiii.EnvironmentalSelection;
import algorithm.nsgaiii.hyperplane.Hyperplane;
import algorithm.nsgaiii.hyperplane.ReferencePoint;
import utils.math.Geometry;

public class NicheCountSelectionTest {
	
	@Test
	public void normalizeTest(){
		Population allFronts = new Population();
		double var[] = {0,0};
		double obj1[] = {3,7};
		double obj2[] = {5,6};
		double obj3[] = {6,4};
		double obj4[] = {10,2};
		allFronts.addSolution(new VectorSolution(var, obj1));
		allFronts.addSolution(new VectorSolution(var, obj2));
		allFronts.addSolution(new VectorSolution(var, obj3));
		allFronts.addSolution(new VectorSolution(var, obj4));
		EnvironmentalSelection.normalize(2, allFronts);
		assertEquals(0, allFronts.getSolution(0).getObjective(0), Geometry.EPS);
		assertEquals(1, allFronts.getSolution(0).getObjective(1), Geometry.EPS);
		assertEquals(2.0/7, allFronts.getSolution(1).getObjective(0), Geometry.EPS);
		assertEquals(4.0/5, allFronts.getSolution(1).getObjective(1), Geometry.EPS);
		assertEquals(3.0/7, allFronts.getSolution(2).getObjective(0), Geometry.EPS);
		assertEquals(2.0/5, allFronts.getSolution(2).getObjective(1), Geometry.EPS);
		assertEquals(1, allFronts.getSolution(3).getObjective(0), Geometry.EPS);
		assertEquals(0, allFronts.getSolution(3).getObjective(1), Geometry.EPS);
	}
	
	@Test
	public void selectKPointsTest(){
		Hyperplane hp = new Hyperplane(2);
		ArrayList <ReferencePoint> rp = new ArrayList <> ();
		double dim1[] = {1, 3};
		double dim2[] = {3, 1};
		rp.add(new ReferencePoint(dim1));
		rp.add(new ReferencePoint(dim2));
		hp.setReferencePoints(rp);
		assertEquals(0, hp.getReferencePoints().get(0).getNicheCount());
		assertEquals(0, hp.getReferencePoints().get(1).getNicheCount());
		double var[] = {0,0};
		double obj1[] = {3,7};
		double obj2[] = {5,6};
		double obj3[] = {6,4};
		double obj4[] = {10,2};
		double obj5[] = {45,12};
		Population allFronts = new Population();
		VectorSolution s1 = new VectorSolution(var, obj1);
		VectorSolution s2 = new VectorSolution(var, obj2);
		VectorSolution s3 = new VectorSolution(var, obj3);
		VectorSolution s4 = new VectorSolution(var, obj4);
		VectorSolution s5 = new VectorSolution(var, obj5);
		allFronts.addSolution(s1);
		allFronts.addSolution(s2);
		allFronts.addSolution(s3);
		allFronts.addSolution(s4);
		allFronts.addSolution(s5);
		
		Population allButLastFront = new Population(); 
		Population kPoints = EnvironmentalSelection.selectKPoints(2, allButLastFront, allFronts, 2, hp);
		
		assertEquals(2, kPoints.size());
		assertEquals(1, hp.getReferencePoints().get(0).getNicheCount());
		assertEquals(1, hp.getReferencePoints().get(1).getNicheCount());
		assertEquals(s5, kPoints.getSolutions().get(1));
		assertEquals(s2, kPoints.getSolutions().get(0));
	}
}
