package core;

import static org.junit.Assert.*;

import org.junit.Test;

import problems.DTLZ1;

public class NSGAIIITest {
	@Test
	public void nsgaIIIinitialization(){
		NSGAIII alg = new NSGAIII(new DTLZ1(7), 400);
		
		assertEquals("DTLZ1",alg.getProblem().getName());
		assertEquals(92, alg.getPopulationSize());
		
	}
}
