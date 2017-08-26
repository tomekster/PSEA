package solutionRankers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import core.points.Lambda;

public class LambdaCVRankerTest {
	@Test
	public void compareTest(){
		Lambda a = new Lambda(2), b = new Lambda(2);
		a.setNumViolations(2);
		b.setNumViolations(3);
		a.setPenalty(10);
		b.setPenalty(5);
		LambdaCVRanker lcvr = new LambdaCVRanker();
		assertEquals(-1, lcvr.compare(a, b));
		assertEquals(1, lcvr.compare(b, a));
		b.setNumViolations(2);
		assertEquals(1, lcvr.compare(a, b));
		assertEquals(-1, lcvr.compare(b, a));
	}
}
