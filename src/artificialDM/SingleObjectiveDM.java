package artificialDM;

import algorithm.geneticAlgorithm.solution.DoubleSolution;

public class SingleObjectiveDM extends ArtificialDM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1702586872373803894L;
	
	private int optimizedObjective;
	
	public SingleObjectiveDM(int optObj) {
		optimizedObjective = optObj;
	}

	@Override
	public double eval(double[] obj) {
		return obj[optimizedObjective];
	}
	
	public int getOptimizedObjective(){
		return optimizedObjective;
	}
	
	public void setOptimizedObjective(int optObj){
		optimizedObjective = optObj;
	}
	
	@Override
	public int compare(DoubleSolution s1, DoubleSolution s2) {
		return Double.compare(eval(s1), eval(s2));
	}

	@Override
	public DMType getType() {
		return DMType.SO;
	}
}
