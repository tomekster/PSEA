package utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import utils.Geometry.Line2D;

public class GeometryTest {

	private double EPS = Geometry.EPS;

	@Test
	public void testEuclideanDistance() {

		double P1[] = { 1, 1, 1 };
		double P2[] = { 0, 0, 0 };
		assertEquals(Math.pow(3, 0.5), Geometry.euclideanDistance(P1, P2), EPS);

		double P3[] = { 0, 0, 0, 0 };
		double P4[] = { 1, 2, 3, 4 };
		assertEquals(Math.pow(1 + 4 + 9 + 16, 0.5), Geometry.euclideanDistance(P3, P4), EPS);
	}
	
	@Test
	public void testPointLineDist() {
		double P1[] = { 1, 1, 1 };
		double P2[] = { 2, 2, 2 };
		assertEquals(0, Geometry.pointLineDist(P1, P2), EPS);
		
		double P3[] = { 0, 1, 1 };
		double P4[] = { 0, 0, 1 };
		assertEquals(1, Geometry.pointLineDist(P3, P4), EPS);
		
		double P5[] = { 1, 1, 1 };
		double P6[] = { 0, 0, 1 };
		assertEquals(Math.sqrt(2), Geometry.pointLineDist(P5, P6), EPS);
		
		double P7[] = { 0, 0, 1 };
		double P8[] = { 0, 1, 1 };
		assertEquals(Math.sqrt(2)/2, Geometry.pointLineDist(P7, P8), EPS);
		
		double P9[] = { 0, 0, 1 };
		double P10[] = { 1, 1, 1 };
		assertEquals(Math.sqrt(2.0/3), Geometry.pointLineDist(P9, P10), EPS);
	}
	
	//Wikipedia example http://wcipeg.com/wiki/Convex_hull_trick
	@Test
	public void linesSetUpperEnvelope(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(0,4));
		lines.add(new Line2D(2.0/3,4.0/3));
		lines.add(new Line2D(-3,12));
		lines.add(new Line2D(-0.5,3));
		
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		assertEquals(3, res.size());
		assertEquals(-3, res.get(0).a, EPS);
		assertEquals(12, res.get(0).b, EPS);
		assertEquals(0, res.get(1).a, EPS);
		assertEquals(4, res.get(1).b, EPS);
		assertEquals(2.0/3, res.get(2).a, EPS);
		assertEquals(4.0/3, res.get(2).b, EPS);	
	}
	
	//Wikipedia example http://wcipeg.com/wiki/Convex_hull_trick
	//Extended with duplicated lines and lines with same slope value
	@Test
	public void linesSetUpperEnvelope2(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(0,4));
		lines.add(new Line2D(2.0/3,4.0/3));
		lines.add(new Line2D(-3,12));
		lines.add(new Line2D(-0.5,3));
		lines.add(new Line2D(0,4));
		lines.add(new Line2D(0,3));
		lines.add(new Line2D(-3,13));
		lines.add(new Line2D(-2.0/3, 1));
		
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		assertEquals(3, res.size());
		assertEquals(-3, res.get(0).a, EPS);
		assertEquals(13, res.get(0).b, EPS);
		assertEquals(0, res.get(1).a, EPS);
		assertEquals(4, res.get(1).b, EPS);
		assertEquals(2.0/3, res.get(2).a, EPS);
		assertEquals(4.0/3, res.get(2).b, EPS);	
	}
	
	//Wikipedia example http://wcipeg.com/wiki/Convex_hull_trick
		//Extended with duplicated lines and lines with same slope value
	@Test
	public void linesSetUpperEnvelope3(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-1,-10));
		lines.add(new Line2D(0,-6));
		lines.add(new Line2D(-3,-33));
		lines.add(new Line2D(-4,-40));
		lines.add(new Line2D(-4,-24));
		lines.add(new Line2D(1,-6));
		lines.add(new Line2D(1,-8));
			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		assertEquals(4, res.size());
		assertEquals(-4, res.get(0).a, EPS);
		assertEquals(-24, res.get(0).b, EPS);
		assertEquals(-1, res.get(1).a, EPS);
		assertEquals(-10, res.get(1).b, EPS);
		assertEquals(0, res.get(2).a, EPS);
		assertEquals(-6, res.get(2).b, EPS);
		assertEquals(1, res.get(3).a, EPS);
		assertEquals(-6, res.get(3).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope4(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-0.11,0.11));
		lines.add(new Line2D(-0.026,0.026));
		lines.add(new Line2D(0,0.07));
		lines.add(new Line2D(0,0.088));
		lines.add(new Line2D(0.063,0));
		lines.add(new Line2D(0.179,0));
			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		assertEquals(3, res.size());
		assertEquals(-0.11, res.get(0).a, EPS);
		assertEquals(0.11, res.get(0).b, EPS);
		assertEquals(0, res.get(1).a, EPS);
		assertEquals(0.088, res.get(1).b, EPS);
		assertEquals(0.179, res.get(2).a, EPS);
		assertEquals(0, res.get(2).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope5(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-0.027,0.027));
		lines.add(new Line2D(-0.005,0.005));
		lines.add(new Line2D(-0.005,0.134));
		lines.add(new Line2D(0,0));
		lines.add(new Line2D(0,0));
		lines.add(new Line2D(0.053,0));
			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
