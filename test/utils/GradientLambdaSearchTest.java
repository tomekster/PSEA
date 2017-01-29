package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.stream.DoubleStream;

import org.junit.Test;

import com.jogamp.opengl.GLSharedContextSetter;

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
	
//	@Test
//	public void getTotalPCGradientTest(){
//		Lambda L = new Lambda(2, 1);
//		double vars[] = {0};
//		double obj1[] = {1,2};
//		double obj2[] = {2,1};
//		PreferenceCollector.getInstance().addComparison(new Solution(vars, obj1), new Solution(vars, obj2));
//		double rp[] = {0.5, 0.5};
//		ReferencePoint lambda = new ReferencePoint(rp);
//		double grad[] = getTotalPCGradient(lambda);
//		assertEquals(2.00122568062, grad[0], Geometry.EPS);
//		assertEquals(-2.00122568062, grad[1], Geometry.EPS);
//	}
}