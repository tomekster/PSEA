package core.points;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import algorithm.geneticAlgorithm.Solution;
import utils.math.Geometry;

public class SolutionTest {
	
	@Test
	public void cloneTest(){
		double var[] = {1,2,3};
		double obj[] = {4,5,6};
		Solution s1 = new Solution(var, obj);
		Solution s2 = s1.copy();
		assert(Arrays.equals(s2.getVariables(),s1.getVariables()));
		assert(Arrays.equals(s2.getObjectives(),s1.getObjectives()));
		s1.setObjective(0, 0);
		assertEquals(4, obj[0], Geometry.EPS);
		assertEquals(4, s2.getObjective(0), Geometry.EPS);
	}
}
