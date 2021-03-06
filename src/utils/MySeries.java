package utils;

import java.util.ArrayList;

import core.points.ReferencePoint;
import core.points.Solution;
import preferences.Comparison;

public class MySeries {
	private ArrayList <Point> solutionSeries;
	private ArrayList <Point> referencePointSeries;
	private ArrayList <Pair<Point, Point>> comparisonSeries;
	
	public MySeries(ArrayList<Solution> s, ArrayList<Comparison> c){
		setSolutionSeries(s);
		setComparisonSeries(c);
	}
	
	public void setReferencePointSeries(ArrayList<ReferencePoint> refPoints){
		referencePointSeries = new ArrayList <Point>();
		for(ReferencePoint rp : refPoints){
			referencePointSeries.add((new Point(rp.getDim())));
		}
	}
	
	public void setSolutionSeries(ArrayList <Solution> solutions){
		solutionSeries= new ArrayList <Point>();
		for(Solution s : solutions){
			solutionSeries.add((new Point(s.getObjectives())));
		}
	}
	
	public void setComparisonSeries(ArrayList <Comparison> comparisons){
		comparisonSeries = new ArrayList <Pair<Point, Point>>();
		for(Comparison c: comparisons){
			Point p1 = new Point(c.getBetter().getObjectives());
			Point p2 = new Point(c.getWorse().getObjectives());
			comparisonSeries.add(new Pair<Point, Point>(p1, p2));
		}
	}
	
	public ArrayList<Point> getSolutionSeries(){
		return solutionSeries;
	}
	public ArrayList<Point> getReferencePointSerise(){
		return referencePointSeries;
	}
	public ArrayList<Pair<Point,Point>> getComparisonSeries(){
		return comparisonSeries;
	}
}
