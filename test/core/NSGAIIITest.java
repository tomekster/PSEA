package core;

import static org.junit.Assert.*;

import org.junit.Test;

import problems.dtlz.DTLZ1;

public class NSGAIIITest {
	@Test
	public void nsgaIIIinitialization(){
		NSGAIII alg = new NSGAIII(new DTLZ1(3), 400, false, 50);
		
		assertEquals("DTLZ1",alg.getProblem().getName());
		assertEquals(92, alg.getPopulationSize());
	}
}
