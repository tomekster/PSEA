package experiment;

import java.io.IOException;

public class PythonVisualizer {

	public void visualise(String dataFilename ) {
		String cmd[] = new String[3];
		cmd[0] = "python";
		cmd[1] = "plot.py";
		cmd[2] = dataFilename;
		
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
}