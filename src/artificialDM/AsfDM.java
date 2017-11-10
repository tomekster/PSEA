package artificialDM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import algorithm.geneticAlgorithm.solutions.Solution;
import utils.math.Geometry;
import utils.math.structures.Line;
import utils.math.structures.Point;
import utils.math.structures.Vector;

public class AsfDM extends ArtificialDM{
	
	private final static double DEFAULT_RHO = 0.0001;
	private static final long serialVersionUID = -6759292394593558688L;
	//TODO - add tests for Chebyshev function
	private double lambda[];
	private Vector vector;
	private static double rho;
	private Point refPoint;
	private double reward;
	private double penalty;
	
	public AsfDM(double refPoint[], double lambda[], String name){
		if(refPoint.length != lambda.length){
			throw new RuntimeException();
		}
		this.refPoint = new Point(refPoint);
		this.vector = new Vector(Geometry.invert(lambda));
		this.lambda = lambda;
		AsfDM.rho = DEFAULT_RHO;
		this.name = name;
	}
	
	public AsfDM(double refPoint[], double lambda[]){
		this(refPoint,lambda, "Anonymous");
	}
	
	public double eval(Solution s){
		return eval(s.getObjectives());
	}
	public double eval(double obj[]){
		return classicEval(obj, this.refPoint, this.lambda);
//		return sternalEval(s, refPoint, lambda);
//		return slowinskiEval(s, refPoint, lambda);
//		return lpEval(s, refPoint, lambda, 4);
	}
	
	public double classicEval(double obj[], Point refPoint, double lambda[]){
		double res = -Double.MAX_VALUE;
		double sum = 0;
		for(int i=0; i<obj.length; i++){
			double mult = lambda[i] * (obj[i] - refPoint.getDim(i));
			res = Double.max(mult, res);
			sum += mult;
		}
		res += sum * rho;
		return res;
	}
	
	public double sternalEval(Solution s, double refPoint[], double lambda[]){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = -Double.MAX_VALUE;
		double sum = 0;
		for(int i=0; i<s.getNumObjectives(); i++){
			double mult = lambda[i] * (s.getObjective(i) - refPoint[i]);
			res = Double.max(mult, res);
			sum += mult;
		}
		res -= sum * rho;
		return res;
	}
	
	public double slowinskiEval(Solution s, double refPoint[], double lambda[]){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = -Double.MAX_VALUE;
		double sum = 0;
		double mult[] = new double[s.getNumObjectives()];
		for(int i=0; i<s.getNumObjectives(); i++){
			mult[i] = lambda[i] * (s.getObjective(i) - refPoint[i]);
			res = Double.max(mult[i], res);
			sum += mult[i];
		}
		res -= ( 1+sum) / (1 + s.getNumObjectives() * res);

		res += sum * rho;
		return res;
	}
	
	public double lpEval(Solution s, double refPoint[], double lambda[], double p){
		if(null == refPoint){
			refPoint = new double[lambda.length];
			Arrays.fill(refPoint, 0);
		}
		double res = 0;
		double sum = 0;
		double mult[] = new double[s.getNumObjectives()];
		for(int i=0; i<s.getNumObjectives(); i++){
			mult[i] = lambda[i] * (s.getObjective(i) - refPoint[i]);
			res += Math.pow(mult[i], p);
			sum += mult[i];
		}
		res = Math.pow(res, 1/p);
		res += sum * rho;
		return res;
	}

	public Line getAsfLine(){
		return new Line(this.refPoint, this.refPoint.shift(this.vector));
	}
	
	public double[] getLambda() {
		return lambda;
	}
	
	public void setLambda(double[] lambda) {
		this.lambda = lambda;
	}
	
	public double getLambda(int i){
		return lambda[i];
	}
	
	public AsfDM copy() {
		return new AsfDM(this.refPoint.getDim(), this.lambda.clone(), this.name);
	}

	public double getReward(){
		return this.reward;
	}
	
	public void setReward(double reward) {
		this.reward = reward;
	}

	public double getPenalty(){
		return this.penalty;
	}
	
	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	public static double getRho() {
		return AsfDM.rho;
	}
	
	@Override
	public int compare(Solution s1, Solution s2) {
		return Double.compare(eval(s1), eval(s2));
	}
	
	public void sort(ArrayList<Solution> solutions){
		HashMap <Solution, Double> asfValue = new HashMap <Solution, Double>();

		for (Solution s : solutions) {
			asfValue.put(s, this.eval(s.getObjectives()));
		}

		Collections.sort(solutions, new Comparator<Solution>() {
			@Override
			public int compare(final Solution s1, final Solution s2) {
				return Double.compare(asfValue.get(s1), asfValue.get(s2)); // Sort ASCENDING by ASF value
			}
		});
	}
}