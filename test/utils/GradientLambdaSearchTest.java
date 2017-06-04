package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import core.Lambda;
import core.points.ReferencePoint;
import core.points.Solution;
import preferences.PreferenceCollector;
public class GradientLambdaSearchTest {
	
	@Test
	public void lambda2thetaTest(){
		double lambda[] = {1.0/3,1.0/3,1.0/3};
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
		double theta[] = gls.lambda2theta(lambda);
		assertEquals(0, theta[2], 1e-10);  
	}
	
	@Test
	public void getComparisonSwitchPointsTest(){
		PreferenceCollector PC = PreferenceCollector.getInstance();
		PC.clear();
		double var[] = {0};
		double obj1[] = {0.311397000904097, 0.003796942733664, 0.201330694436395};
		double obj2[] = {0.324203496628281,	0.108552933625122,	0.074641790760185};
		double obj3[] = {0.295496933135726, 0.101507621791594, 0.105364664352333};
		double obj4[] = {0.29747411474984, 0.107318797896483, 0.096794246310606};
		Solution s1 = new Solution(var, obj1);
		Solution s2 = new Solution(var, obj2);
		Solution s3 = new Solution(var, obj3);
		Solution s4 = new Solution(var, obj4);
		PC.addComparison(s1, s2);
		PC.addComparison(s3, s4);
		double lambda1[] = {0.239218159637306, 0.760781840362694, 0};
		double lambda2[] = {0, 0.484234692114499, 0.515765307885501};
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
 		ArrayList < Pair<Double, Integer> > switchPoints = gls.getAllSwitchPoints(lambda1, lambda2) ;
 		assertEquals(0, switchPoints.get(0).first, Geometry.EPS);
 		assertEquals(0, switchPoints.get(1).first, Geometry.EPS);
 		assertEquals(0.028277705388397, switchPoints.get(2).first, Geometry.EPS);
 		assertEquals(0.383045899950873, switchPoints.get(3).first, Geometry.EPS);
 		assertTrue(switchPoints.get(0).second < 0);
 		assertTrue(switchPoints.get(0).second < 0);
 		assertEquals(2, (long) switchPoints.get(2).second);
 		assertEquals(1, (long) switchPoints.get(3).second);
	}
	
	@Test
	public void getComparisonSwitchPointsTest2(){
		PreferenceCollector PC = PreferenceCollector.getInstance();
		PC.clear();
		double var[] = {0};
		double obj1[] = {0.311397000904097, 0.003796942733664, 0.201330694436395};
		double obj2[] = {0.324203496628281,	0.108552933625122,	0.074641790760185};
		double obj3[] = {0.295496933135726, 0.101507621791594, 0.105364664352333};
		double obj4[] = {0.29747411474984, 0.107318797896483, 0.096794246310606};
		double obj5[] = {0.183198368966633, 0.252631002492642, 0.065416599856092};
		double obj6[] = {0.257090050660066, 0.14720150289073, 0.096674361119274};
		Solution s1 = new Solution(var, obj1);
		Solution s2 = new Solution(var, obj2);
		Solution s3 = new Solution(var, obj3);
		Solution s4 = new Solution(var, obj4);
		Solution s5 = new Solution(var, obj5);
		Solution s6 = new Solution(var, obj6);
		PC.addComparison(s1, s2);
		PC.addComparison(s3, s4);
		PC.addComparison(s5, s6);
		double lambda1[] = {0.28075525615251, 0, 0.71924474384749};
		double lambda2[] = {0.077751763428363, 0.922248236571637, 0};
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
 		ArrayList < Pair<Double, Integer> > switchPoints = gls.getAllSwitchPoints(lambda1, lambda2) ;
 		assertEquals(0, switchPoints.get(0).first, Geometry.EPS);
 		assertEquals(0, switchPoints.get(1).first, Geometry.EPS);
 		assertEquals(0, switchPoints.get(2).first, Geometry.EPS);
 		assertEquals(0.40875895810163, switchPoints.get(3).first, Geometry.EPS);
 		assertEquals(0.746897684117466, switchPoints.get(4).first, Geometry.EPS);
 		assertEquals(-1, (long) switchPoints.get(3).second);
 		assertEquals(3, (long) switchPoints.get(4).second);
//		True only when picking middle point from interval
// 		double bestTime = gls.findBestTime(switchPoints);
// 		assertEquals(0.204379479050815, bestTime, Geometry.EPS); 
	}
	
