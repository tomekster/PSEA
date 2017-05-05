package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import core.Population;
import core.points.Solution;

public class WfgPythonVisualizer {

	public void visualise(Population referenceFront, Population firstFront) {
		String cmd[] = new String[5 + referenceFront.size() + firstFront.size()];
		cmd[0] = "python";
		cmd[1] = "plot.py";
		cmd[2] = "" + referenceFront.getSolution(0).getNumObjectives();
		cmd[3] = "" + referenceFront.size();
		cmd[4] = "" + firstFront.size();
		for(int i =0 ; i<referenceFront.size(); i++){
			Solution s = referenceFront.getSolution(i);
			String obj = "";
			for(double d : s.getObjectives()){
				obj = obj + d + "x";
				cmd[5 + i] = obj.substring(0,obj.length()-1);
			}
		}
		for(int i=0 ; i<firstFront.size(); i++){
			Solution s = firstFront.getSolution(i);
			String obj = "";
			for(double d : s.getObjectives()){
				obj = obj + d + "x";
				cmd[5 + referenceFront.size() + i] = obj.substring(0,obj.length()-1);
			}
		}
		for(String s : cmd){
			System.out.print(s + " ");
		}
		System.out.println("");
		Runtime rt = Runtime.getRuntime();
		Process pr = null;
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
}
