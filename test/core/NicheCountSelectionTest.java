package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import core.hyperplane.Hyperplane;
import core.points.ReferencePoint;
import core.points.Solution;
import utils.DegeneratedMatrixException;
import utils.Geometry;

public class NicheCountSelectionTest {
	
	@Test
	public void normalizeTest(){
		Population allFronts = new Population();
		double var[] = {0,0};
		double obj1[] = {3,7};
		double obj2[] = {5,6};
		double obj3[] = {6,4};
		double obj4[] = {10,2};
		allFronts.addSolution(new Solution(var, obj1));
		allFronts.addSolution(new Solution(var, obj2));
		allFronts.addSolution(new Solution(var, obj3));
		allFronts.addSolution(new Solution(var, obj4));
		NicheCountSelection.normalize(2, allFronts);
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
		Solution s1 = new Solution(var, obj1);
		Solution s2 = new Solution(var, obj2);
		Solution s3 = new Solution(var, obj3);
		Solution s4 = new Solution(var, obj4);
		Solution s5 = new Solution(var, obj5);
		allFronts.addSolution(s1);
		allFronts.addSolution(s2);
		allFronts.addSolution(s3);
		allFronts.addSolution(s4);
		allFronts.addSolution(s5);
		
		Population allButLastFront = new Population(); 
		Population kPoints = new Population();
		try {
			kPoints = NicheCountSelection.selectKPoints(2,allFronts, allButLastFront, allFronts, 2, hp);
		} catch (DegeneratedMatrixException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(2, kPoints.size());
		assertEquals(1, hp.getReferencePoints().get(0).getNicheCount());
		assertEquals(1, hp.getReferencePoints().get(1).getNicheCount());
		assertEquals(s5, kPoints.getSolutions().get(1));
		assertEquals(s2, kPoints.getSolutions().get(0));
	}
}
