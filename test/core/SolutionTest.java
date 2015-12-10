package core;

import static org.junit.Assert.*;

import org.junit.Test;

public class SolutionTest {

	@Test
	public void sameAsTest(){
		double[] var1 = {1,2,3,4};
		double[] var3 = {5,6,7,8};
		double[] obj = {0};
		Solution s1 = new Solution(var1,obj);
		Solution s2 = new Solution(var1,obj);
		assertTrue(s1.sameAs(s2));
		
		Solution s3 = new Solution(var3,obj);
		assertFalse(s1.sameAs(s3));
		
	}
}
