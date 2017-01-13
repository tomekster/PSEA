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
}
