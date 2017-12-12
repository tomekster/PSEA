package algorithm.evolutionary.artificialDM;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.comparison.Comparison;
import algorithm.evolutionary.solutions.Solution;
import utils.math.AsfFunction;
import utils.math.structures.Point;

public class AsfDMTest {

	@Test
	public void verifyModelTest(){
		ArrayList<Comparison> comparisons = new ArrayList<>();
		double obj1[] = {1};
		double obj2[] = {2};
		Solution s1 = new Solution(obj1);
		Solution s2 = new Solution(obj2);
		
		
		
		double lambda[] = {1};
		double rho = 0.0001;
		Point refPoint = new Point(1);
		AsfDm dm = new AsfDm(new AsfFunction(lambda, rho, refPoint));
		
		assertEquals(0, dm.verifyModel(comparisons));
		
		comparisons.add( new Comparison(s1, s2, 0));
		
		assertEquals(0, dm.verifyModel(comparisons));
		
		comparisons.add( new Comparison(s2, s1, 0));
	
		assertEquals(1, dm.verifyModel(comparisons));
		
		comparisons.add( new Comparison(s1, s1, 0));
		
		assertEquals(2, dm.verifyModel(comparisons));
	}
}
