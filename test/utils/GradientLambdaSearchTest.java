package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.stream.DoubleStream;

import org.junit.Test;

import core.points.ReferencePoint;
import core.points.Solution;
import preferences.PreferenceCollector;
import solutionRankers.ChebyshevRanker;
public class GradientLambdaSearchTest {
	
	@Test
	public void lambda2thetaTest(){
		double lambda[] = {1.0/3,1.0/3,1.0/3};
		GradientLambdaSearch gls = new GradientLambdaSearch(3);
		double theta[] = gls.lambda2theta(lambda);
		assertEquals(0, theta[2], 1e-10);  
	}
	
	@Test
	public void getTotalPCGradientTest() {
			PreferenceCollector PC = PreferenceCollector.getInstance();
			double var[] = {0};
			double obj1[] = {2,1,2};
			double obj2[] = {1,2,1};
			Solution s1 = new Solution(var, obj1);
			Solution s2 = new Solution(var, obj2);
			PC.addComparison(s1, s2);
			double lambda[] = {1.0/2, 1.0/6, 1.0/3};
			ChebyshevRanker ranker = new ChebyshevRanker(lambda);
			assertTrue(ranker.compareSolutions(s1, s2) > 0);
			
			GradientLambdaSearch gls = new GradientLambdaSearch(3);
			double smoothGrad[] = gls.smoothMaxGrad(s1.getObjectives(), lambda);
			assertEquals(0, smoothGrad[2], 1e-10);

			double grad[] = gls.getTotalPCGradient(new ReferencePoint(lambda));
			assertEquals(0, DoubleStream.of(grad).sum(), 1e-10);
	}
	
	@Test
	public void getComparisonSwitchPoints(){
		PreferenceCollector PC = PreferenceCollector.getInstance();
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
 		ArrayList < Pair<Double, Integer> > switchPoints = gls.getComparisonSwitchPoints(lambda1, lambda2) ;
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
	public void getComparisonSwitchPoints2(){
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
 		ArrayList < Pair<Double, Integer> > switchPoints = gls.getComparisonSwitchPoints(lambda1, lambda2) ;
 		assertEquals(0, switchPoints.get(0).first, Geometry.EPS);
 		assertEquals(0, switchPoints.get(1).first, Geometry.EPS);
 		assertEquals(0, switchPoints.get(2).first, Geometry.EPS);
 		assertEquals(0.40875895810163, switchPoints.get(3).first, Geometry.EPS);
 		assertEquals(0.746897684117466, switchPoints.get(4).first, Geometry.EPS);
 		assertEquals(-1, (long) switchPoints.get(3).second);
 		assertEquals(3, (long) switchPoints.get(4).second);
 		double bestTime = gls.findBestTime(switchPoints);
 		assertEquals(0.204379479050815, bestTime, Geometry.EPS);
	}
	
	@Test
	public void getComparisonSwitchPoints3(){
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
 		ArrayList < Pair<Double, Integer> > switchPoints = gls.getComparisonSwitchPoints(lambda1, lambda2) ;
 		assertEquals(0.586813707588713, switchPoints.get(2).first, Geometry.EPS);
 		assertEquals(2, (long) switchPoints.get(2).second);
 		double bestTime = gls.findBestTime(switchPoints);
 		assertEquals((1 + 0.586813707588713)/ 2, bestTime, Geometry.EPS);
	}
}