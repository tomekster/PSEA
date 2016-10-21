package core.hyperplane;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import core.Population;
import core.points.ReferencePoint;
import core.points.Solution;
import utils.Geometry;

public class HyperplaneTest {

	double EPS = Geometry.EPS;

	private double refPointDimSum(ReferencePoint rp) {
		double sum = 0.0;
		for (double d : Geometry.normalize(rp.getDim())) {
			sum += d;
		}
		return sum;
	}

	@Test
	public void generateReferencePointsTest() {
		ArrayList<Integer> partitions = new ArrayList<>();
		Hyperplane hp;
		
		partitions.clear();
		partitions.add(12);
		hp = new Hyperplane(3);
		assertEquals(91, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(6);
		hp = new Hyperplane(5);
		assertEquals(210, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(3);
		partitions.add(2);
		hp = new Hyperplane(8);
		assertEquals(156, hp.getReferencePoints().size());
		for (int i=0; i<120; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		for (int i=121; i<156; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(3);
		partitions.add(2);
		hp = new Hyperplane(10);
		assertEquals(275, hp.getReferencePoints().size());
		for (int i=0; i<220; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		for (int i=221; i<275; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(2);
		partitions.add(1);
		hp = new Hyperplane(15);
		assertEquals(135, hp.getReferencePoints().size());
		for (int i=0; i<120; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		for (int i=121; i<135; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1, refPointDimSum(rp), EPS);
		}
	}
	
	@Test
	public void associateTest(){
		Hyperplane hp = new Hyperplane(2);
		ArrayList <ReferencePoint> rp = new ArrayList <> ();
		double dim1[] = {1, 3};
		double dim2[] = {3, 1};
		rp.add(new ReferencePoint(dim1));
		rp.add(new ReferencePoint(dim2));
		hp.setReferencePoints(rp);
		
		double var[] 	= {0 ,0};
		double obj1[] 	= {0 ,1};
		double obj2[]	= {2.0/7 ,4.0/5};
		double obj3[] 	= {3.0/7 ,2.0/5};
		double obj4[] 	= {1 ,0};
		double obj5[] 	= {6 ,2};
		Population pop = new Population();
		Solution s1 = new Solution(var, obj1);
		Solution s2 = new Solution(var, obj2);
		Solution s3 = new Solution(var, obj3);
		Solution s4 = new Solution(var, obj4);
		Solution s5 = new Solution(var, obj5);
		pop.addSolution(s1);
		pop.addSolution(s2);
		pop.addSolution(s3);
		pop.addSolution(s4);
		pop.addSolution(s5);
		
		assertEquals(0, rp.get(0).getNicheCount());
		assertEquals(0, rp.get(1).getNicheCount());
		
		hp.associate(pop);

		rp = hp.getReferencePoints();
		assertEquals(2, rp.get(0).getAssociatedSolutionsQueue().size());
		assertEquals(3, rp.get(1).getAssociatedSolutionsQueue().size());
		
		assertEquals(s2, rp.get(0).getAssociatedSolutionsQueue().poll().getSolution());
		assertEquals(s1, rp.get(0).getAssociatedSolutionsQueue().poll().getSolution());
		assertEquals(s5, rp.get(1).getAssociatedSolutionsQueue().poll().getSolution());
		assertEquals(s3, rp.get(1).getAssociatedSolutionsQueue().poll().getSolution());
		assertEquals(s4, rp.get(1).getAssociatedSolutionsQueue().poll().getSolution());
	}
}
