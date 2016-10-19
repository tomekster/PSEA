package core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import core.points.Solution;
import utils.Geometry;

public class NicheCountSelectionTest {
	
	@Test
	public void normalizeTest(){
		Population testPop = new Population();
		double var[][] = {{0},{0},{0},{0},{0},{0}};
		double obj[][] = {{8,10},{11,4},{9.5,7},{12.5,10},{14,6},{17,7}};
		for(int i=0; i<6; i++){
			testPop.addSolution(new Solution(var[i],obj[i]));
		}
		Population res = NicheCountSelection.normalize(2, testPop);
		assertEquals(res.size(), 6);
		assertEquals(0, res.getSolution(0).getObjective(0), Geometry.EPS);
		assertEquals(6, res.getSolution(0).getObjective(1), Geometry.EPS);
		assertEquals(3, res.getSolution(1).getObjective(0), Geometry.EPS);
		assertEquals(0, res.getSolution(1).getObjective(1), Geometry.EPS);
		assertEquals(1.5, res.getSolution(2).getObjective(0), Geometry.EPS);
		assertEquals(3, res.getSolution(2).getObjective(1), Geometry.EPS);
		assertEquals(4.5, res.getSolution(3).getObjective(0), Geometry.EPS);
		assertEquals(6, res.getSolution(3).getObjective(1), Geometry.EPS);
		assertEquals(6, res.getSolution(4).getObjective(0), Geometry.EPS);
		assertEquals(2, res.getSolution(4).getObjective(1), Geometry.EPS);
	}
	
//	@Test
//	public void associateTest(){
//		NicheCountSelection ncs = new NicheCountSelection(2);
//		assertEquals(3, ncs.getHyperplane().getReferencePoints().size());
//		for(ReferencePoint rp : ncs.getHyperplane().getReferencePoints()){
//			System.out.println(rp.toString());
//		}
//
//		Population testPop = new Population();
//		double var[][] = {{0},{0},{0},{0},{0},{0}};
//		double obj[][] = {{8,10},{11,4},{9.5,7},{12.5,10},{14,6},{17,7}};
//		for(int i=0; i<6; i++){
//			testPop.addSolution(new Solution(var[i],obj[i]));
//		}
//		
//		Population normalizedPop = ncs.normalize(testPop);
//		
//		ncs.associate(normalizedPop);
//		
//		for(ReferencePoint rp : ncs.getHyperplane().getReferencePoints()){
//			System.out.println();
//			System.out.println(rp.toString());
//		}
//	}
}
