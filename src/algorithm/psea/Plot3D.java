package algorithm.psea;

import java.util.ArrayList;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import algorithm.geneticAlgorithm.Solution;
import algorithm.nsgaiii.ReferencePoint;
import algorithm.psea.preferences.Comparison;

public class Plot3D extends AbstractAnalysis{
	private Scatter solutionScatter;
	private Scatter referencePointScatter;
	private Scatter comparisonBetterScatter;
	private Scatter comparisonWorseScatter;
	
	public Plot3D(ArrayList<Solution> s, ArrayList<ReferencePoint> rp, ArrayList<Comparison>comp){
		Coord3d[] solutions = new Coord3d[s.size()];
		Coord3d[] referencePoints = new Coord3d[rp.size()];
		Coord3d[] better = new Coord3d[comp.size()];
		Coord3d[] worse = new Coord3d[comp.size()];
		
		for(int i=0; i<solutions.length; i++){
			double dim[] = s.get(i).getObjectives();
			solutions[i] = new Coord3d(dim[0], dim[1], dim[2]);
		}
		
		for(int i=0; i<referencePoints.length; i++){
			double dim[] = rp.get(i).getDim();
			referencePoints[i] = new Coord3d(dim[0], dim[1], dim[2]);
		}
		
		for(int i=0; i<better.length; i++){
			double dim1[] = comp.get(i).getBetter().getObjectives();
			double dim2[] = comp.get(i).getWorse().getObjectives();
			better[i] = new Coord3d(dim1[0], dim1[1], dim1[2]);
			worse[i] = new Coord3d(dim2[0], dim2[1], dim2[2]);
		}
		solutionScatter = new Scatter(solutions,Color.BLUE);
		referencePointScatter = new Scatter(referencePoints,Color.RED);
		comparisonBetterScatter = new Scatter(better,Color.GREEN);
		comparisonWorseScatter = new Scatter(worse, Color.YELLOW);
	}
		
	@Override
    public void init(){
        chart = AWTChartComponentFactory.chart(Quality.Advanced, "newt");
        solutionScatter.setWidth(2);
        chart.getScene().add(solutionScatter);
        chart.getScene().add(referencePointScatter);
        chart.getScene().add(comparisonBetterScatter);
        chart.getScene().add(comparisonWorseScatter);
    }

	public void update() {
		chart.getScene().remove(solutionScatter);
		chart.getScene().remove(referencePointScatter);
		chart.getScene().remove(comparisonBetterScatter);
		chart.getScene().remove(comparisonWorseScatter);
	}
}