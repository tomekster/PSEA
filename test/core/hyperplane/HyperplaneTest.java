package core.hyperplane;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import utils.Comparator;

public class HyperplaneTest {
	
	double EPS = Comparator.EPS;
	
	private double refPointDimSum(ReferencePoint rp){
		double sum = 0;
		for(int i = 0; i<rp.getNumDimensions(); i++){
			sum += rp.getDim(i);
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
