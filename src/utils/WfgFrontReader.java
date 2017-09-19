package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import algorithm.geneticAlgorithm.Population;
import algorithm.geneticAlgorithm.solution.DoubleSolution;
import problems.Problem;

public class WfgFrontReader {

	private static final String DIR = "ReferenceParetoFronts";

	public static Population getFront(Problem problem) {
		String subdir = "";
		int numObj = problem.getNumObjectives(); 
		subdir = "WFG." + numObj + "D";
		Path path = Paths.get(DIR, subdir, problem.getName() + "." + numObj + "D.pf");
		Population pop = new Population();
		System.out.println(path.toString());
		try (Stream<String> lines = Files.lines(path)) {
			List<List<Double>> data = lines
				.map(l -> Arrays.asList(l.trim().split(" "))
					.stream()
					.map(x -> Double.valueOf(x))
					.collect(Collectors.toList())
				)
				.collect(Collectors.toList());
			for(List <Double> l : data ){
				double var[] = new double[0];
				double obj[] = new double[l.size()];
				for(int i=0; i<obj.length; i++){
					obj[i] = l.get(i);
				}
				
				pop.addSolution(new DoubleSolution(var, obj));
			}
		} catch (IOException ex) {

		}
		return pop;
	}

}
