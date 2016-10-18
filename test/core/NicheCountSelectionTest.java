package core;

import static org.junit.Assert.*;

import org.junit.Test;

import core.points.ReferencePoint;
import core.points.Solution;

public class NicheCountSelectionTest {
	
	@Test
	public void nicheCountInitTest(){
		NicheCountSelection ncs = new NicheCountSelection(3);
		assertEquals(91, ncs.getHyperplane().getReferencePoints().size());
		assertEquals(92, ncs.getPopulationSize());
	}
	
	@Test
	public void normalizeTest(){
		NicheCountSelection ncs = new NicheCountSelection(2);
		Population testPop = new Population();
		double var[][] = {{0},{0},{0},{0},{0},{0}};
		double obj[][] = {{8,10},{11,4},{9.5,7},{12.5,10},{14,6},{17,7}};
		for(int i=0; i<6; i++){
			testPop.addSolution(new Solution(var[i],obj[i]));
		}
		Population res = ncs.normalize(testPop);
		assertEquals(res.size(), 6);
	}
	
	@Test
	public void associateTest(){
		NicheCountSelection ncs = new NicheCountSelection(2);
		assertEquals(3, ncs.getHyperplane().getReferencePoints().size());
		for(ReferencePoint rp : ncs.getHyperplane().getReferencePoints()){
			System.out.println(rp.toString());
		}

		Population testPop = new Population();
		double var[][] = {{0},{0},{0},{0},{0},{0}};
		double obj[][] = {{8,10},{11,4},{9.5,7},{12.5,10},{14,6},{17,7}};
		for(int i=0; i<6; i++){
			testPop.addSolution(new Solution(var[i],obj[i]));
		}
		
		Population normalizedPop = ncs.normalize(testPop);
		
		ncs.associate(normalizedPop);
		
		for(ReferencePoint rp : ncs.getHyperplane().getReferencePoints()){
			System.out.println();
			System.out.println(rp.toString());
		}
	}
}
