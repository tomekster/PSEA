package experiment;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.Solution;
import utils.math.Geometry;

public class PythonVisualizer {

	static final String DATA_FILE_NAME = "tmpVizData.txt";
	
	public static void visualise(int numObjectives, ArrayList <ArrayList <double[]>> dataPoints) {
		
		writeVisualizatoinData(numObjectives, dataPoints);
		String cmd[] = new String[3];
		cmd[0] = "python";
		cmd[1] = "plot.py";
		cmd[2] = DATA_FILE_NAME;
		
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
	
	private static void writeVisualizatoinData(int numObjectives, ArrayList<ArrayList<double[]>> dataPoints) {
		try{
		    PrintWriter writer = new PrintWriter(DATA_FILE_NAME, "UTF-8");
		    writer.println(numObjectives);
		    for(int i=0; i<dataPoints.size(); i++){
				for(double[] point : dataPoints.get(i)){
					if( Arrays.stream(point).sum() < 0.5 - Geometry.EPS){
						System.out.println("ERR");
					}
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

	public static ArrayList<double[]> convert(Population pop) {
		ArrayList <double[]> points = new ArrayList<>();
		for(Solution s : pop.getSolutions()){
			points.add(s.getObjectives());
		}
		return points;
	}
}