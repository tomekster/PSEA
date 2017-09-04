package solutionRankers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import algorithm.psea.AsfPreferenceModel;
import algorithm.rankers.ConstraintViolationRanker;

public class LambdaCVRankerTest {
	@Test
	public void compareTest(){
		AsfPreferenceModel a = new AsfPreferenceModel(2), b = new AsfPreferenceModel(2);
		a.setNumViolations(2);
		b.setNumViolations(3);
		a.setPenalty(10);
		b.setPenalty(5);
		ConstraintViolationRanker lcvr = new ConstraintViolationRanker();
		assertEquals(-1, lcvr.compare(a, b));
		assertEquals(1, lcvr.compare(b, a));
		b.setNumViolations(2);
		assertEquals(1, lcvr.compare(a, b));
		assertEquals(-1, lcvr.compare(b, a));
	}
}
