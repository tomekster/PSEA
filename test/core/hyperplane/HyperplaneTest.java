package core.hyperplane;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import core.points.ReferencePoint;
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
		SolutionDirections hp;
		ArrayList<Integer> partitions = new ArrayList<>();

		partitions.add(4);
		hp = new SolutionDirections(3);
		assertEquals(15, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}

		partitions.clear();
		partitions.add(12);
		hp = new SolutionDirections(3);
		assertEquals(91, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(6);
		hp = new SolutionDirections(5);
		assertEquals(210, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(3);
		partitions.add(2);
		hp = new SolutionDirections(8);
		assertEquals(156, hp.getReferencePoints().size());
		for (int i=0; i<120; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		for (int i=121; i<156; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(0.5, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(3);
		partitions.add(2);
		hp = new SolutionDirections(10);
		assertEquals(275, hp.getReferencePoints().size());
		for (int i=0; i<220; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		for (int i=221; i<275; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(0.5, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(2);
		partitions.add(1);
		hp = new SolutionDirections(15);
		assertEquals(135, hp.getReferencePoints().size());
		for (int i=0; i<120; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		for (int i=121; i<135; i++) {
			ReferencePoint rp = hp.getReferencePoints().get(i);
			assertEquals(0.5, refPointDimSum(rp), EPS);
		}
	}
}
