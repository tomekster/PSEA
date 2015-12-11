package core.hyperplane;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import core.NicheCountSelection;

public class NicheCountSelectionTest {
	
	@Test
	public void nicheCountInitTest(){
		NicheCountSelection ncs = new NicheCountSelection(3);
		assertEquals(91, ncs.getHyperplane().getReferencePoints().size());
		assertEquals(92, ncs.getPopulationSize());
	}
}
