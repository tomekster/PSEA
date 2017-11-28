package experiment;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PythonVisualizer {
		
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
}