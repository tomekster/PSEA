package core.hyperplane;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReferencePointTest {

	double EPS = utils.MyComparator.EPS;
	
	@Test
	public void copyConstructorTest(){
		int numDimensions = 7;
		
		ReferencePoint rp1, rp2;
		rp1 = new ReferencePoint(numDimensions);
		double val = 0.3;
		for(int i=0; i<numDimensions; i++){
			rp1.setDim(i, val);
			val *= 3;
		}
		
		rp2 = new ReferencePoint(rp1);
		
		assertEquals(rp1.getNumDimensions(), rp2.getNumDimensions());
		for(int i=0; i < rp1.getNumDimensions(); i++){
			assertEquals(rp1.getDim(i), rp2.getDim(i), EPS);
		}
		assertNotEquals(rp1, rp2);
	}
	
	@Test
	public void refPointInitializationTest(){
		ReferencePoint rp = new ReferencePoint(5);
		assertEquals(5, rp.getNumDimensions());
		double array[] = {0,0,0,0,0};
		assertArrayEquals(array, rp.getDimensions(),1E-9);
		assertTrue(rp.getAssociatedSolutionsQueue().isEmpty());
	}
	
	@Test
	public void associationsHeapTest(){
		//TODO
	}
}
