package core.hyperplane;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import utils.MyComparator;

public class HyperplaneTest {

	double EPS = MyComparator.EPS;

	private double refPointDimSum(ReferencePoint rp) {
		double sum = 0.0;
		for (double d : rp.getDimensions()) {
			sum += d;
		}
		return sum;
	}

	@Test
	public void referencePointsGenerationTest() {
		Hyperplane hp;
		ArrayList<Integer> partitions = new ArrayList<>();

		partitions.add(4);
		hp = new Hyperplane(3, partitions);
		assertEquals(15, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}

		partitions.clear();
		partitions.add(12);
		hp = new Hyperplane(3, partitions);
		assertEquals(91, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(6);
		hp = new Hyperplane(5, partitions);
		assertEquals(210, hp.getReferencePoints().size());
		for (ReferencePoint rp : hp.getReferencePoints()) {
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		partitions.clear();
		partitions.add(3);
		partitions.add(2);
		hp = new Hyperplane(8, partitions);
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
		hp = new Hyperplane(10, partitions);
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
		hp = new Hyperplane(15, partitions);
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
