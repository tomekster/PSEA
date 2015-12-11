package core.hyperplane;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import utils.MyComparator;

public class HyperplaneTest {
	
	double EPS = MyComparator.EPS;
	
	private double refPointDimSum(ReferencePoint rp){
		double sum = 0.0;
		for(double d : rp.getDimensions()){
			sum += d;
		}
		return sum;
	}
	
	@Test
	public void referencePointsGenerationTest(){
		Hyperplane hp;
		hp= new Hyperplane(3, 4);
		assertEquals(15, hp.getReferencePoints().size());
		for(ReferencePoint rp : hp.getReferencePoints()){
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
		
		hp = new Hyperplane(3, 12);
		assertEquals(91, hp.getReferencePoints().size());
		for(ReferencePoint rp : hp.getReferencePoints()){
			assertEquals(1.0, refPointDimSum(rp), EPS);
		}
	}
}