	@Test
	public void getComparisonSwitchPointsTest3(){
		PreferenceCollector PC = PreferenceCollector.getInstance();
		PC.clear();
		double var[] = {0};
		double obj1[] = {0.215878289274576, 0.31054785953418, 2.92E-04};
		double obj2[] = {4.40E-04, 0.044368309241494, 0.498322098838182};
		double obj3[] = {4.49E-06, 0.127858699480907, 0.391545444032272};
		double obj4[] = {0.039889705869602, 0.070330060026638, 0.427444942560723};
		Solution s1 = new Solution(var, obj1);
		Solution s2 = new Solution(var, obj2);
		Solution s3 = new Solution(var, obj3);
		Solution s4 = new Solution(var, obj4);
		PC.addComparison(s1, s2);
		PC.addComparison(s3, s4);
		double lambda1[] = {0, 0.630406655829493, 0.369593344170507};
		double lambda2[] = {0.140510428179306, 0.859489571820694, 0};
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
 		ArrayList < Pair<Double, Integer> > switchPoints = gls.getAllSwitchPoints(lambda1, lambda2) ;
 		assertEquals(0.586813707588713, switchPoints.get(2).first, Geometry.EPS);
 		assertEquals(2, (long) switchPoints.get(2).second);
//		True only when picking middle point from interval
// 		double bestTime = gls.findBestTime(switchPoints);
// 		assertEquals((1 + 0.586813707588713)/ 2, bestTime, Geometry.EPS);
	}
	
//	@Test
//	public void getComparisonSwitchPointsTest4(){
//		double l1[] = {0.8907733, .0, 0.1092267};
//		double l2[] = {0.9280113, 0.0729887, .0};
//		
//		int cpId = 0;
//		
//		double vars[] = {0};
//		double obj1[] = {0.1451427, 0.3736858, .0};
//		double obj2[] = {.0, 0.0720721, 0.4872119};
//		Comparison cp = new Comparison(new Solution(vars, obj1), new Solution(vars, obj2), 0, 0);
//		
//		ArrayList <Line2D> upperEnvelope = new ArrayList<>();
//		ArrayList <Pair<Double, Integer>> res = new ArrayList<>();
//		upperEnvelope.add(new Line2D(-0.027, 0.027, true));
//		upperEnvelope.add(new Line2D(-0.005, 0.134, true));
//		upperEnvelope.add(new Line2D(0.053, 0.0, false));
//		for(int i=1; i<upperEnvelope.size(); i++){
//			Line2D line1 = upperEnvelope.get(i-1);
//			Line2D line2 = upperEnvelope.get(i);
//			if( line1.isBetter() ^ line2.isBetter() ){
//				double crossX = line1.crossX(line2);
//				if(crossX < 0 || crossX > 1) continue;
//				if(line2.isBetter()){
//					res.add(new Pair<Double, Integer>(crossX, -(cpId+1)));
//				}
//				else{
//					res.add(new Pair<Double, Integer>(crossX, cpId+1));
//				}
//				
//				double lambda[] = Geometry.linearCombination(l1, l2, crossX);
//				double M1 = ChebyshevRanker.eval(cp.getBetter(), null, lambda, 0);
//				double M2 = ChebyshevRanker.eval(cp.getWorse(), null, lambda, 0);
//				System.out.println(M1 + " " + M2);
//				if( ! ((M1-M2) < Geometry.EPS)){
//					 System.out.println("ERROR");
//				}
//			}
//		}
//	}
	
	
	@Test
	public void findBestIntervalsTest(){
		ArrayList<Pair<Double, Integer> > switches = new ArrayList<>();
		switches.add(new Pair<Double, Integer>(0.0, -1));
		switches.add(new Pair<Double, Integer>(0.5, 1));
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
		
		//RANDOM LAMBDAS
		double l1[] = {1,2,3};
		double l2[] = {1,2,3};
		ArrayList <Interval> intervals = gls.findBestIntervals(switches, l1, l2);
		assertEquals(1, intervals.size());
		assertEquals(0.5, intervals.get(0).getBeg(), 1e-6);
		assertEquals(1, intervals.get(0).getEnd(), 1e-6);
	}
	
	@Test
	public void findBestIntervalsTest2(){
		ArrayList<Pair<Double, Integer> > switches = new ArrayList<>();
		switches.add(new Pair<Double, Integer>(0.0, -2));
		switches.add(new Pair<Double, Integer>(0.5, -1));
		switches.add(new Pair<Double, Integer>(0.18878359688722182, 2));
		switches.add(new Pair<Double, Integer>(0.39582323899573246, 1));
		switches.add(new Pair<Double, Integer>(0.7392808269055011, -2));
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
		double l1[] = {0.24718284557443193, 0.7528171544255681, 0.0};
		double l2[] = {0.24718284557443193, 0.0, 0.7528171544255681};
		ArrayList <Interval> intervals = gls.findBestIntervals(switches, l1, l2);
		assertEquals(1, intervals.size());
		assertEquals(0.39582323899573246, intervals.get(0).getBeg(), 1e-6);
		assertEquals(0.7392808269055011, intervals.get(0).getEnd(), 1e-6);
		assertEquals(0, intervals.get(0).getCV());
		
		double mid = (intervals.get(0).getBeg() + intervals.get(0).getEnd()) / 2;
		double dim[] = Geometry.linearCombination(l1, l2, mid);
		ReferencePoint middle = new ReferencePoint(dim);
		int eval = Lambda.evaluateDirection(middle);
		assertEquals(0, eval);
		
		//Best interval (wrong) CV=1 , [0,0]
	}
}