//		System.out.println("RES:");
//		for(Line2D L : res){
//			System.out.println(L.a + "  " + L.b);
//		}
		
		assertEquals(3, res.size());
		assertEquals(-0.027, res.get(0).a, EPS);
		assertEquals(0.027, res.get(0).b, EPS);
		assertEquals(-0.005, res.get(1).a, EPS);
		assertEquals(0.134, res.get(1).b, EPS);
		assertEquals(0.053, res.get(2).a, EPS);
		assertEquals(0.0, res.get(2).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope6(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-0.1779,0.1779));
		lines.add(new Line2D(0,0));
		lines.add(new Line2D(0,0.0238));
		lines.add(new Line2D(0,0.3033));
		lines.add(new Line2D(0.0259,0));
		lines.add(new Line2D(0.1292,0));
			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		
		assertEquals(3, res.size());
		assertEquals(-0.1779, res.get(0).a, EPS);
		assertEquals(0.1779, res.get(0).b, EPS);
		assertEquals(0, res.get(1).a, EPS);
		assertEquals(0.3033, res.get(1).b, EPS);
		assertEquals(0.1292, res.get(2).a, EPS);
		assertEquals(0.0, res.get(2).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope7(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-0.3352,0.3352));
		lines.add(new Line2D(0,0));
		lines.add(new Line2D(0.0174,0));
		lines.add(new Line2D(0,0));
		lines.add(new Line2D(0.1133,0));
		lines.add(new Line2D(0.1133,0.0283));
			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		
		assertEquals(2, res.size());
		assertEquals(-0.3352, res.get(0).a, EPS);
		assertEquals(0.3352, res.get(0).b, EPS);
		assertEquals(0.1133, res.get(1).a, EPS);
		assertEquals(0.0283, res.get(1).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope8(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-0.09,	0.09));
		lines.add(new Line2D(-0.04,0.04));
		lines.add(new Line2D(-0.04,0.12));
		lines.add(new Line2D(-0.01,0.03));
		lines.add(new Line2D(0.02,0));
		lines.add(new Line2D(0.25,0));
			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		System.out.println(res.size());
		assertEquals(-0.09, res.get(0).a, EPS);
		assertEquals(0.09, res.get(0).b, EPS);
		assertEquals(-0.04, res.get(1).a, EPS);
		assertEquals(0.12, res.get(1).b, EPS);
		assertEquals(0.25, res.get(2).a, EPS);
		assertEquals(0, res.get(2).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope9(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(0.09936233736019646, 0));
		lines.add(new Line2D(0.001352729625262398, 0.19317442946120894));
		lines.add(new Line2D(0.0013526954488228974,0));
		lines.add(new Line2D(1.6889104250885733E-5, 0.002411822005556264));
		lines.add(new Line2D(-0.05828312356748756, 0.05828312356748756));
		lines.add(new Line2D(-0.15088285990248482,	0.15088285990248482));
		
//		(-0.15088285990248482, 0.15088285990248482)
//		(-0.05828312356748756, 0.05828312356748756)
//		(1.6889104250885733E-5, 0.002411822005556264)
//		(0.0013526954488228974, 0.0)
//		(0.001352729625262398, 0.19317442946120894)
//		(0.09936233736019646, 0.0)
		
//		lines.add(new Line2D(-0.15088285990248482,	0.15088285990248482));
//		lines.add(new Line2D(-0.05828312356748756, 0.05828312356748756));
//		lines.add(new Line2D(1.6889104250885733E-5, 0.002411822005556264));
//		lines.add(new Line2D(0.0013526954488228974,0));
//		lines.add(new Line2D(0.001352729625262398, 0.19317442946120894));
//		lines.add(new Line2D(0.09936233736019646, 0));
		
//		(-0.15088285990248482, 0.15088285990248482)
//		(-0.05828312356748756, 0.05828312356748756)
//		(1.6889104250885733E-5, 0.002411822005556264)
//		(0.001352729625262398, 0.19317442946120894)
//		(0.0013526954488228974, 0.0)
//		(0.09936233736019646, 0.0)
		
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		System.out.println(res.size());
		assertEquals(3, res.size(), EPS);
		assertEquals(-0.15088285990248482, res.get(0).a, EPS);
		assertEquals(0.15088285990248482, res.get(0).b, EPS);
		assertEquals(0.001352729625262398, res.get(1).a, EPS);
		assertEquals(0.19317442946120894, res.get(1).b, EPS);
		assertEquals(0.09936233736019646, res.get(2).a, EPS);
		assertEquals(0, res.get(2).b, EPS);
	}
	
	@Test
	public void linesSetUpperEnvelope10(){
		ArrayList < Line2D > lines = new ArrayList<>();
		lines.add(new Line2D(-0.000972558490649, 0.000972558490649));
		lines.add(new Line2D(-0.000972004887446, 0.0367188820992));
		lines.add(new Line2D(-0.00052010663848, 0.0310018634846));
		lines.add(new Line2D(-0.000202241430348, 0.00304589266492));
		lines.add(new Line2D(-7.02776113731e-05, 7.02776113731e-05));
		lines.add(new Line2D(-2.273073312e-05, 0.00135490500016));
		lines.add(new Line2D(-1.72930014339e-05, 0.000260444292404));
		lines.add(new Line2D(-1.40221592519e-06, 5.29707225752e-05));
		lines.add(new Line2D(2.03313014521e-06, 3.66289882153e-06));
		lines.add(new Line2D(0.000215476676118, 0.0));
		lines.add(new Line2D(0.000237879839304, 0.0011251418137));
		lines.add(new Line2D(0.000271776677981, 0.000489634407243));
		lines.add(new Line2D(0.000659694581048, 0.000773225601901));
		lines.add(new Line2D(0.00582575501936, 0.0275550907879));
		lines.add(new Line2D(0.00823920726442, 0.0));
		lines.add(new Line2D(0.0200343294063, 0.0234821641088));

			
		ArrayList <Line2D> res = Geometry.linesUpperEnvelope(lines);
		assertEquals(3, res.size());
		assertEquals(-0.000972558490649, res.get(0).a, EPS);
		assertEquals(0.000972558490649, res.get(0).b, EPS);
		assertEquals(-0.000972004887446, res.get(1).a, EPS);
		assertEquals(0.0367188820992, res.get(1).b, EPS);
		assertEquals(0.0200343294063, res.get(2).a, EPS);
		assertEquals(0.0234821641088, res.get(2).b, EPS);
	}
	
	@Test
	public void testGetSimplexSegment(){
		double dim[] = {1.0/3, 1.0/3, 1.0/3};
		double grad[] = {1.0/6, -1.0/3, 1.0/6};
		
		Pair<double[], double[]> res = Geometry.getSimplexSegment(dim, grad);
		double p1[] = res.first;
		double p2[] = res.second;

		double res1[] = {0.5, 0, 0.5};
		double res2[] = {0, 1, 0};
		assertArrayEquals(res1, p1, 1e-10);
		assertArrayEquals(res2, p2, 1e-10);
	}

	@Test
	public void normalizeTest(){
		double a[] = {-0.0004085602, 2.0008171204173157};
		a = Geometry.normalize(a);
		assertEquals(-0.00020423837, a[0], Geometry.EPS);
		assertEquals(1.00020423838, a[1], Geometry.EPS);
	}
	
	@Test
	public void getVectTest(){
		double A[] = {1,-2,3};
		double B[] = {-1, -0.5, 10};
		double C[] = {-2, 1.5, 7};
		
		assertArrayEquals(C, Geometry.getVect(A, B), 1e-6);
	}
	
	@Test
	public void randomPointOnSphereTest(){
		double p[]; 
		
		p = Geometry.randomPointOnSphere(3, 2);
		assertEquals(3, p.length, 1e-6);		
		assertEquals(2, Geometry.getLen(p), 1e-6);
		
		p = Geometry.randomPointOnSphere(4, 0.17);
		assertEquals(4, p.length, 1e-6);		
		assertEquals(0.17, Geometry.getLen(p), 1e-6);
	}
	
	@Test
	public void getRandomVectorSummingTo1(){
		double p[];
		
		p = Geometry.getRandomVectorSummingTo1(2);
		assertEquals(2, p.length, 1e-6);
		assertEquals(1, Arrays.stream(p).sum(), 1e-6);
		p = Geometry.getRandomVectorSummingTo1(3);
		assertEquals(3, p.length, 1e-6);
		assertEquals(1, Arrays.stream(p).sum(), 1e-6);
		p = Geometry.getRandomVectorSummingTo1(4);
		assertEquals(4, p.length, 1e-6);
		assertEquals(1, Arrays.stream(p).sum(), 1e-6);
	}
	
	
	@Test
	public void getRandomVectorOnHyperplaneTest(){
		double p[];
		
		p = Geometry.getRandomVectorOnHyperplane(2, 0.3);
		assertEquals(2, p.length, 1e-6);		
		assertEquals(0, Arrays.stream(p).sum(), 1e-6);
		assertEquals(0.3, Geometry.getLen(p), 1e-6);
	}
}
