package experiment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import algorithm.evolutionary.solutions.Population;
import algorithm.evolutionary.solutions.Solution;
import algorithm.implementations.nsgaiii.hyperplane.ReferencePoint;

public class PythonVisualizer {
	
	
	public static void saveResults(int numObjectives, ArrayList <ArrayList <double[]>> dataPoints, String filename) {
		visualize(numObjectives, dataPoints, filename, "plot.py");
	}
	public static void colorMap(int numObjectives, ArrayList <ArrayList <double[]>> dataPoints, String filename) {
		visualize(numObjectives, dataPoints, filename, "colorMap.py");
	}
	
	public static void visualize(int numObjectives, ArrayList <ArrayList <double[]>> dataPoints, String filename, String scriptName) {
		writeVisualizatoinData(numObjectives, dataPoints, filename);
		if(numObjectives > 3) return;
		
		String cmd[] = new String[3];
		cmd[0] = "python";
		cmd[1] = scriptName;
		cmd[2] = filename;
		
		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//		String line = "";
//		try {
//			while((line = bfr.readLine()) != null) {
//				// display each output line form python script
//				System.out.println("P->" + line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private static void writeVisualizatoinData(int numObjectives, ArrayList<ArrayList<double[]>> dataPoints, String filename) {
		try{
		    PrintWriter writer = new PrintWriter(filename, "UTF-8");
		    writer.println(numObjectives);
		    for(int i=0; i<dataPoints.size(); i++){
				for(double[] point : dataPoints.get(i)){
					for(double d : point){
						writer.print(" " + d);
					}
					writer.println(" " + i);
				}				
			}
		    writer.close();
		} catch (IOException e) {
		   // do something
		}
	}

	public static ArrayList<double[]> convert(Population <? extends Solution> pop) {
		ArrayList <double[]> points = new ArrayList<>();
		for(Solution s : pop.getSolutions()){
			points.add(s.getObjectives());
		}
		return points;
	}
	
	public static ArrayList<double[]> convert(ArrayList <ReferencePoint> referencePoints) {
		ArrayList <double[]> points = new ArrayList<>();
		for(ReferencePoint rp : referencePoints){
			points.add(rp.getDim());
		}
		return points;
	}
	
	public static ArrayList<double[]> convert(Double[] array) {
		ArrayList <double[]> points = new ArrayList<>();
		for(double d : array){
			double tmp[] = {d};
			points.add(tmp);
		}
		return points;
	}
}