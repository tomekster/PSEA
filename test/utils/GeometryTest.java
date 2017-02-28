package utils;

import static org.junit.Assert.*;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;

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
		
		ArrayList <Line2D> res = Geometry.linesSetUpperEnvelope(lines);
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
		
		ArrayList <Line2D> res = Geometry.linesSetUpperEnvelope(lines);
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
			
		ArrayList <Line2D> res = Geometry.linesSetUpperEnvelope(lines);
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
			
		ArrayList <Line2D> res = Geometry.linesSetUpperEnvelope(lines);
		assertEquals(3, res.size());
		assertEquals(-0.11, res.get(0).a, EPS);
		assertEquals(0.11, res.get(0).b, EPS);
		assertEquals(0, res.get(1).a, EPS);
		assertEquals(0.088, res.get(1).b, EPS);
		assertEquals(0.179, res.get(2).a, EPS);
		assertEquals(0, res.get(2).b, EPS);
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
}
